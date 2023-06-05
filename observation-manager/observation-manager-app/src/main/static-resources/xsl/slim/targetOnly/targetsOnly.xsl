<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:external="http://ExternalFunction.xalan-c++.xml.apache.org" exclude-result-prefixes="external">

	<!-- Formatting Angle -->

	<xsl:template name="angle">

		<xsl:param name="angle"/>

		<xsl:value-of select="$angle"/>

		<xsl:choose>

			<xsl:when test="$angle[@unit='arcsec']">&#8221;</xsl:when>

			<xsl:when test="$angle[@unit='arcmin']">&#8242;</xsl:when>

			<xsl:when test="$angle[@unit='deg']">&#176;</xsl:when>

			<xsl:when test="$angle[@unit='rad']"> rad</xsl:when>

		</xsl:choose>

	</xsl:template>

	

	<xsl:template match="target">
		
			<table border="0" cellspacing="3" cellpadding="3" width="90%" style="font-size:14;font-family:Verdana,Arial">
					
				<tr>

					<td valign="top" width="20%" bgcolor="#AAC6FF">

<xsl:text disable-output-escaping="yes">&lt;a name="target</xsl:text>

			<xsl:value-of select="@id"/>

			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

						<xsl:if test="count(constellation)>0">
							<b>Constellation:<br/></b><ul><li type="square"><xsl:value-of select="constellation"/></li></ul>
						</xsl:if>
						<xsl:if test="count(constellation)=0">
							<b>No constellation</b>
						</xsl:if>
												
					</td>

					<td width="80%"></td>

				</tr>

				<tr>

					<td width="20%"></td>

					<td valign="top" width="80%" halign="left">

						<table border="1" cellspacing="0" cellpadding="2" width="60%" style="font-size:14;font-family:Verdana,Arial">

							<tr>

								<td>
									<b>Object: </b>

									<xsl:choose>

										<xsl:when test="@type='oal:PlanetTargetType' or @type='oal:MoonTargetType' or  @type='oal:SunTargetType'">

											<xsl:choose>
												<xsl:when test="name='SUN'">Sun</xsl:when>
												<xsl:when test="name='MERCURY'">Mercury</xsl:when>
												<xsl:when test="name='VENUS'">Venus</xsl:when>
												<xsl:when test="name='EARTH'">Earth</xsl:when>
												<xsl:when test="name='MOON'">Moon</xsl:when>
												<xsl:when test="name='MARS'">Mars</xsl:when>
												<xsl:when test="name='JUPITER'">Jupiter</xsl:when>
												<xsl:when test="name='SATURN'">Saturn</xsl:when>
												<xsl:when test="name='URANUS'">Uranus</xsl:when>
												<xsl:when test="name='NEPTUNE'">Neptune</xsl:when>

												<xsl:otherwise>
													<xsl:value-of select="name"/>
												</xsl:otherwise>

											</xsl:choose>

										</xsl:when>

										<xsl:otherwise>
											<xsl:value-of select="name"/>
										</xsl:otherwise>

									</xsl:choose>

								</td>

							</tr>

							<tr>

								<td>
									<b>Type: </b>
									
									<xsl:choose>

										<xsl:when test="@type='oal:deepSkyGX'">Galaxy</xsl:when>
										<xsl:when test="@type='oal:deepSkyGC'">Globular Cluster</xsl:when>
										<xsl:when test="@type='oal:deepSkyGN'">Diffuse Nebula<xsl:if test="boolean(nebulaType)"><span style="font-size:9;"> (<xsl:value-of select="nebulaType"/>)</span></xsl:if></xsl:when>
										<xsl:when test="@type='oal:deepSkyOC'">Open Cluster</xsl:when>
										<xsl:when test="@type='oal:deepSkyPN'">Planetary Nebula</xsl:when>
										<xsl:when test="@type='oal:deepSkyQS'">Quasar</xsl:when>
										<xsl:when test="@type='oal:deepSkyDS'">Double Star</xsl:when>
										<xsl:when test="@type='oal:deepSkyDN'">Dark Nebula</xsl:when>
										<xsl:when test="@type='oal:deepSkyAS'">Asterism</xsl:when>
										<xsl:when test="@type='oal:deepSkySC'">Star cloud</xsl:when>
										<xsl:when test="@type='oal:deepSkyMS'">Multiple star system</xsl:when>
										<xsl:when test="@type='oal:deepSkyCG'">Cluster of galaxies</xsl:when>
										<xsl:when test="@type='oal:variableStarTargetType'">Variable star</xsl:when>
										<xsl:when test="@type='oal:SunTargetType'">Sun</xsl:when>
										<xsl:when test="@type='oal:MoonTargetType'">Moon</xsl:when>
										<xsl:when test="@type='oal:PlanetTargetType'">Planet</xsl:when>
										<xsl:when test="@type='oal:MinorPlanetTargetType'">Minor Planet</xsl:when>
										<xsl:when test="@type='oal:CometTargetType'">Comet</xsl:when>
										<xsl:when test="@type='oal:UndefinedTargetType'">(other Object)</xsl:when>

										<xsl:otherwise>(unknown Type)</xsl:otherwise>

									</xsl:choose>

								</td>

							</tr>

							<xsl:if test="count(alias)>0">

								<tr>

									<td>
										<b>Alias: </b>
										<xsl:for-each select="alias">

											<xsl:value-of select="."/>

											<xsl:if test="position() != last()">, </xsl:if>

										</xsl:for-each>

									</td>

								</tr>

							</xsl:if>

							<tr>

								<td>
									<b>Position: </b>

									<xsl:if test="boolean(position/ra)">

										<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

											<tr>

												<td>RA: </td>

												<td>

													<xsl:call-template name="formatHHMM">

														<xsl:with-param name="node" select="position/ra"/>

													</xsl:call-template>

												</td>

											</tr>

										</table>

									</xsl:if>

									<xsl:if test="boolean(position/dec)">

										<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

											<tr>

												<td>Dec: </td>

												<td>

													<xsl:call-template name="formatDDMM">

														<xsl:with-param name="node" select="position/dec"/>

													</xsl:call-template>

												</td>

											</tr>

										</table>

									</xsl:if>

								</td>

							</tr>

							<!-- Output from attributes of Subclasses -->
							<xsl:if test="contains(@type,'oal:deepSky')">
								<!-- Deep Sky -->
								<xsl:if test="boolean(smallDiameter) and boolean(largeDiameter)">
									<tr>
										<td>
											<b>Size: </b>
											<xsl:call-template name="angle">
												<xsl:with-param name="angle" select="smallDiameter"/>
											</xsl:call-template> &#215;
											<xsl:call-template name="angle">
												<xsl:with-param name="angle" select="largeDiameter"/>
											</xsl:call-template>
										</td>
									</tr>
								</xsl:if>
								<xsl:if test="boolean(visMag)">
									<tr>
										<td>
											<b>Magnitude (vis): </b> <xsl:value-of select="visMag"/> mag</td>
									</tr>
								</xsl:if>
								<xsl:if test="boolean(surfBr)">
									<tr>
										<td><b>Surface brightness:</b>	<xsl:value-of select="surfBr"/> mag/arcmin²</td>
									</tr>
								</xsl:if>
							</xsl:if>
							<xsl:if test="boolean(observer)">
								<tr>
									<td>
										<b>Origin: </b>
										<xsl:text disable-output-escaping="yes">&lt;a href="#observer</xsl:text>
										<xsl:value-of select="observer"/>
										<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
										<xsl:value-of select="key('observerKey', observer)/surname"/>,
										<xsl:text/>
										<xsl:value-of select="key('observerKey', observer)/name"/>
										<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
									</td>
								</tr>
							</xsl:if>
							<xsl:if test="boolean(datasource)">
								<tr>
									<td>
										<b>Origin: </b><xsl:value-of select="datasource"/></td>
								</tr>
							</xsl:if>
							<xsl:if test="not(contains(./@type,'oal:observationTargetType') or contains(./@type,'oal:PlanetTargetType') or contains(./@type,'oal:MoonTargetType') or contains(./@type,'oal:SunTargetType') or contains(./@type,'oal:MinorPlanetTargetType') or contains(./@type,'oal:CometTargetType'))">
			
			<tr>

					<td><b>DSS Fields: </b>

					<span style="font-size:10;">
					
			<xsl:text disable-output-escaping="yes">&#91;&lt;a href="http://archive.stsci.edu/cgi-bin/dss_search?v=phase2_gsc1&#38;r=</xsl:text><xsl:value-of select="position/ra"/><xsl:text disable-output-escaping="yes">&#38;d=</xsl:text><xsl:value-of select="position/dec"/><xsl:text disable-output-escaping="yes">&#38;e=J2000&#38;h=30.0&#38;w=30.0&#38;f=gif&#38;c=none&#38;fov=NONE&#38;v3=" style="text-decoration:none;" title="GIF 1000px &#8226; HST mag 16 &#8226; &#169;2001 STScI Digitized Sky Survey" target="DSS" onclick="window.open('','DSS','width=550,height=550,menubars=no,toolbars=no,directories=no,resizable=yes,scrollbars=yes,left=50,top=50')"&gt;</xsl:text><xsl:value-of select="name"/><xsl:text disable-output-escaping="yes"> (GSC1)&lt;/a&gt;&#93; &#8226; &#91;&lt;a href="http://archive.stsci.edu/cgi-bin/dss_search?v=phase2_gsc2&#38;r=</xsl:text><xsl:value-of select="position/ra"/><xsl:text disable-output-escaping="yes">&#38;d=</xsl:text><xsl:value-of select="position/dec"/><xsl:text disable-output-escaping="yes">&#38;e=J2000&#38;h=30.0&#38;w=30.0&#38;f=gif&#38;c=none&#38;fov=NONE&#38;v3=" style="text-decoration:none;" title="GIF 1800px &#8226; HST mag 21 &#8226; &#169;2008 STScI Digitized Sky Survey" target="DSS" onclick="window.open('','DSS','width=550,height=550,menubars=no,toolbars=no,directories=no,resizable=yes,scrollbars=yes,left=50,top=50')"&gt;</xsl:text><xsl:value-of select="name"/><xsl:text disable-output-escaping="yes"> (GSC2)&lt;/a&gt;&#93;</xsl:text>
