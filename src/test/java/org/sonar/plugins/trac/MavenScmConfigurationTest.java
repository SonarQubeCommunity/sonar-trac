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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

public class MavenScmConfigurationTest {

  private final MavenProject mvnProjectNullScm = new MavenProject();
  private MavenScmConfiguration mvnConf;
  private final MavenScmConfiguration mvnConfNullScm = new MavenScmConfiguration(mvnProjectNullScm);
  private static final String URL = "http://localhost";

  @Before
  public void setUp() {
    MavenProject mvnProject = new MavenProject();
    final IssueManagement issueMgmt = new IssueManagement();
    issueMgmt.setUrl(URL);
    mvnProject.setIssueManagement(issueMgmt);
    mvnConf = new MavenScmConfiguration(mvnProject);
  }
  
  @Test
  public void getIssueManagementUrl_should_return_the_Correct_url() {
    assertEquals(URL, mvnConf.getIssueManagementUrl());
  }
  @Test
  public void getIssueManagementUrl_should_return_null_if_scm_is_null() {
    assertNull(mvnConfNullScm.getIssueManagementUrl());
  }
}
