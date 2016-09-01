<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:sws="http://stamps.com/xml/namespace/2015/12/swsim/swsimV50">
	<xsl:output method="xml" />
	<xsl:template match="/">
		<soapenv:Envelope>
			<soapenv:Header />
			<soapenv:Body>
				<sws:CreateIndicium>
					<sws:Credentials>
						<sws:IntegrationID>
							<xsl:value-of
								select="ShipmentRequest/Shipment/StampsSecurity/ServiceAccessToken/AccessLicenseNumber" />
						</sws:IntegrationID>
						<sws:Username>
							<xsl:value-of
								select="ShipmentRequest/Shipment/StampsSecurity/UsernameToken/Username" />
						</sws:Username>
						<sws:Password>
							<xsl:value-of
								select="ShipmentRequest/Shipment/StampsSecurity/UsernameToken/Password" />
						</sws:Password>
					</sws:Credentials>
					<sws:IntegratorTxID><xsl:value-of
								select="ShipmentRequest/Shipment/StampsSecurity/ServiceAccessToken/IntegratorTxID" /></sws:IntegratorTxID>
					<sws:Rate>
						<sws:FromZIPCode>
							<xsl:value-of
								select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/PostalCode " />
						</sws:FromZIPCode>
						<sws:ToZIPCode>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Address/PostalCode " />
						</sws:ToZIPCode>
						<sws:ToCountry>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Address/CountryCode " />
						</sws:ToCountry>
						<sws:ServiceType>
							<xsl:value-of select="ShipmentRequest/Shipment/ServiceType" />
						</sws:ServiceType>
						<sws:WeightLb>
							<xsl:value-of select="ShipmentRequest/Shipment/Package/Weight/Value" />
						</sws:WeightLb>
						<sws:PackageType>
							<xsl:value-of select="ShipmentRequest/Shipment/Package/Packaging/Code" />
						</sws:PackageType>
						<sws:Length>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Package/Dimensions/Length" />
						</sws:Length>
						<sws:Width>
							<xsl:value-of select="ShipmentRequest/Shipment/Package/Dimensions/Width" />
						</sws:Width>
						<sws:Height>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Package/Dimensions/Height" />
						</sws:Height>
						<sws:ShipDate><xsl:value-of
								select="ShipmentRequest/Shipment/Package/ShipDate" /></sws:ShipDate>
						<sws:NonMachinable><xsl:value-of
								select="ShipmentRequest/Shipment/Package/NonMachinable" /></sws:NonMachinable>
						<sws:RectangularShaped>true</sws:RectangularShaped>
						<sws:IsIntraBMC>false</sws:IsIntraBMC>
						<sws:Zone>0</sws:Zone>
						<sws:RateCategory>0</sws:RateCategory>
						<sws:ToState />
						<sws:CubicPricing>false</sws:CubicPricing>
					</sws:Rate>
					<sws:From>
						<sws:FullName>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Sender/Contact/PersonName " />
						</sws:FullName>
						<sws:Company>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Sender/Contact/PersonName " />
						</sws:Company>
						<sws:Address1>
							<xsl:value-of
								select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/AddressLine " />
						</sws:Address1>
						<sws:Address2>
							<xsl:value-of
								select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/AddressLine " />
						</sws:Address2>
						<sws:City>
							<xsl:value-of
								select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/City " />
						</sws:City>
						<sws:State>
							<xsl:value-of
								select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/StateOrProvinceCode " />
						</sws:State>
						<sws:ZIPCode>
							<xsl:value-of
								select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/PostalCode " />
						</sws:ZIPCode>
						<sws:Province>
							<xsl:value-of
								select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/StateOrProvinceCode " />
						</sws:Province>
						<sws:PostalCode>
							<xsl:value-of
								select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/PostalCode " />
						</sws:PostalCode>
						<sws:Country>
							<xsl:value-of
								select="ShipmentRequest/Shipment/ShipperDetails/ContactAddress/CountryCode " />
						</sws:Country>
					</sws:From>
					<sws:To>
						<sws:FullName>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Contact/PersonName " />
						</sws:FullName>
						<sws:Company>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Contact/PersonName " />
						</sws:Company>
						<sws:Address1>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Address/StreetLines " />
						</sws:Address1>
						<sws:Address2>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Address/StreetLines " />
						</sws:Address2>
						<sws:City>
							<xsl:value-of select="ShipmentRequest/Shipment/Recipient/Address/City " />
						</sws:City>
						<sws:State>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Address/StateOrProvinceCode " />
						</sws:State>
						<sws:ZIPCode>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Address/PostalCode " />
						</sws:ZIPCode>
						<sws:Province>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Address/StateOrProvinceCode " />
						</sws:Province>
						<sws:PostalCode>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Address/PostalCode " />
						</sws:PostalCode>
						<sws:Country>
							<xsl:value-of
								select="ShipmentRequest/Shipment/Recipient/Address/CountryCode " />
						</sws:Country>
					</sws:To>
					<sws:SampleOnly>false</sws:SampleOnly>
					<sws:PostageMode>Normal</sws:PostageMode>
					<sws:ImageType>
						<xsl:value-of select="ShipmentRequest/LabelImageFormat/Code" />
					</sws:ImageType>
					<sws:ImageDpi>ImageDpiDefault</sws:ImageDpi>
				</sws:CreateIndicium>
			</soapenv:Body>
		</soapenv:Envelope>
	</xsl:template>
</xsl:stylesheet>