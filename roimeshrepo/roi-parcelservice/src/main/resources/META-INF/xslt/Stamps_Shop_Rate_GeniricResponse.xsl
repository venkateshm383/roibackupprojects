<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:rate="http://stamps.com/xml/namespace/2015/12/swsim/swsimV50"
	exclude-result-prefixes="
	soap rate">
	<xsl:output method="xml" />
	<xsl:template match="/">

		<RateReply>
			<Notification>
				<xsl:value-of
					select="soap:Envelope/soap:Body/rate:GetRatesResponse/rate:Authenticator" />
			</Notification>
			<Code>0</Code>
			<Message>
				<xsl:value-of
					select="soap:Envelope/soap:Body/rate:GetRatesResponse/rate:Authenticator" />
			</Message>

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