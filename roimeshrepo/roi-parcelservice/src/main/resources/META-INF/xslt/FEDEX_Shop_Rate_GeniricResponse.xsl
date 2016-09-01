<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:rate="http://fedex.com/ws/rate/v18" exclude-result-prefixes="SOAP-ENV rate">
    <xsl:output method="xml" />
    <xsl:template match="/">

        <RateReply>
            <Notification>
                <xsl:value-of
                    select="SOAP-ENV:Envelope/SOAP-ENV:Body/rate:RateReply/rate:Notifications/rate:Severity" />
            </Notification>
            <Code>
                <xsl:value-of
                    select="SOAP-ENV:Envelope/SOAP-ENV:Body/rate:RateReply/rate:Notifications/rate:Code" />
            </Code>
            <Message>
                <xsl:value-of
                    select="SOAP-ENV:Envelope/SOAP-ENV:Body/rate:RateReply/rate:Notifications/rate:Message" />
            </Message>

            <xsl:for-each
                select="SOAP-ENV:Envelope/SOAP-ENV:Body/rate:RateReply/rate:RateReplyDetails">

                <RateReplyDetails>
                    <Carrier>FEDEX</Carrier>

                    <ServiceType>
                        <xsl:value-of select="rate:ServiceType" />
                    </ServiceType>

                    <CommitTimestamp>
                        <xsl:value-of select="rate:CommitDetails/rate:CommitTimestamp" />
                    </CommitTimestamp>
                    <DeliveryTimestamp>
                        <xsl:value-of select="rate:DeliveryTimestamp" />
                    </DeliveryTimestamp>

                 <!--    <RatedShipmentDetails> -->

                        <ShipmentRateDetail>
                            <TotalBillingWeight>
                                <Units>
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalBillingWeight/rate:Units" />
                                </Units>
                                <Value>
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalBillingWeight/rate:Value" />
                                </Value>
                            </TotalBillingWeight>

                            <TotalBaseCharge>
                               <!--  <Currency>
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalBaseCharge/rate:Currency" />
                                </Currency> -->
                                <!-- <Amount> -->
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalBaseCharge/rate:Amount" />
                                <!-- </Amount> -->
                            </TotalBaseCharge>
                            <TotalDiscounts>
                               <!--  <Currency>
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalFreightDiscounts/rate:Currency" />
                                </Currency>
                                <Amount> -->
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalFreightDiscounts/rate:Amount" />
                               <!--  </Amount> -->
                            </TotalDiscounts>
                            <TotalSurcharges>
                               <!--  <Currency>
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalSurcharges/rate:Currency" />
                                </Currency>
                                <Amount> -->
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalSurcharges/rate:Amount" />
                                <!-- </Amount> -->
                            </TotalSurcharges>
                            <TotalNetCharge>
                                <!-- <Currency>
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalNetCharge/rate:Currency" />
                                </Currency>
                                <Amount> -->
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalNetCharge/rate:Amount" />
                                <!-- </Amount> -->
                            </TotalNetCharge>
                            <TotalTaxes>
                                <!-- <Currency>
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalTaxes/rate:Currency" />
                                </Currency>
                                <Amount> -->
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalTaxes/rate:Amount" />
                                <!-- </Amount> -->
                            </TotalTaxes>
                            <TotalNetChargeWithDutiesAndTaxes>
                                <!-- <Currency>
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalNetChargeWithDutiesAndTaxes/rate:Currency" />
                                </Currency>
                                <Amount> -->
                                    <xsl:value-of
                                        select="rate:RatedShipmentDetails/rate:ShipmentRateDetail/rate:TotalNetChargeWithDutiesAndTaxes/rate:Amount" />
                               <!--  </Amount> -->
                            </TotalNetChargeWithDutiesAndTaxes>

                        </ShipmentRateDetail>

                     <!--    <RatedPackages> -->

                            <GroupNumber>
                                <xsl:value-of
                                    select="rate:RatedShipmentDetails/rate:RatedPackages/rate:GroupNumber" />
                            </GroupNumber>

                            <PackageRateDetail>
                                <TotalBillingWeight>
                                    <Units>
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:BillingWeight/rate:Units" />
                                    </Units>
                                    <Value>
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:BillingWeight/rate:Value" />
                                    </Value>
                                </TotalBillingWeight>

                                <TotalBaseCharge>
                                    <!-- <Currency>
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:BaseCharge/rate:Currency" />
                                    </Currency>
                                    <Amount> -->
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:BaseCharge/rate:Amount" />
                                   <!--  </Amount> -->
                                </TotalBaseCharge>
                                <TotalDiscounts>
                                 <!--    <Currency>
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:TotalFreightDiscounts/rate:Currency" />
                                    </Currency>
                                    <Amount> -->
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:TotalFreightDiscounts/rate:Amount" />
                                   <!--  </Amount> -->
                                </TotalDiscounts>
                                <TotalSurcharges>
                                    <!-- <Currency>
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:TotalSurcharges/rate:Currency" />
                                    </Currency>
                                    <Amount> -->
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:TotalSurcharges/rate:Amount" />
                                   <!--  </Amount>-->
                                </TotalSurcharges> 
                                <TotalNetCharge>
                                   <!--  <Currency>
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:NetCharge/rate:Currency" />
                                    </Currency>
                                    <Amount> -->
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:NetCharge/rate:Amount" />
                                   <!--  </Amount> -->
                                </TotalNetCharge>
                                <TotalTaxes>
                                    <!-- <Currency>
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:TotalTaxes/rate:Currency" />
                                    </Currency>
                                    <Amount> -->
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:TotalTaxes/rate:Amount" />
                                   <!--  </Amount> -->
                                </TotalTaxes>
                                <TotalNetChargeWithDutiesAndTaxes>
                                   <!--  <Currency>
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:NetCharge/rate:Currency" />
                                    </Currency>
                                    <Amount> -->
                                        <xsl:value-of
                                            select="rate:RatedShipmentDetails/rate:RatedPackages/rate:PackageRateDetail/rate:NetCharge/rate:Amount" />
                                   <!--  </Amount> -->
                                </TotalNetChargeWithDutiesAndTaxes>

                            </PackageRateDetail>
                        <!-- </RatedPackages> -->

                   <!--  </RatedShipmentDetails> -->

                </RateReplyDetails>

            </xsl:for-each>

        </RateReply>

    </xsl:template>
</xsl:stylesheet>