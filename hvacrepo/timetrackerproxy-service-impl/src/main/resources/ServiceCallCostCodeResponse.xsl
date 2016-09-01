<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"
xmlns:w="http://wennsoft.com/webservices/"
xmlns:d="urn:schemas-microsoft-com:xml-diffgram-v1"
exclude-result-prefixes="s w d">
<xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>

<xsl:template match="/">
<ServiceCallCostCodeList>
    <xsl:for-each select="s:Envelope/s:Body/w:GetWSCostCodeListDataResponse/w:GetWSCostCodeListDataResult/d:diffgram/WSCostCodeListData/Table" >
<xsl:if test="WS_Cost_Code">
        <ServiceCallCostCode>
            <CostCode><xsl:value-of select="WS_Cost_Code"/></CostCode>
            <CostCodeDescription><xsl:value-of select="Cost_Code_Description" /></CostCodeDescription>
        </ServiceCallCostCode>
        
    </xsl:if>
</xsl:for-each>
</ServiceCallCostCodeList>
</xsl:template>

</xsl:stylesheet>
