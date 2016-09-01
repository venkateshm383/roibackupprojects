<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" />

	<xsl:template match="/">
		<ShipmentRequest>

			<Shipment>

				<ServiceType>
					<xsl:value-of
						select="Shipment/Service/HostServiceType/HostNaming/HostName/Service" />
				</ServiceType>

				<Shipper>

					<shipper_id>
						<xsl:value-of select="Shipment/Shipper/shipper_id" />
					</shipper_id>
				</Shipper>

				<Sender>
					<Contact>
						<PersonName>
							<xsl:value-of
								select="Shipment/ShipFromAddress/ContactAndAddress/PersonName" />
						</PersonName>
					</Contact>
					<Address>
						<StreetLines>
							<xsl:value-of
								select="Shipment/ShipFromAddress/ContactAndAddress/StreetLine1" />
						</StreetLines>
						<City>
							<xsl:value-of select="Shipment/ShipFromAddress/ContactAndAddress/City" />
						</City>
						<StateOrProvinceCode>
							<xsl:value-of
								select="Shipment/ShipFromAddress/ContactAndAddress/StateOrProvinceCode" />
						</StateOrProvinceCode>
						<PostalCode>
							<xsl:value-of
								select="Shipment/ShipFromAddress/ContactAndAddress/PostalCode" />
						</PostalCode>
						<CountryCode>
							<xsl:value-of
								select="Shipment/ShipFromAddress/ContactAndAddress/CountryCode" />
						</CountryCode>
					</Address>
				</Sender>

				<Recipient>
					<Contact>
						<PersonName>
							<xsl:value-of
								select="Shipment/ShipToAddress/ContactAndAddress/PersonName" />
						</PersonName>
					</Contact>
					<Address>
						<StreetLines>
							<xsl:value-of
								select="Shipment/ShipToAddress/ContactAndAddress/StreetLine1" />
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

				<PaymentInformation>
					<ShipmentCharge>
						<!-- <xsl:if test="Shipment/PaymentInformation/ShipmentCharge/Type/text() 
							= 'SENDER'"> <Type> <xsl:value-of select="01" /> </Type> </xsl:if> -->
						<Type>
							<xsl:value-of select="Shipment/PaymentInformation/ShipmentCharge/Type" />
						</Type>
						<BillShipper>
							<AccountNumber>
								<xsl:value-of
									select="Shipment/PaymentInformation/ShipmentCharge/BillShipper/AccountNumber" />
							</AccountNumber>
						</BillShipper>
					</ShipmentCharge>
				</PaymentInformation>

				<Package>

					<Packaging>
						<Code>
							<xsl:value-of select="Shipment/Packages/Package/PackagingType/Code" />
						</Code>
					</Packaging>

					<Weight>
						<xsl:if
							test="Shipment/Packages/Package/Weight/UnitOfMeasurement/Code/text() = 'LB'">
							<Units>
								<xsl:text>LBS</xsl:text>
							</Units>
						</xsl:if>
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
							<xsl:value-of
								select="Shipment/Packages/Package/Dimensions/UnitOfMeasurement/Code" />
						</Units>
					</Dimensions>
				</Package>
			</Shipment>
			<LabelImageFormat>
				<Code>
					<xsl:value-of select="Shipment/LabelSpecification/ImageType" />
				</Code>
			</LabelImageFormat>
		</ShipmentRequest>

	</xsl:template>
</xsl:stylesheet>