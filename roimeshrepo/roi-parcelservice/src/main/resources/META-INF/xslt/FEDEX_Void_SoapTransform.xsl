<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" />

	<xsl:template match="/">

		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
			xmlns:v17="http://fedex.com/ws/ship/v17">
			<soapenv:Body>
				<v17:DeleteShipmentRequest>

					<v17:WebAuthenticationDetail>
						<v17:UserCredential>
							<v17:Key>
								<xsl:value-of
									select="Void/DeleteShipmentRequest/WebAuthenticationDetail/UserCredential/Key" />
							</v17:Key>
							<v17:Password>
								<xsl:value-of
									select="Void/DeleteShipmentRequest/WebAuthenticationDetail/UserCredential/Password" />
							</v17:Password>
						</v17:UserCredential>
					</v17:WebAuthenticationDetail>

					<v17:ClientDetail>
						<v17:AccountNumber>
							<xsl:value-of
								select="Void/DeleteShipmentRequest/ClientDetail/AccountNumber" />
						</v17:AccountNumber>
						<v17:MeterNumber>
							<xsl:value-of
								select="Void/DeleteShipmentRequest/ClientDetail/MeterNumber" />
						</v17:MeterNumber>
					</v17:ClientDetail>


					<v17:Version>
						<v17:ServiceId>
							<xsl:value-of select="Void/DeleteShipmentRequest/Version/ServiceId" />
						</v17:ServiceId>
						<v17:Major>
							<xsl:value-of select="Void/DeleteShipmentRequest/Version/Major" />
						</v17:Major>
						<v17:Intermediate>
							<xsl:value-of select="Void/DeleteShipmentRequest/Version/Intermediate" />
						</v17:Intermediate>
						<v17:Minor>
							<xsl:value-of select="Void/DeleteShipmentRequest/Version/Minor" />
						</v17:Minor>
					</v17:Version>

					<v17:ShipTimestamp>
						<xsl:value-of select="Void/ShipTimestamp" />
					</v17:ShipTimestamp>
					<v17:TrackingId>
						<v17:TrackingIdType>
							<xsl:value-of select="Void/TrackingIdType" />
						</v17:TrackingIdType>
						<v17:TrackingNumber>
							<xsl:value-of select="Void/Request/TrackingId/PackageTrackingNumber" />
						</v17:TrackingNumber>
					</v17:TrackingId>
					<v17:DeletionControl>
						<xsl:value-of select="Void/DeletionControl" />
					</v17:DeletionControl>

				</v17:DeleteShipmentRequest>
			</soapenv:Body>
		</soapenv:Envelope>

	</xsl:template>
</xsl:stylesheet>
