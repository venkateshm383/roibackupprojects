<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="utf-16"/>
	<xsl:template match="/">
		<ServiceChannelNotification>
			<RequestData2SC>
				<StoreID>XYZ-001</StoreID>
				<CustomerID>ABC</CustomerID>
				<DATA2SC>
					<xsl:attribute name="PIN"><xsl:value-of
						select="Event/SRSourceProviderId" /></xsl:attribute>
					<xsl:attribute name="ID"><xsl:value-of
						select="Event/SRSourceTransactionId" /></xsl:attribute>
					<CALL>
						<xsl:if test="Event/SRSourceRequestId">
							<xsl:attribute name="WO_NUM"><xsl:value-of
								select="Event/SRSourceRequestId" /></xsl:attribute>
						</xsl:if>
						<xsl:if test="Event/SRSourceRequestId">
							<xsl:attribute name="TR_NUM"><xsl:value-of
								select="Event/SRSourceRequestId" /></xsl:attribute>
						</xsl:if>
						<xsl:if test="Event/SRSourceStatus">
							<xsl:attribute name="STATUS"><xsl:value-of
								select="Event/SRSourceStatus" /></xsl:attribute>
						</xsl:if>
					</CALL>
				</DATA2SC>
			</RequestData2SC>
			<Payload>
				<Event>
					<xsl:if test="Event/EventId">
						<EventId>
							<xsl:value-of select="Event/EventId" />
						</EventId>
					</xsl:if>
					<xsl:if test="Event/TenantId">
						<TenantId>
							<xsl:value-of select="Event/EventId" />
						</TenantId>
					</xsl:if>
					<xsl:if test="Event/SRSourceType">
						<SRSourceType>
							<xsl:value-of select="Event/EventId" />
						</SRSourceType>
					</xsl:if>
					<xsl:if test="Event/SRSourceRequestId">
						<SRSourceRequestId>
							<xsl:value-of select="Event/SRSourceRequestId" />
						</SRSourceRequestId>
					</xsl:if>
					<xsl:if test="Event/SRSourceTransactionId">
						<SRSourceTransactionId>
							<xsl:value-of select="Event/SRSourceTransactionId" />
						</SRSourceTransactionId>
					</xsl:if>
					<xsl:if test="Event/SRSourceProviderId">
						<SRSourceProviderId>
							<xsl:value-of select="Event/SRSourceProviderId" />
						</SRSourceProviderId>
					</xsl:if>
					<xsl:if test="Event/SRSourceStatus">
						<SRSourceStatus>
							<xsl:value-of select="Event/SRSourceStatus" />
						</SRSourceStatus>
					</xsl:if>
					<xsl:if test="Event/SRSourceStatus">
						<SRSourceStatus>
							<xsl:value-of select="Event/SRSourceStatus" />
						</SRSourceStatus>
					</xsl:if>
					<xsl:if test="Event/SRInternalStatus">
						<SRInternalStatus>
							<xsl:value-of select="Event/SRInternalStatus" />
						</SRInternalStatus>
					</xsl:if>
					<xsl:if test="Event/EventRaisedDTM">
						<EventRaisedDTM>
							<xsl:value-of select="Event/EventRaisedDTM" />
						</EventRaisedDTM>
					</xsl:if>
				</Event>
			</Payload>
		</ServiceChannelNotification>
	</xsl:template>
</xsl:stylesheet>