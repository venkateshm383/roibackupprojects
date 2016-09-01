<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:v1="http://www.ups.com/XMLSchema/XOLTWS/UPSS/v1.0" xmlns:v11="http://www.ups.com/XMLSchema/XOLTWS/Rate/v1.1"
	xmlns:v12="http://www.ups.com/XMLSchema/XOLTWS/Common/v1.0">
	<xsl:output method="xml" />

	<xsl:template match="/">

		<soapenv:Envelope>

			<soapenv:Header>
				<v1:UPSSecurity>
					<v1:UsernameToken>
						<v1:Username>
							<xsl:value-of select="RateRequest/UPSSecurity/UsernameToken/Username" />
						</v1:Username>
						<v1:Password>
							<xsl:value-of select="RateRequest/UPSSecurity/UsernameToken/Password" />
						</v1:Password>
					</v1:UsernameToken>
					<v1:ServiceAccessToken>
						<v1:AccessLicenseNumber>
							<xsl:value-of
								select="RateRequest/UPSSecurity/ServiceAccessToken/AccessLicenseNumber" />
						</v1:AccessLicenseNumber>
					</v1:ServiceAccessToken>
				</v1:UPSSecurity>
			</soapenv:Header>

			<soapenv:Body>
				<v11:RateRequest>
					<v12:Request>
						<!--Zero or more repetitions: -->
						<v12:RequestOption>
							<xsl:value-of select="RateRequest/Request/RequestOption" />
						</v12:RequestOption>
						<v12:RequestAction>
							<xsl:value-of select="RateRequest/Request/RequestOption" />
						</v12:RequestAction>
						<!--Optional: -->
						<v12:SubVersion>unknown</v12:SubVersion>
						<!--Optional: -->
						<v12:TransactionReference>
							<!--Optional: -->
							<v12:CustomerContext>
								<xsl:value-of
									select="RateRequest/Request/TransactionReference/CustomerContext" />
							</v12:CustomerContext>
							<v12:XpciVersion>
								<xsl:value-of
									select="RateRequest/Request/TransactionReference/XpciVersion" />
							</v12:XpciVersion>
							<!--Optional: -->
							<v12:TransactionIdentifier>unknown
							</v12:TransactionIdentifier>
						</v12:TransactionReference>
					</v12:Request>
					<v11:PickupType>
						<v11:Code>
							<xsl:value-of select="RateRequest/PickupType/PickupTypeCode" />
						</v11:Code>
						<!--Optional: -->
						<v11:Description>unknown</v11:Description>
					</v11:PickupType>
					<!--Optional: -->
					<v11:CustomerClassification>
						<v11:Code>
							<xsl:value-of
								select="RateRequest/CustomerClassification/CustomerClassificationCode" />
						</v11:Code>
						<!--Optional: -->
						<v11:Description>unknown</v11:Description>
					</v11:CustomerClassification>

					<v11:Shipment>
						<v11:Shipper>
							<!--Optional: -->
							<v11:Name>
								<xsl:value-of select="RateRequest/ShipperDetails/ContactAddress/Name " />
							</v11:Name>
							<!--Optional: -->
							<v11:ShipperNumber><xsl:value-of select="RateRequest/Shipper/shipper_id " /></v11:ShipperNumber>
							<v11:Address>
								<!--0 to 3 repetitions: -->
								<v11:AddressLine>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/AddressLine " />
								</v11:AddressLine>
								<!--Optional: -->
								<v11:City>
									<xsl:value-of select="RateRequest/ShipperDetails/ContactAddress/City " />
								</v11:City>
								<!--Optional: -->
								<v11:StateProvinceCode>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/StateOrProvinceCode " />
								</v11:StateProvinceCode>
								<!--Optional: -->
								<v11:PostalCode>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/PostalCode " />
								</v11:PostalCode>
								<v11:CountryCode>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/CountryCode " />
								</v11:CountryCode>
							</v11:Address>
						</v11:Shipper>
						<v11:ShipTo>
							<!--Optional: -->
							<v11:Name>
								<xsl:value-of select="RateRequest/Recipient/Contact/PersonName " />
							</v11:Name>
							<v11:Address>
								<!--0 to 3 repetitions: -->
								<v11:AddressLine>
									<xsl:value-of select="RateRequest/Recipient/Address/StreetLines " />
								</v11:AddressLine>
								<!--Optional: -->
								<v11:City>
									<xsl:value-of select="RateRequest/Recipient/Address/City " />
								</v11:City>
								<!--Optional: -->
								<v11:StateProvinceCode>
									<xsl:value-of
										select="RateRequest/Recipient/Address/StateProvinceCode " />
								</v11:StateProvinceCode>
								<!--Optional: -->
								<v11:PostalCode>
									<xsl:value-of select="RateRequest/Recipient/Address/PostalCode " />
								</v11:PostalCode>
								<v11:CountryCode>
									<xsl:value-of select="RateRequest/Recipient/Address/CountryCode " />
								</v11:CountryCode>
								<!--Optional: -->
								<v11:ResidentialAddressIndicator>unknown
								</v11:ResidentialAddressIndicator>
							</v11:Address>
						</v11:ShipTo>

						<v11:ShipFrom>
							<!--Optional: -->
							<!--Optional: -->
							<v11:Name>
								<xsl:value-of select="RateRequest/ShipperDetails/ContactAddress/Name " />
							</v11:Name>
							<!--Optional: -->
							<v11:ShipperNumber><xsl:value-of select="RateRequest/Shipper/shipper_id " /></v11:ShipperNumber>
							<v11:Address>
								<!--0 to 3 repetitions: -->
								<v11:AddressLine>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/AddressLine " />
								</v11:AddressLine>
								<!--Optional: -->
								<v11:City>
									<xsl:value-of select="RateRequest/ShipperDetails/ContactAddress/City " />
								</v11:City>
								<!--Optional: -->
								<v11:StateProvinceCode>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/StateOrProvinceCode " />
								</v11:StateProvinceCode>
								<!--Optional: -->
								<v11:PostalCode>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/PostalCode " />
								</v11:PostalCode>
								<v11:CountryCode>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/CountryCode " />
								</v11:CountryCode>
							</v11:Address>
						</v11:ShipFrom>
						 <v11:Service>
							<v11:Code>
								<xsl:value-of select="RateRequest/ServiceType" />
							</v11:Code>
							<v11:Description>unknown</v11:Description>
						</v11:Service> 
						<!--Optional: -->
						<v11:DocumentsOnlyIndicator>unknown</v11:DocumentsOnlyIndicator>
						<!--Optional: -->
						<v11:NumOfPieces>
							<xsl:value-of select="RateRequest/PackageCount" />
						</v11:NumOfPieces>
						<!--1 or more repetitions: -->
						<v11:Package>
							<!--Optional: -->
							<v11:PackagingType>
								<v11:Code>
									<xsl:value-of select="RateRequest/PackingType/PackagingTypeCode" />
								</v11:Code>
								<!--Optional: -->
								<v11:Description>unknown</v11:Description>
							</v11:PackagingType>
							<!--Optional: -->
							<v11:Dimensions>
								<v11:UnitOfMeasurement>
									<v11:Code>
										<xsl:value-of
											select="RateRequest/RequestedPackageLineItems/Dimensions/Units" />
									</v11:Code>
									<!--Optional: -->
									<v11:Description>unknown</v11:Description>
								</v11:UnitOfMeasurement>
								<!--Optional: -->
								<v11:Length>
									<xsl:value-of
										select="RateRequest/RequestedPackageLineItems/Dimensions/Length" />
								</v11:Length>
								<!--Optional: -->
								<v11:Width>
									<xsl:value-of
										select="RateRequest/RequestedPackageLineItems/Dimensions/Width" />
								</v11:Width>
								<!--Optional: -->
								<v11:Height>
									<xsl:value-of
										select="RateRequest/RequestedPackageLineItems/Dimensions/Height" />
								</v11:Height>
							</v11:Dimensions>
							<!--Optional: -->
							<v11:PackageWeight>
								<v11:UnitOfMeasurement>
									<v11:Code>
										<xsl:value-of
											select="RateRequest/RequestedPackageLineItems/Weight/Units" />
									</v11:Code>
									<!--Optional: -->
									<v11:Description>unknown</v11:Description>
								</v11:UnitOfMeasurement>
								<v11:Weight>
									<xsl:value-of
										select="RateRequest/RequestedPackageLineItems/Weight/Value" />
								</v11:Weight>
							</v11:PackageWeight>
							<!--Optional: -->
						</v11:Package>


					</v11:Shipment>
				</v11:RateRequest>
			</soapenv:Body>
		</soapenv:Envelope>

	</xsl:template>
</xsl:stylesheet>
