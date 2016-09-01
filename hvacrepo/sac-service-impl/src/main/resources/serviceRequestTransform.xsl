<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="utf-16"/>
	<xsl:template match="/">
		<ServiceRequest>
			<RequestSource>
				<Source>ZenDesk-ServiceChanel</Source>
				<ProviderIdentification>
					<xsl:value-of select="DATA2SC/@PIN" />
				</ProviderIdentification>
				<ProviderNumber>
					<xsl:value-of select="DATA2SC/CALL/@PRO" />
				</ProviderNumber>
				<ProviderName>
					<xsl:value-of select="DATA2SC/CALL/@PRO_NAME" />
				</ProviderName>
				<TransactionNumber>
					<xsl:value-of select="DATA2SC/CALL/@TR_NUM" />
				</TransactionNumber>
				<ServiceRequestId>
					<xsl:value-of select="DATA2SC/@ID" />
				</ServiceRequestId>
			</RequestSource>
			<RequestDetail>
				<xsl:attribute name="Status"><xsl:value-of
					select="DATA2SC/CALL/@TYPE" /></xsl:attribute>
				<xsl:attribute name="DateTime"><xsl:value-of
					select="DATA2SC/CALL/@DATETIME" /></xsl:attribute>
				<ProblemType>
					<xsl:value-of select="DATA2SC/CALL/@CATEGORY" />
				</ProblemType>
				<ProblemArea>
					<xsl:value-of select="DATA2SC/CALL/@TRADE" />
				</ProblemArea>
				<ProblemDescription>
					<xsl:value-of select="DATA2SC/CALL/PROBLEM" />
				</ProblemDescription>
				<ScheduledDTM>
					<xsl:value-of select="DATA2SC/CALL/@SCHED_DATETIME" />
				</ScheduledDTM>
				<WOStatus>
					<xsl:value-of select="DATA2SC/CALL/@STATUS" />
				</WOStatus>
				<WOPriority>
					<xsl:value-of select="DATA2SC/CALL/@PRIORITY" />
				</WOPriority>
				<CreatedBy>
					<xsl:value-of select="DATA2SC/CALL/@OPERATOR" />
				</CreatedBy>
				<RequestedBy>
					<xsl:value-of select="DATA2SC/CALL/@CALLER" />
				</RequestedBy>
				<SUB>
				<xsl:value-of select="DATA2SC/CALL/@SUB"/>
				</SUB>
				<WOLocation>
					<xsl:value-of select="DATA2SC/CALL/@LOC" />
				</WOLocation>
				<EquipmentId>
					<xsl:value-of select="DATA2SC/CALL/@EQP_ID" />
				</EquipmentId>
				<WONumber>
					<xsl:value-of select="DATA2SC/CALL/@WO_NUM" />
				</WONumber>
				<WOPO>
					<xsl:value-of select="DATA2SC/CALL/@PO_NUM" />
				</WOPO>
				<WOPrice>
					<xsl:value-of select="DATA2SC/CALL/@PRICE" />
				</WOPrice>
				<WOTax>
					<xsl:value-of select="DATA2SC/CALL/@TAX" />
				</WOTax>
				<WOTaxAditional>
					<xsl:value-of select="DATA2SC/CALL/@TAX2" />
				</WOTaxAditional>
				<WONTE>
					<xsl:value-of select="DATA2SC/CALL/@NTE" />
				</WONTE>
				<WOCurrency>
					<xsl:value-of select="DATA2SC/CALL/@CURRENCY" />
				</WOCurrency>
				<ISRecall>
					<xsl:value-of select="DATA2SC/CALL/@RECALL" />
				</ISRecall>
				<ATTR>
					<NAME><xsl:value-of select="DATA2SC/CALL/ATTR/@NAME" /></NAME>
					<LINE><xsl:value-of select="DATA2SC/CALL/ATTR/@LINE" /></LINE>
					<DATETIME><xsl:value-of select="DATA2SC/CALL/ATTR/@DATETIME" /></DATETIME>
					<CREATED_BY><xsl:value-of select="DATA2SC/CALL/ATTR/@CREATED_BY" /></CREATED_BY>
					<NEW_SCHED_DATETIME><xsl:value-of select="DATA2SC/CALL/ATTR/@NEW_SCHED_DATETIME" /></NEW_SCHED_DATETIME>
					<xsl:value-of select="DATA2SC/CALL/ATTR/text()" />
				</ATTR>
			</RequestDetail>
		</ServiceRequest>
	</xsl:template>
</xsl:stylesheet>