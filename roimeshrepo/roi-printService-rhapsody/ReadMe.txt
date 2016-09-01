“roi-printservice-rhapsody" project:
================================
We have written a code which get the batchid from request data, get the label data from the dynastore using batchid as key and send to create a “printJobId” to printing server. Also used to get status of “printJobId” for print node server
•	It is created on top of core framework “roi-framework” and “mesh-core”. It is rhapsody implemented print service.
•	Feature project must contain a xml file name ‘featureMetaInfo.xml” into resource folder which defines all the resource (event, featureimpl, permastore etc.) supported by feature.
•	Event xml file configure the channel, system event, component event and service event for the feature. featureImpl xml to specify the service supported by feature, endpoints where these service are exposed and implementation route name associated with service.
•	This feature is triggered either from “roi-labelservice” or “roi-label-niceLabelService” or “roi-label-bartenderService”  as an event. Its responsibility is to generate “printJobId” for the label store in dynastore for key as batchid. And Once “printJobId” is generated get the job status.
•	To connect to rhapsody service, require rhapsody-server userCredentials.


Rhapsody Server Setup:
======================
Rhapsody server is require for printing label. Create user if you don’t have. Steps to get authentication is given below.
•	Once the Rhapsody server is up and running, and syncd with the webUI, we can access the UI in url http://hostname:8080/.
•	To login, you have to enter the username and password which has been choosen on installation.


Changes we need to do before testing feature :
=============================================
To test “roi-framework” with feature (roi-printService-rhapsody) you have to follow these steps:
a)	In  “printservice-rhapsody-eventing.xml”available in resources folder of project “roi-printservice-rhapsody”, we have configured File dispatcher channel and provided the local file system path. You need to change it according your suitable file location.
b)	In “roi-printservice-rhapsody” project “resources” folder for the file “RHAPOSDY_CONNECTION.properties” changes the “Credentials” value with one you get during “Rhapsody server Setup”.
c)	First maven build custom plugin called “MultipleEndpointCustomPlugin”.
d)	Maven install project “roi-printservice-rhapsody”.

How to build the aplication:
===========================
1) From eclipse or fuse IDE:
-------------------------
 Right click On project "pom.xml" .Run "mvn clean" followed by "mvn install".
 
 2) From Command line :
 ----------------------
 From command line go till project base directory i.e "roi-printservice-rhapsody" location. Enter command squencly
 
	2.1) to build project:
	          "mvn clean install"
    2.2) to clean old jars available in classpath or to clean eclipse environment
	          "mvn eclipse:clean"
    2.3) to recreate eclipse environment
	         "mvn eclipse:eclipse"

