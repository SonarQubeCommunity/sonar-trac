/*
 * Sonar Trac Plugin
 * Copyright (C) 2010 Thales Optronics Ltd.
 * dev@sonar.codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.sonar.plugins.trac;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Project;

public class TracSensor implements Sensor {

  private static final Logger LOGGER = LoggerFactory.getLogger(TracSensor.class);
  private final Settings settings;
  private final ScmConfiguration configuration;
  
  public TracSensor (ScmConfiguration configuration,final Settings settings){
    this.settings = settings;
    this.configuration = configuration;
  }
  
  public final boolean shouldExecuteOnProject(Project project) {
    // this sensor is executed on any type of project
    return true;
  }

  /**
   * Builds a map of the possible ticket priorities and the number of tickets
   * (active, not closed) for each.
   *
   * @param client valid XmlRpcClient
   * @param distribution PropertiesBuilder<String, Integer> object to build the
   * ticket priority/count distribution
   * @param component optional component to search by
   * @return total count of all active tickets (not closed)
   */
  public final int getPriorityCount(XmlRpcClient client, PropertiesBuilder<String, Integer> distribution, String component) {
    int retVal = 0;

    Object[] params = null;
    Object[] result = null;

    try {
      result = (Object[]) client.execute("ticket.priority.getAll", params);

      if (null == result) {
        LOGGER.error("Trac: null result for priority list query.");
      } else {
        for (Object o : result) {
          if (o instanceof String) {
            String priority = (String) o;

            // For each priority that comes back we need to get a count of the number
            // of tickets that has that priority.
            Object[] priorityResult = null;
            // We only want active tickets.
            // If we don't specify max=0 the number of tickets returned is limited
            // (usually to 100).
            String startQuery = "max=0&status!=closed&priority=" + priority;
            if (null == component) {
              params = new Object[]{startQuery};
            } else {
              params = new Object[]{startQuery + "&component=" + component};
            }

            priorityResult = (Object[]) client.execute("ticket.query", params);

            if (null == priorityResult) {
              LOGGER.error("Trac: null result for priority=" + priority);
            } else {
              retVal += priorityResult.length;
              distribution.add(priority, priorityResult.length);
            }
          } else {
            LOGGER.error("Trac: non string returned o=" + o);
          }
        }
      }

    } catch (XmlRpcException e) {
      LOGGER.warn("Trac: XmlRpcException (possibly missing authentication details?).", e);
    }

    return retVal;
  }

  public final void analyse(Project project, SensorContext sensorContext) {

    final String tracURL = configuration.getUrl();
    // Check to see if we failed to get a Trac instance URL from both the
    // configuration specified in Sonar and the POM.
    if (null == tracURL) {
      LOGGER.warn("Trac: No URL specified, not gathering metrics from Trac.");
    } else {
      // Still try and get username/password from Sonar config.
      String tracUsername = settings.getString(TracPlugin.TRAC_USERNAME_KEY);
      String tracPassword = settings.getString(TracPlugin.TRAC_PASSWORD_KEY);
      String tracTicketComponent = settings.getString(TracPlugin.TRAC_TICKET_COMPONENT_KEY);

      String fullTracUrl = tracURL;

      LOGGER.info("Trac: Connecting to " + tracURL);
      try {
        XmlRpcClientConfigImpl conf = new XmlRpcClientConfigImpl();
        String xmlRPCURL = "/xmlrpc";

        if (null != tracUsername) {
          fullTracUrl = fullTracUrl + "/login" + xmlRPCURL;
          conf.setBasicUserName(tracUsername);
          conf.setBasicPassword(tracPassword);
        } else {
          fullTracUrl = fullTracUrl + xmlRPCURL;
        }

        LOGGER.info("Trac: XML-RPC URL is " + fullTracUrl);
        conf.setServerURL(new URL(fullTracUrl));

        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(conf);

        // Get ticket priorities/count.
        PropertiesBuilder<String, Integer> distribution = new PropertiesBuilder<String, Integer>();
        int totalCount = getPriorityCount(client, distribution, tracTicketComponent);

        Measure issuesMeasure = new Measure(TracMetrics.ISSUES, (double) totalCount);
        issuesMeasure.setUrl(tracURL);
        issuesMeasure.setData(distribution.buildData());
        sensorContext.saveMeasure(issuesMeasure);

        // If the component has been specified we want to save it.
        if (null != tracTicketComponent) {
          sensorContext.saveMeasure(new Measure(TracMetrics.TICKET_COMPONENT, tracTicketComponent));
        }
      } catch (MalformedURLException e) {
        LOGGER.error("Trac: MalformedURLException: " + tracURL, e);
      }

    }
  }
}
