						ROI-MESH-AND-FEATURES-REPOSITORY
						================================
										
Project Overview:
=================
	This topic will cover the overview of the projects. We have 4 core project for framework. Other projects are the features developed over framework.

I) Framework:
==============

a) “roi-framework” project: 
---------------------------

•	We have written a pure java based core framework that has a service class for adding, getting, updating, enabling or disabling feature/service, deleting the configuration in database and cache. 
•	It supports eventing, dynastore, permastore, policy and feature Implementation configuration and can perform above specified operation using its respective service class.

b) “mesh-core” project: 
-----------------------
•	we have written a camel base core framework. All camel related operations are done here.
•	It contains a “baseRoute.xml” which is an entry point for our application. Execution route, Implementation Route of feature are imported in “baseRoute.xml”.
•	This project is responsible for initializing Mesh Header with request context object, feature group, feature, service, hazelcast transactional context object etc. Also responsible for generating unique id for each request.
•	It is also used to route from base route to execution route based on featuregroup and feature name. Route from execution route to Implementation route by loading specific feature, checking if service is enable for the feature service then get the implementation route associated with it.
•	It is used to call camel notifier at the end of the camel context which is responsible for dispatching component, service, system events configured for that feature and service.

c) “features-installer” project(non-osgi): 
------------------------------------------
We have written a feature installer for non-osgi environment whose responsibility is to load all the features configuration (configuration like event configuration, feature Implementation configuration etc.) in database and cache before starting the application.

d) “feature-extender” project(osgi):
-----------------------------------
We have written a feature extender for osgi environment whose responsibility is to load all the features configuration (configuration like event configuration, feature Implementation configuration etc.) in database and cache when a feature bundle is install and remove the configuration if feature bundle is removed.

e) “MultipleEndpointCustomPlugin” project:
-------------------------------------------
We have written a custom maven plugin to generate “soap endpoint” and its “route definition” for the feature. 
•	It has to be added to feature project as plugin in pom.xml. During the build time, the plugin read the “featureservice.xml” file, get the data required to create “endpoint” and its “route definition”.
•	Once “endpoint” and “route definition” is created, write it into “*Execution.xml” available in “resources/META-INF/spring” folder of the project where plugin is added in pom.xml. 
•	Plugin should be added to all the feature project which support “soap endpoint”.

II) Features:
==============
a) “roi-labelService” project:
------------------------------
We have written a code which will generated the batchid, add label template by generating labelId when a request is made for add label and create an actual value substituted label at produce label request and store it in dynastore against generated batchid, triggers another feature to print label by passing batchid. 

b) “roi-label-niceLabelService” project:
----------------------------------------
We have written a code which will generated the batchid, add label template and labelId when a request is made for add label and create an actual value substituted label at produce label request and store it in dynastore against generated batchid, triggers another feature to print label by passing batchid and printer id. 

c) “roi-printservice-printnode” project:
---------------------------------------
We have written a code which get the batchid from request data, get the label data from the dynastore using batchid as key and send to create a “printJobId” to printing server. Also used to get status of “printJobId” for print node server

d) “roi-printservice-cups” project: 
----------------------------------- 
We have written a code which get the batchid from request data, get the label data from the dynastore using batchid as key and send to create a “printJobId” to printing server. Also used to get status of “printJobId” for cups printer.


Note : 
------
1)You will get Database schema and SQL statement script inside "docs/database" folder.
2)Installation Guide for mesh is available in "docs/meshGuide" folder.


Steps to get Oracle Driver dependency:
--------------------------------------
As oracle driver is not available in maven repository due to licensing problem. So we need to make is available in local m2. To do that follow the steps given below :
I) If jar available in lib folder:
1.1)Got to the  lib/meshRequireJar directory from terminal.
1.2) Test maven 3.3.9 is setup in your machine using the command given below :
           mvn -version
You will get the information related to maven.If not installed,Please install maven to your system first.
1.3) To make oracle jar to be available in local maven, use the command given below in terminal.
		mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.2.0 -Dpackaging=jar

II) If jar is unavailable in lib folder:
2.1) To download we should have our account with oracle. You can register from "http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html"
2.2) download the ojdbc jar from link given "http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html". Based on the version of oracle installed on the machine ( code is tested with 11.2.0.2.0)
2.3) Go to the specified location where ojdbc.jar is downloaded. From command line issues following command :
  mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.2.0 -Dpackaging=jar
	         
Steps to get MS SQL maven dependency 
---------------------------------------
I) If jar available in lib folder:
1.1)Got to the  lib/meshRequireJar directory from terminal.
1.2) Test maven 3.3.9 is setup in your machine using the command given below :
           mvn -version
You will get the information related to maven.If not installed,Please install maven to your system first.
1.3) To make oracle jar to be available in local maven, use the command given below in terminal.
		mvn install:install-file -Dfile=sqljdbc41.jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc41 -Dversion=4.0 -Dpackaging=jar

II) If jar is unavailable in lib folder:
2.1) Download the driver from http://www.microsoft.com/download/en/details.aspx?displaylang=en&id=11774 (zip/tar)
2.2) Extract it and go the location where you can see sqljdbc/sqljdbc4 jar.
2.3) add jar to local maven using the command given below:
mvn install:install-file -Dfile=sqljdbc41.jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc41 -Dversion=4.0 -Dpackaging=jar

note:  added maven dependency in pom :
<dependency>
  <groupId>com.microsoft.sqlserver</groupId>
  <artifactId>sqljdbc41</artifactId>
  <version>4.0</version>
</dependency>


Note : We have added new sql file with vendor support which is availabel in docs/database folder . we have upgraded the sql file to version 1.1.

						
						




