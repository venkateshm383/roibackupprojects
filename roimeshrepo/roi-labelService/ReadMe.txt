"roi-labelService" Project
======================
We have written a code which will generated the batchid, add label template by generating labelId when a request is made for add label and create an actual value substituted label at produce label request and store it in dynastore against generated batchid, triggers another feature to print label by passing batchid. 
•	It is created on top of core framework “roi-framework” and “mesh-core”. It is roi implemented label service.
•	Feature project must contain a xml file name ‘featureMetaInfo.xml” into resource folder which defines all the resource (event, featureimpl, permastore etc.) supported by feature.
•	Event xml file configure the channel, system event, component event and service event for the feature. Permstore xml file contain configuration to store data required by application in permastore. featureImpl xml to specify the service supported by feature, endpoints where these service are exposed and implementation route name associated with service.
•	Label Template, Printer config loaded from text file to permastore and on service call template or printer details taken from permastore instead of loading it from text file
•	Services supported by label service are “startlabel” – which returns batchid, “addlabel” which returns you success message if added successfully. It can be called any number of time. “producelabel” which returns all label in concatenated with value replaced for a batchid. “voidlabel” which trigger another feature as event using rest http using “Post” with JSON as the payload.

Note: “voidlabel” service is not mean to call printservice instead it supposed to empty the cache if called. But here we just want to show how one feature can call another through event.

Changes need to do Before Testing this feature :
================================================
To test “roi-framework” with feature (roi-labelService) you have to follow these steps:
a)	In “labelservice-roi-eventing.xml” available in resources folder of project “roi-labelservice” , we have configured File dispatcher channel and provided the local file system path. You need to change it according your suitable file location.
b)	You need to change the path of template file available in “TEMPLATE_LIST.properties”.
c)	Make sure “cxf endpoint” and its “route definition” is not available in labelServiceExecution.xml. 
d)	If available, remove “cxf-endpoint” declaration and its “route definition” form labelServiceExecution.xml but not the declaration of <cxf:bus>.
e)	First maven build custom plugin called “MultipleEndpointCustomPlugin”.

How to build the aplication:
===========================
1) From eclipse or fuse IDE:
-------------------------
 Right click On project "pom.xml" .Run "mvn clean" followed by "mvn install".
 
 2) From Command line :
 ----------------------
 From command line go till project base directory i.e "roi-labelService" location. Enter command squencly
 
	2.1) to build project:
	          "mvn clean install"
    2.2) to clean old jars available in classpath or to clean eclipse environment
	          "mvn eclipse:clean"
    2.3) to recreate eclipse environment
	         "mvn eclipse:eclipse"



