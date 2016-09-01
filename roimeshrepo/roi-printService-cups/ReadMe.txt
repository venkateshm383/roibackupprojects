“roi-printservice-cups” project:
================================

We have written a code which get the batchid from request data, get the label data from the dynastore using batchid as key and send to create a “printJobId” to printing server. Also used to get status of “printJobId” for cups printer.
•	It is created on top of core framework “roi-framework” and “mesh-core”. It is cups implemented print service.
•	Feature project must contain a xml file name ‘featureMetaInfo.xml” into resource folder which defines all the resource (event, featureimpl, permastore etc.) supported by feature.
•	Event xml file configure the channel, system event, component event and service event for the feature. featureImpl xml to specify the service supported by feature, endpoints where these service are exposed and implementation route name associated with service.
•	This feature is triggered either from “roi-labelservice” or “roi-label-niceLabelService” as an event. Its responsibility is to generate “printJobId” for the label store in dynastore for key as batchid. And Once “printJobId” is generated get the job status.



Changes we need to do before testing feature :
=============================================
Cups jar is not available as maven dependent therefor, download cups4j jar of version "0.6.4" and convert is into maven before building this project.This can be done as shown below:

mvn install:install-file -Dfile=<filePath of jar> -DgroupId=print -DartifactId=cups4j -Dversion=0.6.4 -Dpackaging=jar
Where: <filePath of jar> : for example \home\user\downloads\cups4j-0.6.4.jar

To test “roi-framework” with feature (roi-printService-cups) you have to follow these steps:
a)	In  “printservice-cups-eventing.xml”available in resources folder of project “roi-printservice-cups", we have configured File dispatcher channel and provided the local file system path. You need to change it according your suitable file location.

b)	Maven install project “roi-printservice-cups.

How to build the aplication:
===========================
1) From eclipse or fuse IDE:
-------------------------
 Right click On project "pom.xml" .Run "mvn clean" followed by "mvn install".
 
 2) From Command line :
 ----------------------
 From command line go till project base directory i.e "roi-printservice-cups" location. Enter command squencly
 
	2.1) to build project:
	          "mvn clean install"
    2.2) to clean old jars available in classpath or to clean eclipse environment
	          "mvn eclipse:clean"
    2.3) to recreate eclipse environment
	         "mvn eclipse:eclipse"

