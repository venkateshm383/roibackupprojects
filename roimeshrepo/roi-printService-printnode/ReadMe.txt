“roi-printservice-printnode" project:
================================
We have written a code which get the batchid from request data, get the label data from the dynastore using batchid as key and send to create a “printJobId” to printing server. Also used to get status of “printJobId” for print node server
•	It is created on top of core framework “roi-framework” and “mesh-core”. It is printnode implemented print service.
•	Feature project must contain a xml file name ‘featureMetaInfo.xml” into resource folder which defines all the resource (event, featureimpl, permastore etc.) supported by feature.
•	Event xml file configure the channel, system event, component event and service event for the feature. featureImpl xml to specify the service supported by feature, endpoints where these service are exposed and implementation route name associated with service.
•	This feature is triggered either from “roi-labelservice” or “roi-label-niceLabelService” as an event. Its responsibility is to generate “printJobId” for the label store in dynastore for key as batchid. And Once “printJobId” is generated get the job status.
•	To connect to printnode service, require printnode api key.


PrintNode Server Setup:
======================
PrintNode server is require for printing label. Create an account if you don’t have. Steps to get authentication token is given below.
•	Signup from the url given below “https://app.printnode.com/account/login”.
•	On login, click on the “API” tab to generate the api authentication key. Click on “Make New Api Key” tab to get new key.
•	Enter the description and click on save.
•	If already a user, you can login inside, click on the “API” tab to get the api authentication key. Click on “Make New Api Key” tab to get new key if you don’t have else you can copy from the list.


Changes we need to do before testing feature :
=============================================
To test “roi-framework” with feature (roi-printService-printnode) you have to follow these steps:
a)	In  “printservice-printnode-eventing.xml”available in resources folder of project “roi-printservice-printnode”, we have configured File dispatcher channel and provided the local file system path. You need to change it according your suitable file location.
b)	In “roi-printservice-printnode” project “resources” folder for the file “printNodeProperty.properties” changes the “PRINTNODE_TOKEN” value with one you get during “PrintNode server Setup”.
c)	First maven build custom plugin called “MultipleEndpointCustomPlugin”.
d)	Maven install project “roi-printservice-printnode”.

How to build the aplication:
===========================
1) From eclipse or fuse IDE:
-------------------------
 Right click On project "pom.xml" .Run "mvn clean" followed by "mvn install".
 
 2) From Command line :
 ----------------------
 From command line go till project base directory i.e "roi-printservice-printnode" location. Enter command squencly
 
	2.1) to build project:
	          "mvn clean install"
    2.2) to clean old jars available in classpath or to clean eclipse environment
	          "mvn eclipse:clean"
    2.3) to recreate eclipse environment
	         "mvn eclipse:eclipse"

