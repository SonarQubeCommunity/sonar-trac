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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.config.Settings;

public class ScmConfiguration implements BatchExtension {

  private final Settings settings;
  private final MavenScmConfiguration mavenConfiguration;
  private final Supplier<String> url;

  public ScmConfiguration(Settings settings, MavenScmConfiguration mavenConfiguration) {
    this.settings = settings;
    this.mavenConfiguration = mavenConfiguration;
    url = Suppliers.memoize(new IssueManagementUrlSupplier());
  }

  public ScmConfiguration(Settings settings) {
    this(settings,null /** not in maven environment*/);
  }

  public String getUrl() {
    return url.get();
  }

  private class IssueManagementUrlSupplier implements Supplier<String> {

    public String get() {
      String mavenUrl = getMavenUrl();
      if (!StringUtils.isBlank(mavenUrl)) {
        return mavenUrl;
      }

      String urlPropertyFromTracSettings = settings.getString(TracPlugin.TRAC_URL_KEY);
      if (!StringUtils.isBlank(urlPropertyFromTracSettings)) {
        return urlPropertyFromTracSettings;
      }

      return null;
    }

    private String getMavenUrl() {
      if (mavenConfiguration == null) {
        return null;
      }
      return mavenConfiguration.getIssueManagementUrl();
    }
  }
}
