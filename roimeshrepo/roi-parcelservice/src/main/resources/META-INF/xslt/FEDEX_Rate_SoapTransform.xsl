<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns="http://fedex.com/ws/rate/v18">
	<xsl:output method="xml" />

	<xsl:template match="/">

		<SOAP-ENV:Envelope>
			<SOAP-ENV:Body>
				<RateRequest>

					<WebAuthenticationDetail>
						<UserCredential>
							<Key>
								<xsl:value-of
									select="RateRequest/WebAuthenticationDetail/UserCredential/Key" />
							</Key>
							<Password>
								<xsl:value-of
									select="RateRequest/WebAuthenticationDetail/UserCredential/Password" />
							</Password>
						</UserCredential>
					</WebAuthenticationDetail>

					<ClientDetail>
						<AccountNumber>
							<xsl:value-of select="RateRequest/ClientDetail/AccountNumber" />
						</AccountNumber>
						<MeterNumber>
							<xsl:value-of select="RateRequest/ClientDetail/MeterNumber" />
						</MeterNumber>
					</ClientDetail>

					<TransactionDetail>
						<!-- <CustomerTransactionId>
							<xsl:value-of
								select="RateRequest/TransactionDetail/CustomerTransactionId" />
						</CustomerTransactionId> -->
					</TransactionDetail>

					<Version>
						<ServiceId>
							<xsl:value-of select="RateRequest/Version/ServiceId" />
						</ServiceId>
						<Major>
							<xsl:value-of select="RateRequest/Version/Major" />
						</Major>
						<Intermediate>
							<xsl:value-of select="RateRequest/Version/Intermediate" />
						</Intermediate>
						<Minor>
							<xsl:value-of select="RateRequest/Version/Minor" />
						</Minor>
					</Version>

					<ReturnTransitAndCommit>
						<xsl:value-of select="RateRequest/ReturnTransitAndCommit" />
					</ReturnTransitAndCommit>

					<RequestedShipment>

						<ServiceType>
							<xsl:value-of select="RateRequest/RequestedShipment/ServiceType" />
						</ServiceType> 
						<PackagingType>
							<xsl:value-of
								select="RateRequest/RequestedShipment/PackagingType" />
						</PackagingType>
						<Shipper>
							<Contact>
								<CompanyName>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/CompanyName" />
								</CompanyName>
								<PhoneNumber>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/PhoneNumber" />
								</PhoneNumber>
							</Contact>
							<Address>
								<StreetLines>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/StreetLine1" />
								</StreetLines>
								<StreetLines>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/StreetLine2" />
								</StreetLines>
								<City>
									<xsl:value-of select="RateRequest/ShipperDetails/ContactAddress/City" />
								</City>
								<StateOrProvinceCode>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/StateOrProvinceCode" />
								</StateOrProvinceCode>
								<PostalCode>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/PostalCode" />
								</PostalCode>
								<CountryCode>
									<xsl:value-of
										select="RateRequest/ShipperDetails/ContactAddress/CountryCode" />
								</CountryCode>
							</Address>
						</Shipper>

						<Recipient>
							<Contact>
								<PersonName>
									<xsl:value-of
										select="RateRequest/RequestedShipment/Recipient/Contact/PersonName" />
								</PersonName>
								<CompanyName>
									<xsl:value-of
										select="RateRequest/RequestedShipment/Recipient/Contact/CompanyName" />
								</CompanyName>
								<PhoneNumber>
									<xsl:value-of
										select="RateRequest/RequestedShipment/Recipient/Contact/PhoneNumber" />
								</PhoneNumber>
							</Contact>
							<Address>
								<StreetLines>
									<xsl:value-of
										select="RateRequest/RequestedShipment/Recipient/Address/StreetLines" />
								</StreetLines>
								<StreetLines>
									<xsl:value-of
										select="RateRequest/RequestedShipment/Recipient/Address/StreetLines" />
								</StreetLines>
								<City>
									<xsl:value-of
										select="RateRequest/RequestedShipment/Recipient/Address//City" />
								</City>
								<StateOrProvinceCode>
									<xsl:value-of
										select="RateRequest/RequestedShipment/Recipient/Address//StateOrProvinceCode" />
								</StateOrProvinceCode>
								<PostalCode>
									<xsl:value-of
										select="RateRequest/RequestedShipment/Recipient/Address/PostalCode" />
								</PostalCode>
								<CountryCode>
									<xsl:value-of
										select="RateRequest/RequestedShipment/Recipient/Address/CountryCode" />
								</CountryCode>
							</Address>
						</Recipient>

						<PackageCount>
							<xsl:value-of select="RateRequest/RequestedShipment/PackageCount" />
						</PackageCount>
						<RequestedPackageLineItems>

							<GroupPackageCount>
								<xsl:value-of select="RateRequest/RequestedShipment/PackageCount" />
							</GroupPackageCount>
							<Weight>
								<Units>
									<xsl:value-of
										select="RateRequest/RequestedShipment/RequestedPackageLineItems/Weight/Units" />
								</Units>
								<Value>
									<xsl:value-of
										select="RateRequest/RequestedShipment/RequestedPackageLineItems/Weight/Value" />
								</Value>
							</Weight>
							<Dimensions>
								<Length>
									<xsl:value-of
										select="RateRequest/RequestedShipment/RequestedPackageLineItems/Dimensions/Length" />
								</Length>
								<Width>
									<xsl:value-of
										select="RateRequest/RequestedShipment/RequestedPackageLineItems/Dimensions/Width" />
								</Width>
								<Height>
									<xsl:value-of
										select="RateRequest/RequestedShipment/RequestedPackageLineItems/Dimensions/Height" />
								</Height>
								<Units>
									<xsl:value-of
										select="RateRequest/RequestedShipment/RequestedPackageLineItems/Dimensions/Units" />
								</Units>
							</Dimensions>
						</RequestedPackageLineItems>

					</RequestedShipment>

				</RateRequest>
			</SOAP-ENV:Body>
		</SOAP-ENV:Envelope>

	</xsl:template>
</xsl:stylesheet>
