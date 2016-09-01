<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:ship="http://www.ups.com/XMLSchema/XOLTWS/Ship/v1.0"
	xmlns:common="http://www.ups.com/XMLSchema/XOLTWS/Common/v1.0"
	xmlns:err="http://www.ups.com/XMLSchema/XOLTWS/Error/v1.1"
	exclude-result-prefixes="soapenv ship common err">
	<xsl:output method="xml" />
	<xsl:template match="/">

		<ShipReply>

			<xsl:if
				test="soapenv:Envelope/soapenv:Body/ship:ShipmentResponse/common:Response/common:ResponseStatus/common:Description/text() = 'Success'">
				<Notification>
					<xsl:value-of
						select="soapenv:Envelope/soapenv:Body/ship:ShipmentResponse/common:Response/common:ResponseStatus/common:Description" />
				</Notification>
				<Code>
					<xsl:value-of
						select="soapenv:Envelope/soapenv:Body/ship:ShipmentResponse/common:Response/common:ResponseStatus/common:Code" />
				</Code>
				<Message>
					<xsl:text>Request was successfully processed.</xsl:text>
				</Message>

				<CompletedShipmentDetail>
					<Carrier>UPS</Carrier>
					<ServiceType>Service Type Unknown</ServiceType>
					<CommitDetails>
						<CommitTimestamp>
							Unknown
						</CommitTimestamp>
					</CommitDetails>
					<SequenceNumber>
						Unknown
					</SequenceNumber>
					<MasterTrackingId>
						<TrackingNumber>No MasterTrackingId</TrackingNumber>
					</MasterTrackingId>
					<TrackingIds>
						<TrackingNumber>
							<xsl:value-of
								select="soapenv:Envelope/soapenv:Body/ship:ShipmentResponse/ship:ShipmentResults/ship:PackageResults/ship:TrackingNumber" />
						</TrackingNumber>
					</TrackingIds>
					<Label>
						<Type>
							unknown
						</Type>
						<ImageType>
							<xsl:value-of
								select="soapenv:Envelope/soapenv:Body/ship:ShipmentResponse/ship:ShipmentResults/ship:PackageResults/ship:ShippingLabel/ship:ImageFormat/ship:Code" />
						</ImageType>
						<Resolution>
							Unknown
						</Resolution>
						<Image>
							<xsl:value-of
								select="soapenv:Envelope/soapenv:Body/ship:ShipmentResponse/ship:ShipmentResults/ship:PackageResults/ship:ShippingLabel/ship:GraphicImage" />
						</Image>
					</Label>
				</CompletedShipmentDetail>

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

		</ShipReply>

	</xsl:template>
</xsl:stylesheet>
	