

SonarQube Trac  Plugin
==========================
Download and Version information: http://update.sonarsource.org/plugins/trac-confluence.html

## Build Status
[![Build Status](https://sonarplugins.ci.cloudbees.com/job/trac/buildStatus/icon?job=check-manifest)](https://sonarplugins.ci.cloudbees.com/job/trac)

## Description / Features
This plugin uses the Trac XML-RPC plugin to connect to a Trac (http://trac.edgewall.org/) instance and display metrics about open tickets. It can also drill down to the component level. 

## Installation
1.Install the plugin through the Update Center or download it into the SONARQUBE_HOME/extensions/plugins directory
2.Restart the SonarQube server

## Usage
Install and enable the Trac [XML-RPC plugin](http://trac-hacks.org/wiki/XmlRpcPlugin) on your Trac instance. You will need to give anonymous or a user account 'XML_RPC' and 'TICKET_VIEW' privileges.
A user working with trac 0.11.7 has reported on the user mailing-list that the [HttpAuthPlugin](http://trac-hacks.org/wiki/HttpAuthPlugin) should be also installed, so if you get the following error Trac: XmlRpcException (possibly missing authentication details?) install it as well.

The Trac instance URL can be specified in two places:

Your project's pom.xml file under the 'issue managment' section; for example: (note that the username/password/component has to be specified in SonarQube project settings, the plugin does not currently have the ability to read the username/password/component from the pom.xml)
<issueManagement>
    <system>Trac</system>
    <url>http://trac_server/projects/project_name</url>
</issueManagement>

Specified in SonarQube under the project settings.

Username/password/component name should also be specified here.

Note: component name is completely optional, if you don't specify one you will get the number of tickets over the whole instance. There is also no validation of the component name, so if you specify a non-existent component name, no tickets will be found.

## Analysis
Run an analysis (see [Run Analysis](https://github.com/SonarCommunity/sonar-doxygen/wiki))

## Known Limitations
While the Trac instance URL can be picked up from your project's pom.xml file the username/password/component have to be specified within the project settings in SonarQube

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
