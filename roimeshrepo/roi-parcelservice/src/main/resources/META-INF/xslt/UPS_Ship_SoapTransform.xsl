<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:v1="http://www.ups.com/XMLSchema/XOLTWS/UPSS/v1.0" xmlns:v11="http://www.ups.com/XMLSchema/XOLTWS/Ship/v1.0"
	xmlns:v12="http://www.ups.com/XMLSchema/XOLTWS/Common/v1.0" xmlns:v13="http://www.ups.com/XMLSchema/XOLTWS/IF/v1.0">
	<xsl:output method="xml" />

	<xsl:template match="/">

		<soapenv:Envelope>

			<soapenv:Header>
				<v1:UPSSecurity>
					<v1:UsernameToken>
						<v1:Username>
							<xsl:value-of
								select="ShipmentRequest/Shipment/UPSSecurity/UsernameToken/Username" />
						</v1:Username>
						<v1:Password>
							<xsl:value-of
								select="ShipmentRequest/Shipment/UPSSecurity/UsernameToken/Password" />
						</v1:Password>
					</v1:UsernameToken>
					<v1:ServiceAccessToken>
						<v1:AccessLicenseNumber>
							<xsl:value-of
								select="ShipmentRequest/Shipment/UPSSecurity/ServiceAccessToken/AccessLicenseNumber" />
						</v1:AccessLicenseNumber>
					</v1:ServiceAccessToken>
				</v1:UPSSecurity>
			</soapenv:Header>

			<soapenv:Body>
				<v11:ShipmentRequest>
					<v12:Request>
						<v12:RequestOption>
							<xsl:value-of select="ShipmentRequest/Shipment/Request/RequestOption" />
						</v12:RequestOption>
						<v12:TransactionReference>
							<v12:CustomerContext>
								<xsl:value-of
									select="ShipmentRequest/Shipment/Request/TransactionReference/CustomerContext" />
							</v12:CustomerContext>
							<v12:XpciVersion>
								<xsl:value-of
									select="ShipmentRequest/Shipment/Request/TransactionReference/XpciVersion" />
							</v12:XpciVersion>
						</v12:TransactionReference>
					</v12:Request>

					<v11:Shipment>
						<v11:Shipper>
							<v11:Name>
								<xsl:value-of
									select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/Name " />
							</v11:Name>
							<v11:ShipperNumber>
								<xsl:value-of select="ShipmentRequest/Shipment/Shipper/shipper_id " />
							</v11:ShipperNumber>
							<v11:Address>
								<v11:AddressLine>
									<xsl:value-of
										select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/AddressLine " />
								</v11:AddressLine>
								<v11:City>
									<xsl:value-of
										select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/City " />
								</v11:City>
								<!--Optional: -->
								<v11:StateProvinceCode>
									<xsl:value-of
										select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/StateOrProvinceCode " />
								</v11:StateProvinceCode>
								<v11:PostalCode>
									<xsl:value-of
										select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/PostalCode " />
								</v11:PostalCode>
								<v11:CountryCode>
									<xsl:value-of
										select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/CountryCode " />
								</v11:CountryCode>
							</v11:Address>
						</v11:Shipper>
						<v11:ShipTo>
							<v11:Name>
								<xsl:value-of
									select="ShipmentRequest/Shipment/Recipient/Contact/PersonName " />
							</v11:Name>
							<v11:Address>
								<v11:AddressLine>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Recipient/Address/StreetLines " />
								</v11:AddressLine>
								<v11:City>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Recipient/Address/City " />
								</v11:City>
								<v11:StateProvinceCode>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Recipient/Address/StateOrProvinceCode " />
								</v11:StateProvinceCode>
								<v11:PostalCode>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Recipient/Address/PostalCode " />
								</v11:PostalCode>
								<v11:CountryCode>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Recipient/Address/CountryCode " />
								</v11:CountryCode>
							</v11:Address>
						</v11:ShipTo>

						<v11:ShipFrom>
							<v11:Name>
								<xsl:value-of
									select="ShipmentRequest/Shipment/Sender/Contact/PersonName " />
							</v11:Name>
							<v11:ShipperNumber>
								<xsl:value-of select="ShipmentRequest/Shipment/Shipper/shipper_id " />
							</v11:ShipperNumber>
							<v11:Address>
								<v11:AddressLine>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Sender/Address/StreetLines " />
								</v11:AddressLine>
								<v11:City>
									<xsl:value-of select="ShipmentRequest/Shipment/Sender/Address/City " />
								</v11:City>
								<v11:StateProvinceCode>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Sender/Address/StateOrProvinceCode " />
								</v11:StateProvinceCode>
								<v11:PostalCode>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Sender/Address/PostalCode " />
								</v11:PostalCode>
								<v11:CountryCode>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Sender/Address/CountryCode " />
								</v11:CountryCode>
							</v11:Address>
						</v11:ShipFrom>

						<v11:PaymentInformation>
							<v11:ShipmentCharge>
								<v11:Type>
									<xsl:value-of
										select="ShipmentRequest/Shipment/PaymentInformation/ShipmentCharge/Type " />
								</v11:Type>
								<v11:BillShipper>
									<v11:AccountNumber>
										<xsl:value-of
											select="ShipmentRequest/Shipment/PaymentInformation/ShipmentCharge/BillShipper/AccountNumber " />
									</v11:AccountNumber>
								</v11:BillShipper>
							</v11:ShipmentCharge>
						</v11:PaymentInformation>

						<v11:Service>
							<v11:Code>
								<xsl:value-of select="ShipmentRequest/Shipment/ServiceType" />
							</v11:Code>
						</v11:Service>
						<v11:Package>
							<v11:Packaging>
								<v11:Code>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Package/Packaging/Code" />
								</v11:Code>
							</v11:Packaging>
							<v11:Dimensions>
								<v11:UnitOfMeasurement>
									<v11:Code>
										<xsl:value-of
											select="ShipmentRequest/Shipment/Package/Dimensions/Units" />
									</v11:Code>
								</v11:UnitOfMeasurement>
								<v11:Length>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Package/Dimensions/Length" />
								</v11:Length>
								<v11:Width>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Package/Dimensions/Width" />
								</v11:Width>
								<v11:Height>
									<xsl:value-of
										select="ShipmentRequest/Shipment/Package/Dimensions/Height" />
								</v11:Height>
							</v11:Dimensions>
							<v11:PackageWeight>
								<v11:UnitOfMeasurement>
									<v11:Code>
										<xsl:value-of select="ShipmentRequest/Shipment/Package/Weight/Units" />
									</v11:Code>
								</v11:UnitOfMeasurement>
								<v11:Weight>
									<xsl:value-of select="ShipmentRequest/Shipment/Package/Weight/Value" />
								</v11:Weight>
							</v11:PackageWeight>
						</v11:Package>
					</v11:Shipment>

					<v11:LabelSpecification>
						<v11:LabelImageFormat>
							<v11:Code>
								<xsl:value-of select="ShipmentRequest/LabelImageFormat/Code" />
							</v11:Code>
						</v11:LabelImageFormat>
						<v11:LabelStockSize>
							<v11:Height>
								<xsl:value-of
									select="ShipmentRequest/Shipment/LabelStockSize/LabelStockSizeHeight" />
							</v11:Height>
							<v11:Width>
								<xsl:value-of
									select="ShipmentRequest/Shipment/LabelStockSize/LabelStockSizeWidth" />
							</v11:Width>
						</v11:LabelStockSize>
					</v11:LabelSpecification>
				</v11:ShipmentRequest>
			</soapenv:Body>
		</soapenv:Envelope>

	</xsl:template>
</xsl:stylesheet>