</span>
</td>

				</tr>

			</xsl:if>							
<tr>
							<td align="right"><span style="font-size:9;"><a href="#obslist">Back to list</a></span>
							</td>
							</tr>		
						</table>

					</td>

				</tr>
					
			</table>		

	</xsl:template>

	

	<xsl:template name="formatHHMM">

		<xsl:param name="node"/>

                

		<xsl:param name="hrs"><xsl:value-of select='floor($node div 15)'/></xsl:param>

		<xsl:param name="hrs_rest"><xsl:value-of select='$node - ($hrs * 15)'/></xsl:param>

		<xsl:param name="minutes"><xsl:value-of select='floor($hrs_rest * 60 div 15)'/></xsl:param>

		<xsl:param name="minutes_rest"><xsl:value-of select='$hrs_rest - ($minutes div 60 * 15)'/></xsl:param>

                <xsl:param name="sec"><xsl:value-of select='round($minutes_rest * 3600 div 15)'/></xsl:param>

		<result><xsl:value-of select="$hrs"/>h <xsl:if test="$minutes &lt; 10">0</xsl:if><xsl:value-of select="$minutes"/>mn <xsl:if test="$sec  &lt; 10">0</xsl:if><xsl:value-of select="$sec"/>s</result>

	</xsl:template>

	

	

	<xsl:template name="formatDDMM">

		<xsl:param name="node"/>

		<xsl:if test='$node &lt; 0'> 

			<xsl:call-template name="formatDDMM_lower">

				<xsl:with-param name="node" select="$node"/>

			</xsl:call-template>

		</xsl:if>

		<xsl:if test="$node &gt; 0 or $node = 0">

			<xsl:call-template name="formatDDMM_higher">

				<xsl:with-param name="node" select="$node"/>

			</xsl:call-template>

		</xsl:if> 

	</xsl:template>

	

        

	<xsl:template name="formatDDMM_lower">

		<xsl:param name="node"/>



		<xsl:param name="abs_degrees"><xsl:value-of select='- $node'/></xsl:param>

		<xsl:param name="degs"><xsl:value-of select='floor($abs_degrees)'/></xsl:param>				

		<xsl:param name="degs_rest"><xsl:value-of select='$abs_degrees -  $degs'/></xsl:param>

		

		<xsl:param name="minutes"><xsl:value-of select='floor(60 * ($degs_rest))'/></xsl:param>

		<xsl:param name="minutes_rest"><xsl:value-of select='$degs_rest - ($minutes div 60)'/></xsl:param>

		

		<xsl:param name="sec"><xsl:value-of select='round($minutes_rest * 3600)'/></xsl:param>

		

		<result>-<xsl:value-of select="$degs"/><xsl:text>&#176; </xsl:text><xsl:if test="$minutes &lt; 10">0</xsl:if><xsl:value-of select="$minutes"/><xsl:text>&apos; </xsl:text><xsl:if test="$sec &lt; 10">0</xsl:if><xsl:value-of select="$sec"/><xsl:text>&quot;</xsl:text></result>

	</xsl:template>

        

        

	<xsl:template name="formatDDMM_higher">

		<xsl:param name="node"/>



		<xsl:param name="degs"><xsl:value-of select='floor($node)'/></xsl:param>				

		<xsl:param name="degs_rest"><xsl:value-of select='$node -  $degs'/></xsl:param>

		

		<xsl:param name="minutes"><xsl:value-of select='floor(60 * ($degs_rest))'/></xsl:param>

		<xsl:param name="minutes_rest"><xsl:value-of select='$degs_rest - ($minutes div 60)'/></xsl:param>

		

		<xsl:param name="sec"><xsl:value-of select='round($minutes_rest * 3600)'/></xsl:param>

		

		<result><xsl:value-of select="$degs"/><xsl:text>&#176; </xsl:text><xsl:if test="$minutes &lt; 10">0</xsl:if><xsl:value-of select="$minutes"/><xsl:text>&apos; </xsl:text><xsl:if test="$sec &lt; 10">0</xsl:if><xsl:value-of select="$sec"/><xsl:text>&quot;</xsl:text></result>

	</xsl:template> 	

        

	<xsl:template match="observer">

		<p>

			<xsl:text disable-output-escaping="yes">&lt;a name="observer</xsl:text>

			<xsl:value-of select="@id"/>

			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

			<b>Observer: </b>

			<xsl:value-of select="name"/>

			<xsl:text> </xsl:text>

		     <xsl:value-of select="surname"/>

			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

		</p>

		<xsl:if test="count(contact) > 0">Contacts: <br/>

			<ul>

				<xsl:for-each select="contact">

					<li>

						<xsl:value-of select="."/>

					</li>

				</xsl:for-each>

			</ul>

		</xsl:if>

		<xsl:call-template name="linkTop"/>

	</xsl:template>

	

	<xsl:output method="html"/>

	<!-- mit diesen Schluesselelementen loesen wir die Referenzen komfortabel wieder auf :-) -->

	<xsl:key name="targetKey" match="targets/target" use="@id"/>

	<xsl:key name="observerKey" match="observers/observer" use="@id"/>

	<xsl:template match="/">

		<HTML>

			<HEAD>

				<TITLE>Object List</TITLE>

			</HEAD>

			<BODY>

				<div align="center" style="font-size:24;font-family:Verdana,Arial;color:#0000C0">Object List</div>

				<div style="font-size:12;font-family:Verdana, Arial">

					<!-- Beobachtungen in Dokumentenreihenfolge ausgeben -->

					<a name="objectList"/>

					<h3><u>Catalog objects remaining to observe (<xsl:value-of select="count(//targets/target)"/>):</u><a name="obslist"/></h3>

