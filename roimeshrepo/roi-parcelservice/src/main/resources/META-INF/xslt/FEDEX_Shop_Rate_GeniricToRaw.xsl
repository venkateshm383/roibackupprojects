<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" />

	<xsl:template match="/">

		<RateRequest>
			<ReturnTransitAndCommit>
				<xsl:value-of
					select="RateRequest/RateRequestInfo/RateRequestType/ReturnTransitTime" />
			</ReturnTransitAndCommit>
			<RequestedShipment>

				<!-- <xsl:if test=" RateRequest/RateRequestInfo/ServiceTypes/Way/text() 
					= 'Ground'"> <ServiceType> <xsl:value-of select="RateRequest/RateRequestInfo/ServiceTypes/ServiceType" 
					/> </ServiceType> </xsl:if> -->

				<Shipper>

					<shipper_id>
						<xsl:value-of select="RateRequest/Shipper/shipper_id" />
					</shipper_id>
					<!-- <Contact> <CompanyName> <xsl:value-of select="RateRequest/Shipper/ContactAndAddress/CompanyName" 
						/> </CompanyName> <PhoneNumber> <xsl:value-of select="RateRequest/Shipper/ContactAndAddress/PhoneNumber" 
						/> </PhoneNumber> </Contact> <Address> <StreetLines> <xsl:value-of select="RateRequest/Shipper/ContactAndAddress/StreetLine1" 
						/> </StreetLines> <StreetLines> <xsl:value-of select="RateRequest/Shipper/ContactAndAddress/StreetLine2" 
						/> </StreetLines> <City> <xsl:value-of select="RateRequest/Shipper/ContactAndAddress/City" 
						/> </City> <StateOrProvinceCode> <xsl:value-of select="RateRequest/Shipper/ContactAndAddress/StateOrProvinceCode" 
						/> </StateOrProvinceCode> <PostalCode> <xsl:value-of select="RateRequest/Shipper/ContactAndAddress/PostalCode" 
						/> </PostalCode> <CountryCode> <xsl:value-of select="RateRequest/Shipper/ContactAndAddress/CountryCode" 
						/> </CountryCode> </Address> -->
				</Shipper>

				<Recipient>
					<Contact>
						<PersonName>
							<xsl:value-of
								select="RateRequest/ShipToAddress/ContactAndAddress/PersonName" />
						</PersonName>
						<PhoneNumber>
							<xsl:value-of
								select="RateRequest/ShipToAddress/ContactAndAddress/PhoneNumber" />
						</PhoneNumber>

					</Contact>
					<Address>
						<StreetLines>
							<xsl:value-of
								select="RateRequest/ShipToAddress/ContactAndAddress/StreetLine1" />
						</StreetLines>
						<StreetLines>
							<xsl:value-of
								select="RateRequest/ShipToAddress/ContactAndAddress/StreetLine2" />
						</StreetLines>
						<City>
							<xsl:value-of select="RateRequest/ShipToAddress/ContactAndAddress/City" />
						</City>
						<StateOrProvinceCode>
							<xsl:value-of
								select="RateRequest/ShipToAddress/ContactAndAddress/StateOrProvinceCode" />
						</StateOrProvinceCode>
						<PostalCode>
							<xsl:value-of
								select="RateRequest/ShipToAddress/ContactAndAddress/PostalCode" />
						</PostalCode>
						<CountryCode>
							<xsl:value-of
								select="RateRequest/ShipToAddress/ContactAndAddress/CountryCode" />
						</CountryCode>
					</Address>
				</Recipient>
				<PackageCount>
					<xsl:value-of select="RateRequest/Packages/PackageCount" />
				</PackageCount>


				<xsl:choose>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Postcard'">
						<PackagingType>YOUR_PACKAGING</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Letter'">
						<PackagingType>YOUR_PACKAGING</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Large Envelope or Flat'">
						<PackagingType>FEDEX_ENVELOPE</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Thick Envelope'">
						<PackagingType>FEDEX_ENVELOPE</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Package'">
						<PackagingType>FEDEX_PAK</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Small Flat Rate Box'">
						<PackagingType>FEDEX_SMALL_BOX</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Flat Rate Box'">
						<PackagingType>FEDEX_MEDIUM_BOX</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Large Flat Rate Box'">
						<PackagingType>FEDEX_LARGE_BOX</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Flat Rate Envelope'">
						<PackagingType>FEDEX_ENVELOPE</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Flat Rate Padded Envelope'">
						<PackagingType>FEDEX_ENVELOPE</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Large Package'">
						<PackagingType>FEDEX_LARGE_BOX</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Oversized Package'">
						<PackagingType>FEDEX_EXTRA_LARGE_BOX</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Regional Rate Box A'">
						<PackagingType>YOUR_PACKAGING</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Regional Rate Box B'">
						<PackagingType>YOUR_PACKAGING</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Regional Rate Box C'">
						<PackagingType>YOUR_PACKAGING</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Legal Flat Rate Envelope'">
						<PackagingType>FEDEX_ENVELOPE</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = '10KG BOX'">
						<PackagingType>FEDEX_10KG_BOX</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'BOX'">
						<PackagingType>FEDEX_BOX</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = '25KG BOX'">
						<PackagingType>FEDEX_25KG_BOX</PackagingType>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'TUBE'">
						<PackagingType>FEDEX_TUBE</PackagingType>
					</xsl:when>
					<xsl:otherwise>
						<PackagingType>YOUR_PACKAGING</PackagingType>
					</xsl:otherwise>
				</xsl:choose>


				<RequestedPackageLineItems>

					<Weight>
						<Units>
							<xsl:value-of
								select="RateRequest/Packages/Package/Weight/UnitOfMeasurement/Code" />
						</Units>
						<Value>
							<xsl:value-of select="RateRequest/Packages/Package/Weight/Weight" />
						</Value>
					</Weight>
					<Dimensions>
						<Length>
							<xsl:value-of select="RateRequest/Packages/Package/Dimensions/Length" />
						</Length>
						<Width>
							<xsl:value-of select="RateRequest/Packages/Package/Dimensions/Width" />
						</Width>
						<Height>
							<xsl:value-of select="RateRequest/Packages/Package/Dimensions/Height" />
						</Height>
						<Units>
							<xsl:value-of select="RateRequest/Packages/Package/Dimensions/Units" />
						</Units>
					</Dimensions>
				</RequestedPackageLineItems>
			</RequestedShipment>
		</RateRequest>

	</xsl:template>

</xsl:stylesheet>