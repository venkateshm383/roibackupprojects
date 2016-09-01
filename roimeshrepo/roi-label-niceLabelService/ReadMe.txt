"roi-label-niceLabelService" Project
======================
We have written a code which will generated the batchid, add label template and labelId when a request is made for add label and create an actual value substituted label at produce label request and store it in dynastore against generated batchid, triggers another feature to print label by passing batchid and printer id. 
•	It is created on top of core framework “roi-framework” and “mesh-core”. It is nice-label implemented label service.
•	Feature project must contain a xml file name ‘featureMetaInfo.xml” into resource folder which defines all the resource (event, featureimpl, permastore etc.) supported by feature.
•	Event xml file configure the channel, system event, component event and service event for the feature. featureImpl xml to specify the service supported by feature, endpoints where these service are exposed and implementation route name associated with service.
•	Services supported by label service are “startlabel” – which returns batchid, “addlabel” which returns you success message if added successfully. It can be called any number of time. “producelabel” which returns all label in concatenated with value replaced for a batchid then trigger another feature as event using rest http using “Post” with JSON as the payload.



How to build the aplication:
===========================
1) From eclipse or fuse IDE:
-------------------------
 Right click On project "pom.xml" .Run "mvn clean" followed by "mvn install".
 
 2) From Command line :
 ----------------------
 From command line go till project base directory i.e "roi-label-niceLabelService" location. Enter command squencly
 
	2.1) to build project:
	          "mvn clean install"
    2.2) to clean old jars available in classpath or to clean eclipse environment
	          "mvn eclipse:clean"
    2.3) to recreate eclipse environment
	         "mvn eclipse:eclipse"



