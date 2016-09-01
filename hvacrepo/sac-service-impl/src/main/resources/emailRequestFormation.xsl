<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" />
	<xsl:output encoding="utf-16"/>
	<xsl:template match="/">
		<EmailNotification>
			<Subject></Subject>
			<Body></Body>
			<xsl:if test="Event/Attachments">
			<Attachments>
				<xsl:for-each select="Event/Attachments/Attachment">
					<Attachment>
						<xsl:attribute name="filename">
							<xsl:value-of select="filename" />
						</xsl:attribute>
						<xsl:attribute name="contentType">
							<xsl:value-of select="contentType" />
						</xsl:attribute>
						<xsl:value-of select="file" />
					</Attachment>
				</xsl:for-each>
			</Attachments>
			</xsl:if>
			<Sender></Sender>
			<Recipients>
			</Recipients>
			<Payload>
				<Event>
					<xsl:attribute name="ID">
						<xsl:value-of select="Event/ID" />
					</xsl:attribute>
					<SRNumber>
						<xsl:value-of select="Event/SRNumber" />
					</SRNumber>
					<SRLocation>
						<xsl:value-of select="Event/SRLocation" />
					</SRLocation>
					<CustNumber>
						<xsl:value-of select="Event/CustNumber" />
					</CustNumber>
					<xsl:if test="Event/Attachments">
						<Attachments>
							<xsl:for-each select="Event/Attachments/Attachment">
								<Attachment>
									<xsl:attribute name="filename">
									<xsl:value-of select="filename" />
										</xsl:attribute>
									<xsl:attribute name="contentType">
									<xsl:value-of select="contentType" /></xsl:attribute>
									<xsl:value-of select="file" />
								</Attachment>
							</xsl:for-each>
						</Attachments>
					</xsl:if>
				</Event>
			</Payload>
		</EmailNotification>
	</xsl:template>
</xsl:stylesheet>