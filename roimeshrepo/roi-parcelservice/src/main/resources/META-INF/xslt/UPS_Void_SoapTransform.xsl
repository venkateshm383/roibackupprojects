<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:v1="http://www.ups.com/XMLSchema/XOLTWS/UPSS/v1.0" xmlns:v11="http://www.ups.com/XMLSchema/XOLTWS/Void/v1.1"
	xmlns:v12="http://www.ups.com/XMLSchema/XOLTWS/Common/v1.0">
	<xsl:output method="xml" />

	<xsl:template match="/">

		<soapenv:Envelope>

			<soapenv:Header>
				<v1:UPSSecurity>
					<v1:UsernameToken>
						<v1:Username>
							<xsl:value-of select="Void/UPSSecurity/UsernameToken/Username" />
						</v1:Username>
						<v1:Password>
							<xsl:value-of select="Void/UPSSecurity/UsernameToken/Password" />
						</v1:Password>
					</v1:UsernameToken>
					<v1:ServiceAccessToken>
						<v1:AccessLicenseNumber>
							<xsl:value-of
								select="Void/UPSSecurity/ServiceAccessToken/AccessLicenseNumber" />
						</v1:AccessLicenseNumber>
					</v1:ServiceAccessToken>
				</v1:UPSSecurity>
			</soapenv:Header>

			<soapenv:Body>
				<v11:VoidShipmentRequest>
					<v12:Request>
						<!--Zero or more repetitions: -->
						<v12:RequestOption></v12:RequestOption>
						<!--Optional: -->
						<!-- <v12:TransactionReference> <v12:CustomerContext></v12:CustomerContext> 
							<v12:TransactionIdentifier></v12:TransactionIdentifier> </v12:TransactionReference> -->
					</v12:Request>
					<v11:VoidShipment>
						<!-- <v11:ShipmentIdentificationNumber>
							<xsl:value-of select="Void/VoidShipment/ShipmentIdentificationNumber" />
						</v11:ShipmentIdentificationNumber> -->
						 <v11:ShipmentIdentificationNumber>
							<xsl:value-of select="Void/Request/TrackingId/PackageTrackingNumber" />
						</v11:ShipmentIdentificationNumber>
						<!-- <v11:TrackingNumber></v11:TrackingNumber> -->
					</v11:VoidShipment>
				</v11:VoidShipmentRequest>
			</soapenv:Body>
		</soapenv:Envelope>

	</xsl:template>
</xsl:stylesheet>
