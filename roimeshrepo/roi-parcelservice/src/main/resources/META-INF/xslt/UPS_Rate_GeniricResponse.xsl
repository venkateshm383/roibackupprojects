<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:rate="http://www.ups.com/XMLSchema/XOLTWS/Rate/v1.1"
	xmlns:common="http://www.ups.com/XMLSchema/XOLTWS/Common/v1.0"
	xmlns:err="http://www.ups.com/XMLSchema/XOLTWS/Error/v1.1"
	exclude-result-prefixes="soapenv rate common err">
	<xsl:output method="xml" />
	<xsl:template match="/">

		<RateReply>
			<xsl:choose>
				<xsl:when
					test="soapenv:Envelope/soapenv:Body/rate:RateResponse/common:Response/common:ResponseStatus/common:Description/text() = 'Success'">
					<Notification>
						<xsl:value-of
							select="soapenv:Envelope/soapenv:Body/rate:RateResponse/common:Response/common:ResponseStatus/common:Description" />
					</Notification>
				</xsl:when>

				<xsl:otherwise>
					<Notification>
						<xsl:value-of
							select="soapenv:Envelope/soapenv:Body/soapenv:Fault/detail/err:Errors/err:ErrorDetail/err:Severity" />
					</Notification>
				</xsl:otherwise>
			</xsl:choose>


			<!-- <Notification> <xsl:value-of select="soapenv:Envelope/soapenv:Body/rate:RateResponse/common:Response/common:ResponseStatus/common:Description" 
				/> </Notification> -->

			<xsl:choose>
				<xsl:when
					test="soapenv:Envelope/soapenv:Body/rate:RateResponse/common:Response/common:ResponseStatus/common:Description/text() = 'Success'">
					<Code>
						<xsl:value-of
							select="soapenv:Envelope/soapenv:Body/rate:RateResponse/common:Response/common:ResponseStatus/common:Code" />
					</Code>
				</xsl:when>

				<xsl:otherwise>
					<Code>
						<xsl:value-of
							select="soapenv:Envelope/soapenv:Body/soapenv:Fault/detail/err:Errors/err:ErrorDetail/err:PrimaryErrorCode/err:Code" />
					</Code>
				</xsl:otherwise>
			</xsl:choose>



			<!-- <Code> <xsl:value-of select="soapenv:Envelope/soapenv:Body/rate:RateResponse/common:Response/common:ResponseStatus/common:Code" 
				/> </Code> -->


			<xsl:choose>
				<xsl:when
					test="soapenv:Envelope/soapenv:Body/rate:RateResponse/common:Response/common:ResponseStatus/common:Description/text() = 'Success'">
					<Message>
						<xsl:value-of
							select="soapenv:Envelope/soapenv:Body/rate:RateResponse/common:Response/common:Alert/common:Description" />
					</Message>
				</xsl:when>

				<xsl:otherwise>
					<Message>
						<xsl:value-of
							select="soapenv:Envelope/soapenv:Body/soapenv:Fault/detail/err:Errors/err:ErrorDetail/err:PrimaryErrorCode/err:Description" />
					</Message>
				</xsl:otherwise>
			</xsl:choose>


			<!-- <Message>
				<xsl:value-of
					select="soapenv:Envelope/soapenv:Body/rate:RateResponse/common:Response/common:Alert/common:Description" />
			</Message> -->

			<xsl:for-each
				select="soapenv:Envelope/soapenv:Body/rate:RateResponse/rate:RatedShipment">

				<RateReplyDetails>
					<Carrier>UPS</Carrier>
					<ServiceType>
						<xsl:value-of select="rate:Service/rate:Code" />
					</ServiceType>
					<CommitTimestamp>
						<xsl:value-of select="rate:GuaranteedDelivery/rate:BusinessDaysInTransit" />
					</CommitTimestamp>
					<DeliveryByTime>
						<xsl:value-of select="rate:GuaranteedDelivery/rate:DeliveryByTime" />
					</DeliveryByTime>

					<!-- <RatedShipmentDetails> -->
					<ShipmentRateDetail>

						<TotalBillingWeight>
							<Units>
								<xsl:value-of
									select="rate:BillingWeight/rate:UnitOfMeasurement/rate:Code" />
							</Units>
							<Value>
								<xsl:value-of select="rate:BillingWeight/rate:Weight" />
							</Value>
						</TotalBillingWeight>

						<TotalBaseCharge>
							<!-- <Currency> <xsl:value-of select="rate:TransportationCharges/rate:CurrencyCode" 
								/> </Currency> <Amount> -->
							<xsl:value-of select="rate:TransportationCharges/rate:MonetaryValue" />
							<!-- </Amount> -->
						</TotalBaseCharge>
						<TotalDiscounts>
							0.00
							<!-- <Currency>USD</Currency> -->
							<!-- <Amount>0.00</Amount> -->
						</TotalDiscounts>
						<TotalSurcharges>
							<!-- <Currency> <xsl:value-of select="rate:ServiceOptionsCharges/rate:CurrencyCode" 
								/> </Currency> <Amount> -->
							<xsl:value-of select="rate:ServiceOptionsCharges/rate:MonetaryValue" />
							<!-- </Amount> -->
						</TotalSurcharges>
						<TotalNetCharge>
							<!-- <Currency> <xsl:value-of select="rate:TotalCharges/rate:CurrencyCode" 
								/> </Currency> <Amount> -->
							<xsl:value-of select="rate:TotalCharges/rate:MonetaryValue" />
							<!-- </Amount> -->
						</TotalNetCharge>
						<TotalTaxes>
							0.00
							<!-- <Currency>USD</Currency> <Amount>0.00</Amount> -->
						</TotalTaxes>
						<TotalNetChargeWithDutiesAndTaxes>
							<!-- <Currency> <xsl:value-of select="rate:TotalCharges/rate:CurrencyCode" 
								/> </Currency> <Amount> -->
							<xsl:value-of select="rate:TotalCharges/rate:MonetaryValue" />
							<!-- </Amount> -->
						</TotalNetChargeWithDutiesAndTaxes>

					</ShipmentRateDetail>

					<!-- <RatedPackages> -->

					<!-- <GroupNumber> <xsl:value-of select="soapenv:Envelope/soapenv:Body/rate:RateReply/rate:RateReplyDetails/rate:RatedShipmentDetails/rate:RatedPackages/rate:GroupNumber" 
						/> </GroupNumber> -->

					<PackageRateDetail>
						<TotalBillingWeight>
							<Units>
								<xsl:value-of
									select="rate:BillingWeight/rate:UnitOfMeasurement/rate:Code" />
							</Units>
							<Value>
								<xsl:value-of select="rate:BillingWeight/rate:Weight" />
							</Value>
						</TotalBillingWeight>

						<TotalBaseCharge>
							<!-- <Currency> <xsl:value-of select="rate:TransportationCharges/rate:CurrencyCode" 
								/> </Currency> <Amount> -->
							<xsl:value-of select="rate:TransportationCharges/rate:MonetaryValue" />
							<!-- </Amount> -->
						</TotalBaseCharge>
						<TotalDiscounts>
							0.00
							<!-- <Currency>USD</Currency> <Amount>0.00</Amount> -->
						</TotalDiscounts>
						<TotalSurcharges>
							<!-- <Currency> <xsl:value-of select="rate:ServiceOptionsCharges/rate:CurrencyCode" 
								/> </Currency> <Amount> -->
							<xsl:value-of select="rate:ServiceOptionsCharges/rate:MonetaryValue" />
							<!-- </Amount> -->
						</TotalSurcharges>
						<TotalNetCharge>
							<!-- <Currency> <xsl:value-of select="rate:TotalCharges/rate:CurrencyCode" 
								/> </Currency> <Amount> -->
							<xsl:value-of select="rate:TotalCharges/rate:MonetaryValue" />
							<!-- </Amount> -->
						</TotalNetCharge>
						<TotalTaxes>
							0.00
							<!-- <Currency>USD</Currency> <Amount>0.00</Amount> -->
						</TotalTaxes>
						<TotalNetChargeWithDutiesAndTaxes>
							<!-- <Currency> <xsl:value-of select="rate:TotalCharges/rate:CurrencyCode" 
								/> </Currency> <Amount> -->
							<xsl:value-of select="rate:TotalCharges/rate:MonetaryValue" />
							<!-- </Amount> -->
						</TotalNetChargeWithDutiesAndTaxes>

					</PackageRateDetail>
					<!-- </RatedPackages> </RatedShipmentDetails> -->
				</RateReplyDetails>
			</xsl:for-each>
		</RateReply>

	</xsl:template>
</xsl:stylesheet>