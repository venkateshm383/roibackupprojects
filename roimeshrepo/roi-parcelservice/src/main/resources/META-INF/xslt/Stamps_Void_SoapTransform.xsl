<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:sws="http://stamps.com/xml/namespace/2015/12/swsim/swsimV50">
	<xsl:output method="xml" />

	<xsl:template match="/">
		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
			xmlns:sws="http://stamps.com/xml/namespace/2015/12/swsim/swsimV50">
			<soapenv:Header />
			<soapenv:Body>
				<sws:CancelIndicium>
					<sws:Credentials>
						<sws:IntegrationID><xsl:value-of
								select="Void/StampsSecurity/ServiceAccessToken/AccessLicenseNumber" /></sws:IntegrationID>
						<sws:Username><xsl:value-of select="Void/StampsSecurity/UsernameToken/Username" /></sws:Username>
						<sws:Password><xsl:value-of select="Void/StampsSecurity/UsernameToken/Password" /></sws:Password>
					</sws:Credentials>
				<!-- sws:StampsTxID is used when Postage is invalid/insufficient, if then we have to call PurchasePostage -->
				 <sws:TrackingNumber><xsl:value-of select="Void/Request/TrackingId/PackageTrackingNumber" /></sws:TrackingNumber>
					<!-- <sws:StampsTxID><xsl:value-of select="Void/Request/TrackingId/PackageTrackingNumber" /></sws:StampsTxID> -->
				</sws:CancelIndicium>
			</soapenv:Body>
		</soapenv:Envelope>
	</xsl:template>
</xsl:stylesheet>
