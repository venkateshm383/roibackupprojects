<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0"  indent="yes"/>
<xsl:strip-space elements="*" />
<xsl:template match="/">
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://wennsoft.com/webservices/">
  <soapenv:Header/>
  <soapenv:Body>
      <web:GetWSCostCodeListData>
         <!--Optional:-->
         <web:connectionString><xsl:value-of select="ServiceData/ConnectionString" /></web:connectionString>
      </web:GetWSCostCodeListData>
   </soapenv:Body>
</soapenv:Envelope>
</xsl:template>
</xsl:stylesheet>