<div style="line-height:200%;"><xsl:for-each select="//targets/target">
			<xsl:sort select="position()" data-type="number" order="ascending"/>
			
			<xsl:text disable-output-escaping="yes">&lt;a href="#target</xsl:text>
			<xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
			<xsl:value-of select="name"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
			
			<xsl:if test="position() != last()"> - </xsl:if>
			
			</xsl:for-each>
<hr/></div>
					<xsl:for-each select="//targets/target">
						<xsl:sort select="constellation"/>
						<xsl:apply-templates select="."/>
					</xsl:for-each>										

					<!-- Stammdaten ausgeben -->

					<h3>References</h3>



					<!-- Stammdaten der Beobachter ausgeben -->

					<xsl:for-each select="//observers/observer">

						<xsl:sort select="name"/>

						<xsl:sort select="surname"/>

						<xsl:apply-templates select="."/>

					</xsl:for-each>



					<!-- Generierungsdatum ausgeben -->

					<script type="text/javascript">

						<xsl:text disable-output-escaping="yes">

                  &#60;!--

                  document.write("Created on " + document.lastModified.replace(/(\d{2})\/(\d{2})/,"$2/$1"));

                  //--&#62;

               </xsl:text>
               
					</script>
					<xsl:text disable-output-escaping="yes">&lt;br&gt;with &lt;a href="https://github.com/capape/observation-manager" target="_blank"&gt;Observation Manager&lt;/a&gt;</xsl:text>

				</div> 

			</BODY>

		</HTML>

	</xsl:template>		

	

	<!-- Link zurueck zur Liste der Beobachtungen -->

	<xsl:template name="linkTop">

		<xsl:text disable-output-escaping="yes">&lt;a href="#objectList"&gt; &gt;&gt; Object List &lt;&lt;&lt;/a&gt;</xsl:text>

		<hr/>

	</xsl:template>

</xsl:stylesheet>

