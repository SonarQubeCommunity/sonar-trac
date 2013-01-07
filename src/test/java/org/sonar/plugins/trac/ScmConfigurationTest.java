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

import org.apache.maven.model.IssueManagement;
import org.apache.maven.project.MavenProject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.sonar.api.config.Settings;

public class ScmConfigurationTest {
  private final Settings settings = new Settings();
  private static final String URL = "http://localhost";

  @Test
  public void testMavenConfiguration() {
    
    MavenProject mvnProject = new MavenProject();
    final IssueManagement issueMgmt = new IssueManagement();
    issueMgmt.setUrl(URL);
    mvnProject.setIssueManagement(issueMgmt);
    MavenScmConfiguration mavenConfonfiguration = new MavenScmConfiguration(mvnProject);
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings, mavenConfonfiguration);
    
    assertThat ( scmConfiguration.getUrl() , is(URL));
  }

  @Test
  public void testNonMavenConfiguration() {
    
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    
    assertNull ( scmConfiguration.getUrl());
  }

  @Test
  public void testConfigurationOfTracPlugin() {
     settings.setProperty(TracPlugin.TRAC_URL_KEY, URL);
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    
    assertThat ( scmConfiguration.getUrl() , is(URL));
  }
}
