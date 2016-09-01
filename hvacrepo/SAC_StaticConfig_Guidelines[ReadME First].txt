Procedures you need to follow to fetch the files from the directory, where your all the staticfiles are stored ....eg: xsl,ftl files only for the time being (not the pipeconfiguration files)

1. In roi-framework you need to edit the property file [StaticConfigProperties.properties
    - inside it there is one property defined named "staticConfigDirectory", set the value according to your file system path

2. Now go to src/test/java -> com.getusroi.staticconfig.Impl -> TestStaticConfigImpl.java
	You will find two methods run the first method which is testAddingConfigFile(). 
	Hereafter your directory will be generated now go to your base directory which you set in the property file, 
	you will see a new directory has been created like "gap/site1/sacGroup/sac/key2act/1.0" Inside it one .xsl file is generated.

3. Delete the file and store all the files from your classpath, which are your xsl(s) and ftl(s) into the newly created directory.

4. From now onwards, what will happen is, your ftl filename or your xsl filename mentioned in the pipeline configuration would map to the file stored in the directory that you just created 


NOTE :  "gap/site1/sacGroup/sac/key2act/1.0" this is based on "tenantID/SiteID/FeatureGroup/FeatureName/VendorName/Version"
Since now we are using the above for the time being I have hardcoded the Configuration Context, which would change according to the Requirement
