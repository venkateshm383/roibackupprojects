<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="utf-16"/>
	<xsl:template match="/">	
		<DATA2SC>
			<xsl:attribute name="PIN"><xsl:value-of select="DATA2SC/PIN" /></xsl:attribute>
			<xsl:attribute name="ID"><xsl:value-of select="DATA2SC/ID" /></xsl:attribute>
			<CALL>
				<xsl:attribute name="TYPE"><xsl:value-of select="DATA2SC/CALL/TYPE" /></xsl:attribute>
				<xsl:attribute name="DATETIME"><xsl:value-of
					select="DATA2SC/CALL/DATETIME" /></xsl:attribute>
				<xsl:attribute name="OPERATOR"><xsl:value-of
					select="DATA2SC/CALL/OPERATOR" /></xsl:attribute>
				<xsl:attribute name="CALLER"><xsl:value-of
					select="DATA2SC/CALL/CALLER" /></xsl:attribute>
				<xsl:attribute name="CATEGORY"><xsl:value-of
					select="DATA2SC/CALL/CATEGORY" /></xsl:attribute>
				<xsl:attribute name="SUB"><xsl:value-of select="DATA2SC/CALL/SUB" /></xsl:attribute>
				<xsl:attribute name="LOC"><xsl:value-of select="DATA2SC/CALL/LOC" /></xsl:attribute>
				<xsl:attribute name="TRADE"><xsl:value-of select="DATA2SC/CALL/TRADE" /></xsl:attribute>
				<xsl:attribute name="PRO"><xsl:value-of select="DATA2SC/CALL/PRO" /></xsl:attribute>
				<xsl:attribute name="PRO_NAME"><xsl:value-of
					select="DATA2SC/CALL/PRO_NAME" /></xsl:attribute>
				<xsl:attribute name="TR_NUM"><xsl:value-of
					select="DATA2SC/CALL/TR_NUM" /></xsl:attribute>
				<xsl:attribute name="WO_NUM"><xsl:value-of
					select="DATA2SC/CALL/WO_NUM" /></xsl:attribute>
				<xsl:attribute name="PO_NUM"><xsl:value-of
					select="DATA2SC/CALL/PO_NUM" /></xsl:attribute>
				<xsl:attribute name="STATUS"><xsl:value-of
					select="DATA2SC/CALL/STATUS" /></xsl:attribute>
				<xsl:attribute name="PRIORITY"><xsl:value-of
					select="DATA2SC/CALL/PRIORITY" /></xsl:attribute>
				<xsl:attribute name="EQP_ID"><xsl:value-of
					select="DATA2SC/CALL/EQP_ID" /></xsl:attribute>
				<xsl:attribute name="PRICE"><xsl:value-of
					select="DATA2SC/CALL/PRICE" /></xsl:attribute>
				<xsl:attribute name="TAX"><xsl:value-of
					select="DATA2SC/CALL/TAX" /></xsl:attribute>
				<xsl:attribute name="TAX2"><xsl:value-of
					select="DATA2SC/CALL/TAX2" /></xsl:attribute>
				<xsl:attribute name="NTE"><xsl:value-of select="DATA2SC/CALL/NTE" /></xsl:attribute>
				<xsl:attribute name="CURRENCY"><xsl:value-of select="DATA2SC/CALL/CURRENCY" /></xsl:attribute>
				<xsl:attribute name="SCHED_DATETIME"><xsl:value-of
					select="DATA2SC/CALL/SCHED_DATETIME"/></xsl:attribute>
				<xsl:attribute name="RECALL"><xsl:value-of
					select="DATA2SC/CALL/RECALL" /></xsl:attribute>
				<PROBLEM>
					<xsl:value-of select="DATA2SC/CALL/PROBLEM" />
				</PROBLEM>
				<ATTR>
				<xsl:attribute name="NAME"><xsl:value-of select="DATA2SC/CALL/ATTR/NAME" /></xsl:attribute>
				<xsl:attribute name="LINE"><xsl:value-of select="DATA2SC/CALL/ATTR/LINE" /></xsl:attribute>
				<xsl:attribute name="DATETIME"><xsl:value-of select="DATA2SC/CALL/ATTR/DATETIME" /></xsl:attribute>
				<xsl:attribute name="CREATED_BY"><xsl:value-of select="DATA2SC/CALL/ATTR/CREATED_BY" /></xsl:attribute>
				<xsl:attribute name="NEW_SCHED_DATETIME"><xsl:value-of select="DATA2SC/CALL/ATTR/NEW_SCHED_DATETIME" /></xsl:attribute>
					<xsl:value-of select="DATA2SC/CALL/ATTR/text()" />
				</ATTR>
			</CALL>
		</DATA2SC>
	</xsl:template>
</xsl:stylesheet>