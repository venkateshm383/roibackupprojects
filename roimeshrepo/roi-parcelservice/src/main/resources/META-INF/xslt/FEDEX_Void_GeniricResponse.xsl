<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:v="http://fedex.com/ws/ship/v17" exclude-result-prefixes="SOAP-ENV v">
	<xsl:output method="xml" />
	<xsl:template match="/">

		<VoidReply>
			<Notification>
				<xsl:value-of
					select="SOAP-ENV:Envelope/SOAP-ENV:Body/v:ShipmentReply/v:HighestSeverity" />
			</Notification>
			<Code>
				<xsl:value-of
					select="SOAP-ENV:Envelope/SOAP-ENV:Body/v:ShipmentReply/v:Notifications/v:Code" />
			</Code>
			<Message>
				<xsl:value-of
					select="SOAP-ENV:Envelope/SOAP-ENV:Body/v:ShipmentReply/v:Notifications/v:Message" />
			</Message>
		</VoidReply>
	</xsl:template>
</xsl:stylesheet>
