<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"
xmlns:w="http://wennsoft.com/webservices/"
xmlns:d="urn:schemas-microsoft-com:xml-diffgram-v1"
exclude-result-prefixes="s w d">
<xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>

<xsl:template match="/">

<ServiceCallList>    
<xsl:for-each select="s:Envelope/s:Body/w:GetServiceCallListDataResponse/w:GetServiceCallListDataResult/d:diffgram/ServiceCallListData/Table" >

<xsl:if test="Service_Call_ID">
        <ServiceCall>
            <ServiceCallId><xsl:value-of select="Service_Call_ID" /></ServiceCallId>
            <ServiceDescription><xsl:value-of select="Service_Description" /></ServiceDescription>
        </ServiceCall>
    </xsl:if>

</xsl:for-each>
</ServiceCallList> 
</xsl:template>

</xsl:stylesheet>
