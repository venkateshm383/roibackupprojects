<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"
xmlns:w="http://wennsoft.com/webservices/"
xmlns:d="urn:schemas-microsoft-com:xml-diffgram-v1"
exclude-result-prefixes="s w d">
<xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>

<xsl:template match="/">
<TransactionTypeList>
<xsl:for-each select="s:Envelope/s:Body/w:GetTransactionTypeListDataResponse/w:GetTransactionTypeListDataResult/d:diffgram/TransactionTypeListData/Table" >
    <xsl:if test="WS_Transaction_Type">
        <TransactionType><xsl:value-of select="WS_Transaction_Type"/></TransactionType>
    </xsl:if>

</xsl:for-each>
</TransactionTypeList>
</xsl:template>

</xsl:stylesheet>
