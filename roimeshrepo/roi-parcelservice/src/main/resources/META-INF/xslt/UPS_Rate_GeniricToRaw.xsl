<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" />

	<xsl:template match="/">
		<RateRequest>

			
				<ServiceType>
					<xsl:value-of select="RateRequest/RateRequestInfo/ServiceTypes/ServiceType" />
				</ServiceType>
			

			<Shipper>

				<shipper_id>
					<xsl:value-of select="RateRequest/Shipper/shipper_id" />
				</shipper_id>
			</Shipper>

			<Recipient>
				<Contact>
					<PersonName>
						<xsl:value-of
							select="RateRequest/ShipToAddress/ContactAndAddress/PersonName" />
					</PersonName>
				</Contact>
				<Address>
					<StreetLines>
						<xsl:value-of
							select="RateRequest/ShipToAddress/ContactAndAddress/StreetLine1" />
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
			<!-- #TODO changes will happen once the values are dynamic -->
			<PackageCount>
				<xsl:value-of select="RateRequest/Packages/PackageCount" />
			</PackageCount>
			<PackingType>
			
			<xsl:choose>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Postcard'">
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Letter'">
			<PackagingTypeCode>01</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Large Envelope or Flat'">
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Thick Envelope'">
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Package'">
			<PackagingTypeCode>02</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Small Flat Rate Box'">
			<PackagingTypeCode>2a</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Flat Rate Box'">
			<PackagingTypeCode>2b</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Large Flat Rate Box'">
			<PackagingTypeCode>2c</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Flat Rate Envelope'">
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Flat Rate Padded Envelope'">
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Large Package'">
			<PackagingTypeCode>02</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Oversized Package'">
			<PackagingTypeCode>02</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Regional Rate Box A'">
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Regional Rate Box B'">
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Regional Rate Box C'">
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'Legal Flat Rate Envelope'">
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = '10KG BOX'">
			<PackagingTypeCode>25</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'BOX'">
			<PackagingTypeCode>21</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = '25KG BOX'">
			<PackagingTypeCode>24</PackagingTypeCode>
					</xsl:when>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'TUBE'">
			<PackagingTypeCode>03</PackagingTypeCode>
					</xsl:when>
					<xsl:otherwise>
			<PackagingTypeCode>00</PackagingTypeCode>
					</xsl:otherwise>
				</xsl:choose>			
			</PackingType>
			<RequestedPackageLineItems>

				<Weight>
					<xsl:if
				test="RateRequest/Packages/Package/Weight/UnitOfMeasurement/Code/text() = 'LB'">
						<Units>
							<xsl:text>LBS</xsl:text>
						</Units>
					</xsl:if>
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
		</RateRequest>

	</xsl:template>
</xsl:stylesheet>