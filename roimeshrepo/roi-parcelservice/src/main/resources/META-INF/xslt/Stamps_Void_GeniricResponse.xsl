<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:void="http://stamps.com/xml/namespace/2015/12/swsim/swsimV50"
	xmlns:fault="http://stamps.com/xml/namespace/2008/01/fault"
	exclude-result-prefixes="soap fault xsd xsi void">
	<xsl:output method="xml" />
	<xsl:template match="/">

		<VoidReply>
			<xsl:if
				test="soap:Envelope/soap:Body/void:CancelIndiciumResponse/void:Authenticator">
				<Notification>
					Success
				</Notification>
				<Code>
					0
				</Code>
				<Message>
					<xsl:text>Request was successful and proceeded for Refund: The sessionId is = </xsl:text>
					<xsl:value-of
					select="soap:Envelope/soap:Body/void:CancelIndiciumResponse/void:Authenticator" />
				</Message>
			</xsl:if>

			<xsl:if
				test="soap:Envelope/soap:Body/soap:Fault">
				<Notification>
					<xsl:text>ErrorOccured</xsl:text>
				</Notification>
				<Code>
					<xsl:value-of
					select="soap:Envelope/soap:Body/soap:Fault/detail/fault:sdcerror/@code" />
				</Code>
				<Message>
					<xsl:value-of
					select="soap:Envelope/soap:Body/soap:Fault/faultstring" />
				</Message>
			</xsl:if>

		</VoidReply>
	</xsl:template>
</xsl:stylesheet>
	