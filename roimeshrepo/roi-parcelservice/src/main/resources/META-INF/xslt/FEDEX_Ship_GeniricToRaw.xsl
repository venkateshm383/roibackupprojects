<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" />

	<xsl:template match="/">

		<Shipment>
			<Service>
				<HostServiceType>
					<HostNaming>
						<HostName>
							<Carrier>
								<xsl:value-of
									select="Shipment/Service/HostServiceType/HostNaming/HostName/Carrier" />
							</Carrier>
							<Service>
								<xsl:value-of
									select="Shipment/Service/HostServiceType/HostNaming/HostName/Service" />
							</Service>
						</HostName>
					</HostNaming>
				</HostServiceType>
			</Service>

			<Shipper>
				<shipper_id>
					<xsl:value-of select="Shipment/Shipper/shipper_id" />
				</shipper_id>
			</Shipper>

			<Recipient>
				<Contact>
					<PersonName>
						<xsl:value-of
							select="Shipment/ShipToAddress/ContactAndAddress/PersonName" />
					</PersonName>
					<PhoneNumber>
						<xsl:value-of
							select="Shipment/ShipToAddress/ContactAndAddress/PhoneNumber" />
					</PhoneNumber>

				</Contact>
				<Address>
					<StreetLines>
						<xsl:value-of
							select="Shipment/ShipToAddress/ContactAndAddress/StreetLine1" />
					</StreetLines>
					<StreetLines>
						<xsl:value-of
							select="Shipment/ShipToAddress/ContactAndAddress/StreetLine2" />
					</StreetLines>
					<City>
						<xsl:value-of select="Shipment/ShipToAddress/ContactAndAddress/City" />
					</City>
					<StateOrProvinceCode>
						<xsl:value-of
							select="Shipment/ShipToAddress/ContactAndAddress/StateOrProvinceCode" />
					</StateOrProvinceCode>
					<PostalCode>
						<xsl:value-of
							select="Shipment/ShipToAddress/ContactAndAddress/PostalCode" />
					</PostalCode>
					<CountryCode>
						<xsl:value-of
							select="Shipment/ShipToAddress/ContactAndAddress/CountryCode" />
					</CountryCode>
				</Address>
			</Recipient>

			<Packages>
				<PackageCount>
					<xsl:value-of select="Shipment/Packages/PackageCount" />
				</PackageCount>
				<Package>
					<DropOffTime>
						<xsl:value-of select="Shipment/Packages/Package/DropOffTime" />
					</DropOffTime>
					<ShipDate>
						<xsl:value-of select="Shipment/Packages/Package/ShipDate" />
					</ShipDate>
					<PackagingType>
						<Code>
							<xsl:value-of select="Shipment/Packages/Package/PackagingType/Code" />
						</Code>
					</PackagingType>

					<PackageCount>
						<xsl:value-of select="Shipment/Packages/PackageCount" />
					</PackageCount>

					<Weight>
						<Units>
							<xsl:value-of
								select="Shipment/Packages/Package/Weight/UnitOfMeasurement/Code" />
						</Units>
						<Value>
							<xsl:value-of select="Shipment/Packages/Package/Weight/Weight" />
						</Value>
					</Weight>
					<Dimensions>
						<Length>
							<xsl:value-of select="Shipment/Packages/Package/Dimensions/Length" />
						</Length>
						<Width>
							<xsl:value-of select="Shipment/Packages/Package/Dimensions/Width" />
						</Width>
						<Height>
							<xsl:value-of select="Shipment/Packages/Package/Dimensions/Height" />
						</Height>
						<Units>
							<xsl:value-of select="Shipment/Packages/Package/Dimensions/Units" />
						</Units>
					</Dimensions>
				</Package>
			</Packages>

			<PaymentInformation>
				<ShipmentCharge>
					<Type>
						<xsl:value-of select="Shipment/PaymentInformation/ShipmentCharge/Type" />
					</Type>
				</ShipmentCharge>
			</PaymentInformation>


			<LabelSpecification>
				<ImageType>
					<xsl:value-of select="Shipment/LabelSpecification/ImageType" />
				</ImageType>
			</LabelSpecification>

		</Shipment>

	</xsl:template>

</xsl:stylesheet>