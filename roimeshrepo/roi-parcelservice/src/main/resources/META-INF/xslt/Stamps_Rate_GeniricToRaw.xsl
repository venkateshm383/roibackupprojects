<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" />

	<xsl:template match="/">
		<RateRequest>

			<ServiceType>
				<xsl:value-of select="RateRequest/RateRequestInfo/ServiceTypes/ServiceType" />
			</ServiceType>

			<!-- <xsl:if test="RateRequest/RateRequestInfo/ServiceTypes/Way/text() 
				= 'Ground'"> <ServiceType> <xsl:value-of select="08" /> </ServiceType> </xsl:if> -->

			<Shipper>

				<shipper_id>
					<xsl:value-of select="RateRequest/Shipper/shipper_id" />
				</shipper_id>
			</Shipper>
			<xsl:choose>
					<xsl:when
						test="RateRequest/Packages/Package/PackagingType/Code/text() = 'All'">
						<PackageType>Unknown</PackageType>
					</xsl:when>
					<xsl:otherwise>
						<PackingType>
			<PackagingTypeCode><xsl:value-of select="RateRequest/Packages/Package/PackagingType/Code" /></PackagingTypeCode>
			</PackingType>
					</xsl:otherwise>
			</xsl:choose>
			
			<Recipient>
				<!-- <Contact> <PersonName> <xsl:value-of select="RateRequest/ShipToAddress/ContactAndAddress/PersonName" 
					/> </PersonName> </Contact> -->
				<Address>
					<!-- <StreetLines> <xsl:value-of select="RateRequest/ShipToAddress/ContactAndAddress/StreetLine1" 
						/> </StreetLines> <City> <xsl:value-of select="RateRequest/ShipToAddress/ContactAndAddress/City" 
						/> </City> <StateOrProvinceCode> <xsl:value-of select="RateRequest/ShipToAddress/ContactAndAddress/StateOrProvinceCode" 
						/> </StateOrProvinceCode> -->
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
			<!-- <PackageCount> <xsl:value-of select="RateRequest/Packages/PackageCount" 
				/> </PackageCount> -->
			<RequestedPackageLineItems>

				<Weight>
					<!-- <xsl:if test="RateRequest/Packages/Package/Weight/UnitOfMeasurement/Code/text() 
						= 'LB'"> <Units> <xsl:text>LBS</xsl:text> </Units> </xsl:if> -->
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
					<!-- <Units> <xsl:value-of select="RateRequest/Packages/Package/Dimensions/Units" 
						/> </Units> -->
				</Dimensions>
			</RequestedPackageLineItems>
		</RateRequest>

	</xsl:template>
</xsl:stylesheet>