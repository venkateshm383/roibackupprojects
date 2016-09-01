<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:err="http://www.ups.com/XMLSchema/XOLTWS/Error/v1.1" xmlns:ship="http://stamps.com/xml/namespace/2015/12/swsim/swsimV50"
	exclude-result-prefixes="soap xsi xsd err ship">
	<xsl:output method="xml" />
	<xsl:template match="/">
		<ShipReply>

			<xsl:if
				test="soap:Envelope/soap:Body/ship:CreateIndiciumResponse/ship:Authenticator">
				<Notification>Successfully created Ship</Notification>
				<Code>0</Code>
				<Message>
					<xsl:text>Request was successfully processed.</xsl:text>
				</Message>

				<CompletedShipmentDetail>
					<Carrier>Stamps</Carrier>
					<ServiceType>
						<xsl:value-of
							select="soap:Envelope/soap:Body/ship:CreateIndiciumResponse/ship:Rate/ship:ServiceType" />
					</ServiceType>
					<CommitDetails>
						<CommitTimestamp>
							<xsl:value-of
								select="soap:Envelope/soap:Body/ship:CreateIndiciumResponse/ship:Rate/ship:DeliveryDate" />
						</CommitTimestamp>
					</CommitDetails>
					<SequenceNumber>
						<xsl:value-of
							select="soap:Envelope/soap:Body/ship:CreateIndiciumResponse/ship:Mac" />
					</SequenceNumber>


					<xsl:choose>
						<xsl:when
							test="soap:Envelope/soap:Body/ship:CreateIndiciumResponse/ship:TrackingNumber">
							<MasterTrackingId>
								<TrackingNumber>
									<xsl:value-of
										select="soap:Envelope/soap:Body/ship:CreateIndiciumResponse/ship:TrackingNumber" />
								</TrackingNumber>
							</MasterTrackingId>
						</xsl:when>
						<xsl:otherwise>
							<MasterTrackingId>
								<TrackingNumber>No MasterTrackingId</TrackingNumber>
							</MasterTrackingId>
						</xsl:otherwise>
					</xsl:choose>
					<TrackingIds>
						<TrackingNumber>
							<xsl:value-of
								select="soap:Envelope/soap:Body/ship:CreateIndiciumResponse/ship:StampsTxID" />
						</TrackingNumber>
					</TrackingIds>
					<Label>
						<Type>unknown</Type>
						<xsl:if
							test="soap:Envelope/soap:Body/ship:CreateIndiciumResponse/ship:URL">
							<ImageType>URL</ImageType>
							<Resolution>Unknown</Resolution>
							<Image>
								<xsl:value-of
									select="soap:Envelope/soap:Body/ship:CreateIndiciumResponse/ship:URL" />
							</Image>
						</xsl:if>
					</Label>
				</CompletedShipmentDetail>
			</xsl:if>

			<xsl:if test="soap:Envelope/soap:Body/soap:Error">
				<Notification>
					<xsl:text>Error</xsl:text>
				</Notification>
				<Code>
					ErrorCode
				</Code>
				<Message>
					errorOccured
				</Message>
			</xsl:if>

		</ShipReply>

	</xsl:template>
</xsl:stylesheet>