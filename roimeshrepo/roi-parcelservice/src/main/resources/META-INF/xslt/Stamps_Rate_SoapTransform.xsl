<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:sws="http://stamps.com/xml/namespace/2015/12/swsim/swsimV50">
	<xsl:output method="xml" />

	<xsl:template match="/">

		<soapenv:Envelope>

			<soapenv:Header>
				<!-- <v1:UPSSecurity>
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
				</v1:UPSSecurity> -->
			</soapenv:Header>

	<soapenv:Body>
		<sws:GetRates>
			<sws:Credentials>
				<sws:IntegrationID><xsl:value-of select="RateRequest/StampsSecurity/ServiceAccessToken/AccessLicenseNumber" /></sws:IntegrationID>
				<sws:Username><xsl:value-of select="RateRequest/StampsSecurity/UsernameToken/Username" /></sws:Username>
				<sws:Password><xsl:value-of select="RateRequest/StampsSecurity/UsernameToken/Password" /></sws:Password>
			</sws:Credentials>
				<sws:Rate>
            <!--Optional:-->
            <sws:FromZIPCode><xsl:value-of select="RateRequest/ShipperDetails/ContactAddress/PostalCode" /></sws:FromZIPCode>
            <!--Optional:-->
            <sws:ToZIPCode><xsl:value-of select="RateRequest/Recipient/Address/PostalCode" /></sws:ToZIPCode>
            <!--Optional:-->
            <sws:ToCountry><xsl:value-of select="RateRequest/Recipient/Address/CountryCode" /></sws:ToCountry>
       
            <sws:ServiceType><xsl:value-of select="RateRequest/ServiceType" /></sws:ServiceType>
          
            <sws:WeightLb><xsl:value-of select="RateRequest/RequestedPackageLineItems/Weight/Value" /></sws:WeightLb>
          
            <sws:PackageType><xsl:value-of select="RateRequest/PackingType/PackagingTypeCode" /></sws:PackageType>
            <!--Optional:-->
          
            <!--Optional:-->
            <sws:Length><xsl:value-of select="RateRequest/RequestedPackageLineItems/Dimensions/Length" /></sws:Length>
            <!--Optional:-->
            <sws:Width><xsl:value-of select="RateRequest/RequestedPackageLineItems/Dimensions/Width" /></sws:Width>
            <!--Optional:-->
            <sws:Height><xsl:value-of select="RateRequest/RequestedPackageLineItems/Dimensions/Height" /></sws:Height>
            <!--Optional:-->
            <sws:ShipDate><xsl:value-of select="RateRequest/CurrentDate/CurrentDateYYYYMMDD" /></sws:ShipDate>
         
          
            <sws:NonMachinable><xsl:value-of select="RateRequest/NonMachinable/NonMachinableCode" /></sws:NonMachinable>
            <!--Optional:-->
            <sws:RectangularShaped><xsl:value-of select="RateRequest/RectangularShaped/RectangularShapedCode" /></sws:RectangularShaped>
           
            <sws:IsIntraBMC><xsl:value-of select="RateRequest/IsIntraBMC/IsIntraBMCCode" /></sws:IsIntraBMC>
            <!--Optional:-->
            <sws:Zone><xsl:value-of select="RateRequest/Zone/ZoneCode" /></sws:Zone>
            <!--Optional:-->
            <sws:RateCategory><xsl:value-of select="RateRequest/RateCategory/RateCategoryCode" /></sws:RateCategory>
            <!--Optional:-->
            <sws:ToState/>
            <!--Optional:-->
            <sws:CubicPricing><xsl:value-of select="RateRequest/CubicPricing/CubicPricingCode" /></sws:CubicPricing>
         </sws:Rate>
      </sws:GetRates>
			</soapenv:Body>
		</soapenv:Envelope>

	</xsl:template>
</xsl:stylesheet>