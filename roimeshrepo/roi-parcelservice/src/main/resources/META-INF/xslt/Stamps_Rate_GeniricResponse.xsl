<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:rate="http://stamps.com/xml/namespace/2015/12/swsim/swsimV50"
	xmlns:fault="http://stamps.com/xml/namespace/2008/01/fault"
	xmlns:fault1="http://stamps.com/xml/namespace/2008/02/stamps/fault"
	exclude-result-prefixes="
	soap rate fault fault1 xsd xsi">
	<xsl:output method="xml" />
	<xsl:template match="/">

		<RateReply>
			
			
			
<xsl:choose>
  <xsl:when test="soap:Envelope/soap:Body/rate:GetRatesResponse/rate:Rates/rate:Rate"><Notification>Success</Notification><Code>0</Code><Message>
				<xsl:value-of
					select="soap:Envelope/soap:Body/rate:GetRatesResponse/rate:Authenticator" />
			</Message></xsl:when>
  <xsl:when test="soap:Envelope/soap:Body/soap:Fault">	<xsl:if test="soap:Envelope/soap:Body/soap:Fault">
		<xsl:if test="soap:Envelope/soap:Body/soap:Fault/detail/fault:sdcerror">	
	<Code><xsl:value-of
			select="soap:Envelope/soap:Body/soap:Fault/detail/fault:sdcerror/@code" /></Code>
	</xsl:if>
	<xsl:if test="soap:Envelope/soap:Body/soap:Fault/detail/fault1:stamps_exception">	
	<Code><xsl:value-of
			select="soap:Envelope/soap:Body/soap:Fault/detail/fault1:stamps_exception/@code" /></Code>
	</xsl:if>
</xsl:if>
<!-- <xsl:if test="soap:Envelope/soap:Body/soap:Fault">
		<xsl:if test="soap:Envelope/soap:Body/soap:Fault/detail/fault:sdcerror">	
	<Code><xsl:value-of
			select="soap:Envelope/soap:Body/soap:Fault/detail/fault:sdcerror/@code" /></Code>
	</xsl:if>
	<xsl:if test="soap:Envelope/soap:Body/soap:Fault/detail/fault1:stamps_exception">	
	<Code><xsl:value-of
			select="soap:Envelope/soap:Body/soap:Fault/detail/fault1:stamps_exception/@code" /></Code>
	</xsl:if>
</xsl:if> -->
<xsl:if test="soap:Envelope/soap:Body/soap:Fault"><Message>
				<xsl:value-of
					select="soap:Envelope/soap:Body/soap:Fault/faultstring" />
			</Message></xsl:if>
</xsl:when>
  <xsl:otherwise><Notification>Warning</Notification><Code>1000</Code><Message>NotAvailable for all integrations</Message></xsl:otherwise>
</xsl:choose> 
			
			
		<!-- 	<xsl:if test="soap:Envelope/soap:Body/rate:GetRatesResponse/rate:Rates/rate:Rate"><Notification>
				Success
			</Notification></xsl:if> -->
			<!-- <xsl:if test="soap:Envelope/soap:Body/rate:GetRatesResponse/rate:Rates/rate:Rate"><Notification>
				Success
			</Notification></xsl:if> -->
			
			<!-- <xsl:if test="soap:Envelope/soap:Body/soap:Fault"><Notification>ErrorOccured</Notification></xsl:if> -->
			<!-- <xsl:if test="soap:Envelope/soap:Body/rate:GetRatesResponse/rate:Authenticator"><Code>0</Code></xsl:if> -->
			
			<!-- <xsl:if test="soap:Envelope/soap:Body/rate:GetRatesResponse/rate:Authenticator">
			
			</xsl:if> -->
			
			
			<xsl:for-each
				select="soap:Envelope/soap:Body/rate:GetRatesResponse/rate:Rates/rate:Rate">

				<RateReplyDetails>
					<Carrier>STAMPS</Carrier>

					<ServiceType>
						<xsl:value-of select="rate:ServiceType" />
					</ServiceType>

					<PackageType>
						<xsl:value-of select="rate:PackageType" />
					</PackageType>

					<CommitTimestamp>
						<xsl:value-of select="rate:DeliverDays" />
					</CommitTimestamp>
					<DeliveryTimestamp>
						<xsl:value-of select="rate:DeliveryDate" />
					</DeliveryTimestamp>

					<ShipmentRateDetail>
						<TotalBillingWeight>
							<Units>LBS</Units>
							<Value>
								<xsl:value-of select="rate:WeightLb" />
							</Value>
						</TotalBillingWeight>

						<TotalBaseCharge>
							<xsl:value-of select="rate:Amount" />
						</TotalBaseCharge>
						<TotalDiscounts>0</TotalDiscounts>
						<TotalSurcharges>0</TotalSurcharges>
						<TotalNetCharge>0</TotalNetCharge>
						<TotalTaxes>0</TotalTaxes>
						<TotalNetChargeWithDutiesAndTaxes>
							<xsl:value-of select="rate:Amount" />
						</TotalNetChargeWithDutiesAndTaxes>

					</ShipmentRateDetail>

					<GroupNumber>
						<xsl:value-of select="rate:Zone" />
					</GroupNumber>

					<PackageRateDetail>
						<TotalBillingWeight>
							<Units>LBS</Units>
							<Value>
								<xsl:value-of select="rate:WeightLb" />
							</Value>
						</TotalBillingWeight>

						<TotalBaseCharge>
							<xsl:value-of select="rate:Amount" />
						</TotalBaseCharge>
						<TotalDiscounts>0</TotalDiscounts>
						<TotalSurcharges>0</TotalSurcharges>
						<TotalNetCharge>0</TotalNetCharge>
						<TotalTaxes>0</TotalTaxes>
						<TotalNetChargeWithDutiesAndTaxes>
							<xsl:value-of select="rate:Amount" />
						</TotalNetChargeWithDutiesAndTaxes>

					</PackageRateDetail>
				</RateReplyDetails>

			</xsl:for-each>

		</RateReply>

	</xsl:template>
</xsl:stylesheet>