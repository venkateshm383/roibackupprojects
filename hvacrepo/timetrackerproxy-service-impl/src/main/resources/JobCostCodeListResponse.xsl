<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"
xmlns:w="http://wennsoft.com/webservices/"
xmlns:d="urn:schemas-microsoft-com:xml-diffgram-v1"
exclude-result-prefixes="s w d">
<xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>

<xsl:template match="/">
<JobCostCodeList>
    <xsl:for-each select="s:Envelope/s:Body/w:GetCostCodeListDataResponse/w:GetCostCodeListDataResult/d:diffgram/CostCodeListData/Table" >
<xsl:if test="Cost_Code">
        <JobCostCode>
            <CostCode><xsl:value-of select="Cost_Code"/></CostCode>
            <CostCodeDescription><xsl:value-of select="Cost_Code_Description" /></CostCodeDescription>
        </JobCostCode>
        
    </xsl:if>
</xsl:for-each>
</JobCostCodeList>
</xsl:template>

</xsl:stylesheet>
