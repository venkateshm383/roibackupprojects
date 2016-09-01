<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns="http://fedex.com/ws/ship/v17">
	<xsl:output method="xml" />

	<xsl:template match="/">

		<soapenv:Envelope>
			<soapenv:Body>
				<ProcessShipmentRequest>

					<WebAuthenticationDetail>
						<UserCredential>
							<Key>
								<xsl:value-of
									select="Shipment/WebAuthenticationDetail/UserCredential/Key" />
							</Key>
							<Password>
								<xsl:value-of
									select="Shipment/WebAuthenticationDetail/UserCredential/Password" />
							</Password>
						</UserCredential>
					</WebAuthenticationDetail>

					<ClientDetail>
						<AccountNumber>
							<xsl:value-of select="Shipment/ClientDetail/AccountNumber" />
						</AccountNumber>
						<MeterNumber>
							<xsl:value-of select="Shipment/ClientDetail/MeterNumber" />
						</MeterNumber>
					</ClientDetail>

					<Version>
						<ServiceId>
							<xsl:value-of select="Shipment/Version/ServiceId" />
						</ServiceId>
						<Major>
							<xsl:value-of select="Shipment/Version/Major" />
						</Major>
						<Intermediate>
							<xsl:value-of select="Shipment/Version/Intermediate" />
						</Intermediate>
						<Minor>
							<xsl:value-of select="Shipment/Version/Minor" />
						</Minor>
					</Version>

					<RequestedShipment>

						<ShipTimestamp>
							<xsl:value-of select="Shipment/Packages/Package/ShipDate" />
						</ShipTimestamp>
						<DropoffType>
							<xsl:value-of select="Shipment/Packages/Package/DropOffTime" />
						</DropoffType>
						<ServiceType>
							<xsl:value-of
								select="Shipment/Service/HostServiceType/HostNaming/HostName/Service" />
						</ServiceType>
						<PackagingType>
							<xsl:value-of select="Shipment/Packages/Package/PackagingType/Code" />
						</PackagingType>

						<Shipper>
							<Contact>
								<CompanyName>
									<xsl:value-of
										select="Shipment/ShipperDetails/ContactAddress/CompanyName" />
								</CompanyName>
								<PhoneNumber>
									<xsl:value-of
										select="Shipment/ShipperDetails/ContactAddress/PhoneNumber" />
								</PhoneNumber>
							</Contact>
							<Address>
								<StreetLines>
									<xsl:value-of
										select="Shipment/ShipperDetails/ContactAddress/StreetLine1" />
								</StreetLines>
								<StreetLines>
									<xsl:value-of
										select="Shipment/ShipperDetails/ContactAddress/StreetLine2" />
								</StreetLines>
								<City>
									<xsl:value-of select="Shipment/ShipperDetails/ContactAddress/City" />
								</City>
								<StateOrProvinceCode>
									<xsl:value-of
										select="Shipment/ShipperDetails/ContactAddress/StateOrProvinceCode" />
								</StateOrProvinceCode>
								<PostalCode>
									<xsl:value-of
										select="Shipment/ShipperDetails/ContactAddress/PostalCode" />
								</PostalCode>
								<CountryCode>
									<xsl:value-of
										select="Shipment/ShipperDetails/ContactAddress/CountryCode" />
								</CountryCode>
							</Address>
						</Shipper>

						<Recipient>
							<Contact>
								<PersonName>
									<xsl:value-of select="Shipment/Recipient/Contact/PersonName" />
								</PersonName>
								<!-- <CompanyName>
									<xsl:value-of select="Shipment/Recipient/Contact/CompanyName" />
								</CompanyName> -->
								<PhoneNumber>
									<xsl:value-of select="Shipment/Recipient/Contact/PhoneNumber" />
								</PhoneNumber>
							</Contact>
							<Address>
								<StreetLines>
									<xsl:value-of select="Shipment/Recipient/Address/StreetLines" />
								</StreetLines>
								<StreetLines>
									<xsl:value-of select="Shipment/Recipient/Address/StreetLines" />
								</StreetLines>
								<City>
									<xsl:value-of select="Shipment/Recipient/Address/City" />
								</City>
								<StateOrProvinceCode>
									<xsl:value-of select="Shipment/Recipient/Address/StateOrProvinceCode" />
								</StateOrProvinceCode>
								<PostalCode>
									<xsl:value-of select="Shipment/Recipient/Address/PostalCode" />
								</PostalCode>
								<CountryCode>
									<xsl:value-of select="Shipment/Recipient/Address/CountryCode" />
								</CountryCode>
							</Address>
						</Recipient>
						<ShippingChargesPayment>
							<PaymentType>
								<xsl:value-of select="Shipment/PaymentInformation/ShipmentCharge/Type" />
							</PaymentType>
							<Payor>
								<ResponsibleParty>
									<AccountNumber>
										<xsl:value-of select="Shipment/ClientDetail/AccountNumber" />
									</AccountNumber>
								</ResponsibleParty>
							</Payor>
						</ShippingChargesPayment>
						<LabelSpecification>
							<LabelFormatType>
								<xsl:value-of select="Shipment/LabelSpecification/LabelFormatType" />
							</LabelFormatType>

							<xsl:if test="Shipment/LabelSpecification/ImageType/text() = 'ZPL'">
								<ImageType>
									<xsl:text>ZPLII</xsl:text>
								</ImageType>
							</xsl:if>

						</LabelSpecification>

						<PackageCount>
							<xsl:value-of select="Shipment/Packages/PackageCount" />
						</PackageCount>

						<RequestedPackageLineItems>
							<Weight>
								<Units>
									<xsl:value-of select="Shipment/Packages/Package/Weight/Units" />
								</Units>
								<Value>
									<xsl:value-of select="Shipment/Packages/Package/Weight/Value" />
								</Value>
							</Weight>
							<Dimensions>
								<Length>
									<xsl:value-of
										select="Shipment/Packages/Package/Dimensions/Length" />
								</Length>
								<Width>
									<xsl:value-of
										select="Shipment/Packages/Package/Dimensions/Width" />
								</Width>
								<Height>
									<xsl:value-of
										select="Shipment/Packages/Package/Dimensions/Height" />
								</Height>
								<Units>
									<xsl:value-of
										select="Shipment/Packages/Package/Dimensions/Units" />
								</Units>
							</Dimensions>
						</RequestedPackageLineItems>

					</RequestedShipment>

				</ProcessShipmentRequest>
			</soapenv:Body>
		</soapenv:Envelope>

	</xsl:template>
</xsl:stylesheet>
