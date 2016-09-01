<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:ship="http://fedex.com/ws/ship/v17" exclude-result-prefixes="SOAP-ENV ship">
	<xsl:output method="xml" />
	<xsl:template match="/">

		<ShipReply>

			<xsl:if test="SOAP-ENV:Envelope/SOAP-ENV:Body/SOAP-ENV:Fault">
				<Notification>
					<xsl:text>Error</xsl:text>
				</Notification>
				<Code>
					<xsl:value-of
						select="SOAP-ENV:Envelope/SOAP-ENV:Body/SOAP-ENV:Fault/detail/code" />
				</Code>
				<Message>
					<xsl:value-of
						select="SOAP-ENV:Envelope/SOAP-ENV:Body/SOAP-ENV:Fault/detail/desc" />
				</Message>
			</xsl:if>
			
			<xsl:if test="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:Notifications/ship:Severity">
			<Notification>
				<xsl:value-of
					select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:Notifications/ship:Severity" />
			</Notification>
			<Code>
				<xsl:value-of
					select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:Notifications/ship:Code" />
			</Code>
			<Message>
				<xsl:value-of
					select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:Notifications/ship:Message" />
			</Message>
			<!--Zero or more repetitions on basis of number of Carrier and Service 
				Type : -->
			<CompletedShipmentDetail>
				<Carrier>FEDEX</Carrier>
				<ServiceType>Only Service code is available</ServiceType>
				<CommitDetails>
					<CommitTimestamp>
						<xsl:value-of
							select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:CompletedShipmentDetail/ship:OperationalDetail/ship:TransitTime" />
					</CommitTimestamp>
				</CommitDetails>
				<SequenceNumber>
					<xsl:value-of
						select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:CompletedShipmentDetail/ship:CompletedPackageDetails/ship:SequenceNumber" />
				</SequenceNumber>
				<MasterTrackingId>
					<TrackingNumber>No MasterTrackingId</TrackingNumber>
				</MasterTrackingId>
				<TrackingIds>
					<TrackingNumber>
						<xsl:value-of
							select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:CompletedShipmentDetail/ship:CompletedPackageDetails/ship:TrackingIds/ship:TrackingNumber" />
					</TrackingNumber>
				</TrackingIds>
				<Label>
					<Type>
						<xsl:value-of
							select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:CompletedShipmentDetail/ship:CompletedPackageDetails/ship:Label/ship:Type" />
					</Type>
					<ImageType>
						<xsl:value-of
							select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:CompletedShipmentDetail/ship:CompletedPackageDetails/ship:Label/ship:ImageType" />
					</ImageType>
					<Resolution>
						<xsl:value-of
							select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:CompletedShipmentDetail/ship:CompletedPackageDetails/ship:Label/ship:Resolution" />
					</Resolution>
					<Image>
						<xsl:value-of
							select="SOAP-ENV:Envelope/SOAP-ENV:Body/ship:ProcessShipmentReply/ship:CompletedShipmentDetail/ship:CompletedPackageDetails/ship:Label/ship:Parts/ship:Image" />
					</Image>
				</Label>
			</CompletedShipmentDetail>
			</xsl:if>
		</ShipReply>

	</xsl:template>
</xsl:stylesheet>
	