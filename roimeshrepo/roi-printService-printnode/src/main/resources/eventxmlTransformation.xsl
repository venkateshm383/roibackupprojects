<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" />
	<xsl:template match="/">

<Event>
<name>
	<xsl:copy>
	<xsl:apply-templates select="ROIEvent/event/@id"></xsl:apply-templates>
	</xsl:copy>
	</name>
	
	<tenant>
	<xsl:copy>
	<xsl:apply-templates select="ROIEvent/tenant/@tid"></xsl:apply-templates>
	</xsl:copy>
</tenant>	

<site>
	<xsl:copy>
	<xsl:apply-templates select="ROIEvent/site/@sid"></xsl:apply-templates>
	</xsl:copy>
</site>

<featuregroup>
	<xsl:copy>
	<xsl:apply-templates select="ROIEvent/featuregroup/@groupid"></xsl:apply-templates>
	</xsl:copy>
</featuregroup>

<feature>
	<xsl:copy>
	<xsl:apply-templates select="ROIEvent/feature/@featureid"></xsl:apply-templates>
	</xsl:copy>
</feature>
	
	<paramList>
	

 <xsl:template match="/">
<xsl:for-each select="ROIEvent/EventParams/eventparam">
  <list><xsl:value-of select="node()" /></list>
</xsl:for-each> 
</xsl:template>
	</paramList>
</Event>
	</xsl:template>

</xsl:stylesheet>