<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:v1="http://www.ups.com/XMLSchema/XOLTWS/Void/v1.1" xmlns:v11="http://www.ups.com/XMLSchema/XOLTWS/Common/v1.0"
	xmlns:err="http://www.ups.com/XMLSchema/XOLTWS/Error/v1.1"
	exclude-result-prefixes="soapenv v1 v11 err">
	<xsl:output method="xml" />
	<xsl:template match="/">

		<VoidReply>
			<xsl:if
				test="soapenv:Envelope/soapenv:Body/v1:VoidShipmentResponse/v11:Response/v11:ResponseStatus/v11:Description/text() = 'Success'">
				<Notification>
					<xsl:value-of
						select="soapenv:Envelope/soapenv:Body/v1:VoidShipmentResponse/v11:Response/v11:ResponseStatus/v11:Description" />
				</Notification>
				<Code>
					<xsl:value-of
						select="soapenv:Envelope/soapenv:Body/v1:VoidShipmentResponse/v11:Response/v11:ResponseStatus/v11:Code" />
				</Code>
				<Message>
					<xsl:text>Request was successfully </xsl:text>
					<xsl:value-of
						select="soapenv:Envelope/soapenv:Body/v1:VoidShipmentResponse/v1:SummaryResult/v1:Status/v1:Description" />
				</Message>
			</xsl:if>

			<xsl:if
				test="soapenv:Envelope/soapenv:Body/soapenv:Fault/detail/err:Errors">
				<Notification>
					<xsl:text>Error</xsl:text>
				</Notification>
				<Code>
					<xsl:value-of
						select="soapenv:Envelope/soapenv:Body/soapenv:Fault/detail/err:Errors/err:ErrorDetail/err:PrimaryErrorCode/err:Code" />
				</Code>
				<Message>
					<xsl:value-of
						select="soapenv:Envelope/soapenv:Body/soapenv:Fault/detail/err:Errors/err:ErrorDetail/err:PrimaryErrorCode/err:Description" />
				</Message>
			</xsl:if>

		</VoidReply>
	</xsl:template>
</xsl:stylesheet>
	