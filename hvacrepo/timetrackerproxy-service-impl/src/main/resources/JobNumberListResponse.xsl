<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"
xmlns:w="http://wennsoft.com/webservices/"
xmlns:d="urn:schemas-microsoft-com:xml-diffgram-v1"
exclude-result-prefixes="s w d">
<xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>

<xsl:template match="/">
<JobNumberList>
    <xsl:for-each select="s:Envelope/s:Body/w:GetJobNumberListDataResponse/w:GetJobNumberListDataResult/d:diffgram/JobNumberListData/Table" >
<xsl:if test="Job_Number">
        <JobNumber>
            <JobId><xsl:value-of select="Job_Number"/></JobId>
            <JobDisplay><xsl:value-of select="JobDisplay" /></JobDisplay>
        </JobNumber>
        
    </xsl:if>
</xsl:for-each>
</JobNumberList>
</xsl:template>

</xsl:stylesheet>
