<?xml version="1.0" encoding="ISO-8859-1"?>
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




	<xsl:template match="session">
		<p>
			<xsl:text disable-output-escaping="yes">&lt;a name="session</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
			<b>Sessions: <xsl:value-of select="substring-before(begin, 'T')"/> um <xsl:value-of select="substring-after(begin, 'T')"/></b>
			<xsl:value-of select="session"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>

		

		<!-- Date of Observation -->
		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<tr>
				<td>Begin:</td>
				<td>
					<xsl:value-of select="substring-before(begin, 'T')"/> um <xsl:value-of select="substring-after(begin, 'T')"/>
				</td>
			</tr>
			<tr>
				<td>End:</td>
				<td>
					<xsl:value-of select="substring-before(end, 'T')"/> um <xsl:value-of select="substring-after(end, 'T')"/>
				</td>
			</tr>
		</table>
		
		<!-- Coobservers -->
		<xsl:if test="count(coObserver)>0">
			<p>Additional Observers:
			<ul>
					<xsl:for-each select="coObserver">
						<xsl:sort select="key('observerKey', .)/name"/>
						<li>
							<xsl:text disable-output-escaping="yes">&lt;a href="#observer</xsl:text>
							<xsl:value-of select="."/>
							<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
							<xsl:value-of select="key('observerKey', .)/name"/><xsl:value-of select="key('observerKey', .)/surname"/>
							<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
						</li>
					</xsl:for-each>
				</ul>
			</p>
		</xsl:if>


		<xsl:if test="count(weather)>0 or count(equipment)>0 or count(comments)>0">
			<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			
				<!-- Weather -->
				<xsl:if test="count(weather)>0">
					<tr>
						<td valign="top">Weather:</td>
						<td valign="top">
							<xsl:value-of select="weather"/>
						</td>
					</tr>
				</xsl:if>


				<!-- Equipment -->
				<xsl:if test="count(equipment)>0">
					<tr>
						<td valign="top">Equipment:</td>
						<td valign="top">
							<xsl:value-of select="equipment"/>
						</td>
					</tr>
				</xsl:if>


				<!-- Comments -->
				<xsl:if test="count(comments)>0">
					<tr>
						<td valign="top">Comments:</td>
						<td valign="top">
							<xsl:value-of select="comments"/>
						</td>
					</tr>
				</xsl:if>
			</table>
		</xsl:if>
		<xsl:call-template name="linkTop"/>
	</xsl:template>
	


	<xsl:template match="target">
		<p>
			<xsl:text disable-output-escaping="yes">&lt;a name="target</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
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
						<xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise>
					</xsl:choose>				
				</xsl:when>		
				<xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise>	
			</xsl:choose>			
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>


		<xsl:choose>
			<xsl:when test="@type='oal:deepSkyGX'">Galaxy</xsl:when>
			<xsl:when test="@type='oal:deepSkyGC'">Globular Cluster</xsl:when>
			<xsl:when test="@type='oal:deepSkyGN'">Galactic Nebula</xsl:when>
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


		<xsl:if test="count(constellation)>0"> in <xsl:value-of select="constellation"/>

		</xsl:if>

		<p/>


		<xsl:if test="count(alias)>0">

			<div style="font-size:10">Alias: <xsl:for-each select="alias">

					<xsl:value-of select="."/>
					<xsl:if test="position() != last()">, </xsl:if>
				</xsl:for-each>
			</div>
			<br/>
		</xsl:if>


		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<xsl:if test="boolean(position/ra)">
				<tr>
					<td>RA:</td>
					<td>
						<xsl:call-template name="formatHHMM">
							<xsl:with-param name="node" select="position/ra"/>
						</xsl:call-template>
					</td>
				</tr>
			</xsl:if>


			<xsl:if test="boolean(position/dec)">
				<tr>
					<td>Dec:</td>
					<td>
						<xsl:call-template name="formatDDMM">
							<xsl:with-param name="node" select="position/dec"/>
						</xsl:call-template>
					</td>
				</tr>
			</xsl:if>


			<!-- Output from attributes of Subclasses -->
			<xsl:if test="contains(@type,'oal:deepSky')">
				<!-- Deep Sky -->
				<xsl:if test="boolean(smallDiameter) and boolean(largeDiameter)">
					<tr>
						<td>Size:</td>
						<td>
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
						<td>m(vis):</td>
						<td>
							<xsl:value-of select="visMag"/> mag</td>
					</tr>
				</xsl:if>


				<xsl:if test="boolean(surfBr)">
					<tr>
						<td>SB:</td>
						<td>
							<xsl:value-of select="surfBr"/> mags/sq.arcmin</td>
					</tr>
				</xsl:if>


				<!-- TODO -->
				<xsl:for-each select="surfBr/following-sibling::*">
					<tr>
						<td>
							<xsl:value-of select="local-name()"/>:</td>
						<td>
							<xsl:value-of select="."/>
						</td>
					</tr>
				</xsl:for-each>
			</xsl:if>


			<!-- ################################################################### -->
			<!-- TODO: Other subclasses like planets                                 -->
			<!-- ################################################################### -->


			<xsl:if test="boolean(observer)">
				<tr>
					<td>Origin:</td>
					<td>
						<xsl:value-of select="key('observerKey', observer)/surname"/>, 
						<xsl:text/>
						<xsl:value-of select="key('observerKey', observer)/name"/>
					</td>
				</tr>
			</xsl:if>
			

			<xsl:if test="boolean(datasource)">
				<tr>
					<td>Origin:</td>
					<td>
						<xsl:value-of select="datasource"/>
					</td>
				</tr>
			</xsl:if>
		</table>
	</xsl:template>
	


	<xsl:template name="formatHHMM">
		<xsl:param name="node"/>                

		<xsl:param name="hrs"><xsl:value-of select='floor($node div 15)'/></xsl:param>
		<xsl:param name="hrs_rest"><xsl:value-of select='$node - ($hrs * 15)'/></xsl:param>
		<xsl:param name="minutes"><xsl:value-of select='floor($hrs_rest * 60 div 15)'/></xsl:param>
		<xsl:param name="minutes_rest"><xsl:value-of select='$hrs_rest - ($minutes div 60 * 15)'/></xsl:param>
                <xsl:param name="sec"><xsl:value-of select='round($minutes_rest * 3600 div 15)'/></xsl:param>
		<result><xsl:value-of select="$hrs"/>h <xsl:if test="$minutes &lt; 10">0</xsl:if><xsl:value-of select="$minutes"/>m <xsl:if test="$sec  &lt; 10">0</xsl:if><xsl:value-of select="$sec"/>s</result>
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
		<xsl:if test="count(contact) > 0">Contacts:<br/>
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


	<xsl:template match="site">
		<p>
			<xsl:text disable-output-escaping="yes">&lt;a name="site</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
			<b>Site: </b>
			<xsl:value-of select="name"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>


		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<tr>
				<td>Longitude:</td>
				<td>
					<xsl:call-template name="angle">
						<xsl:with-param name="angle" select="longitude"/>
					</xsl:call-template>
				</td>
			</tr>


			<tr>
				<td>Latitude:</td>
				<td>
					<xsl:call-template name="angle">
						<xsl:with-param name="angle" select="latitude"/>
					</xsl:call-template>
				</td>
			</tr>


			<tr>
				<td>Timezone:</td>
				<td>UT<xsl:if test="timezone >= 0">+</xsl:if>
				<xsl:value-of select="timezone"/> min</td>
			</tr>
		</table>
		<xsl:call-template name="linkTop"/>
	</xsl:template>


	<xsl:template match="scope">
		<p>
			<xsl:text disable-output-escaping="yes">&lt;a name="scope</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
			<b>Optics: </b>
			<xsl:value-of select="model"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>


		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<xsl:if test="count(type)>0">
				<tr>
					<td>Type:</td>
					<td>
						<xsl:value-of select="type"/>
					</td>
				</tr>
			</xsl:if>


			<xsl:if test="count(vendor)>0">
				<tr>
					<td>Vendor:</td>
					<td>
						<xsl:value-of select="vendor"/>
					</td>
				</tr>
			</xsl:if>


			<tr>
				<td>Aperture:</td>
				<td>
					<xsl:value-of select="aperture"/> mm</td>
			</tr>


			<xsl:if test="count(focalLength)>0">
				<tr>
					<td>Focal length:</td>
					<td>
						<xsl:value-of select="focalLength"/> mm</td>
				</tr>
			</xsl:if>


			<xsl:if test="count(magnification)>0">
				<tr>
					<td>Magnification:</td>
					<td>
						<xsl:value-of select="magnification"/> &#215;</td>
				</tr>


			</xsl:if>


			<xsl:if test="count(trueField)>0">
				<tr>
					<td>True field of view:</td>
					<td>
						<xsl:call-template name="angle">
							<xsl:with-param name="angle" select="trueField"/>
						</xsl:call-template>
					</td>
				</tr>
			</xsl:if>


			<xsl:if test="count(lightGrasp)>0">
				<tr>
					<td>Light grasp:</td>
					<td>
						<xsl:value-of select="lightGrasp"/>
					</td>
				</tr>
			</xsl:if>
		</table>
		<xsl:call-template name="linkTop"/>
	</xsl:template>


	<xsl:template match="eyepiece">
		<p>
			<xsl:text disable-output-escaping="yes">&lt;a name="eyepiece</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
			<xsl:if test="count(maxFocalLength)>0">

				<b>Zoom eyepiece: </b>

			</xsl:if>

			<xsl:if test="count(maxFocalLength)=0">

				<b>Eyepiece: </b>

			</xsl:if>			
			<xsl:value-of select="model"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>
		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<xsl:if test="count(vendor)>0">
				<tr>
					<td>Vendor:</td>
					<td>
						<xsl:value-of select="vendor"/>
					</td>
				</tr>
			</xsl:if>
			<tr>
				<td>Focal length:</td>
				<td>

					<xsl:value-of select="focalLength"/>

					<xsl:if test="count(maxFocalLength)>0">-<xsl:value-of select="maxFocalLength"/></xsl:if> mm									

				</td>
			</tr>
			<xsl:if test="count(apparentFOV)>0">
				<tr>
					<td>Apparent field of view:</td>
					<td>
						<xsl:call-template name="angle">
							<xsl:with-param name="angle" select="apparentFOV"/>
						</xsl:call-template>
					</td>
				</tr>
			</xsl:if>
		</table>
		<xsl:call-template name="linkTop"/>
	</xsl:template>


	<xsl:template match="lens">

		<p>

			<xsl:text disable-output-escaping="yes">&lt;a name="lens</xsl:text>

			<xsl:value-of select="@id"/>

			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

			<b>Lens: </b>

			<xsl:value-of select="model"/>

			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

		</p>

		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

			<xsl:if test="count(vendor)>0">

				<tr>

					<td>Vendor:</td>

					<td>

						<xsl:value-of select="vendor"/>

					</td>

				</tr>

			</xsl:if>

			<tr>

				<td>Focal length factor:</td>

				<td>

					<xsl:value-of select="factor"/> mm</td>

			</tr>

		</table>

		<xsl:call-template name="linkTop"/>

	</xsl:template>	

	

	

	<xsl:template match="imager">

		<p>

			<xsl:text disable-output-escaping="yes">&lt;a name="imager</xsl:text>

			<xsl:value-of select="@id"/>

			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

			<xsl:if test="count(pixelsX)>0">

				<b>CCD Camera: </b>

			</xsl:if>

			<xsl:if test="count(pixelsX)=0">

				<b>Camera: </b>

			</xsl:if>			

			<xsl:value-of select="model"/>

			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

		</p>

		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

			<xsl:if test="count(vendor)>0">

				<tr>

					<td>Vendor:</td>

					<td>

						<xsl:value-of select="vendor"/>

					</td>

				</tr>

			</xsl:if>

			<xsl:if test="count(pixelsX)>0">

				<tr>

					<td>Pixel:</td>

					<td>

						<xsl:value-of select="pixelsX"/>x<xsl:value-of select="pixelsY"/>

					</td>

				</tr>

			</xsl:if>

		</table>

		<xsl:call-template name="linkTop"/>

	</xsl:template>		



	<xsl:template match="filter">
		<p>
			<xsl:text disable-output-escaping="yes">&lt;a name="filter</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
			<b>Filter: </b>
			<xsl:value-of select="model"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>


		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<xsl:if test="count(type)>0">
				<tr>
					<td>Typ:</td>
					<td>
						<xsl:choose>
							<xsl:when test="type='other'">Other</xsl:when>
							<xsl:when test="type='broad band'">Broadband</xsl:when>
							<xsl:when test="type='narrow band'">Narrowband</xsl:when>
							<xsl:when test="type='O-III'">OIII</xsl:when>
							<xsl:when test="type='Solar'">Solar</xsl:when>
							<xsl:when test="type='H-beta'">H-Beta</xsl:when>
							<xsl:when test="type='H-alpha'">H-Alpha</xsl:when>
							<xsl:when test="type='color'">Color</xsl:when>
							<xsl:when test="type='neutral'">Neutral</xsl:when>
							<xsl:when test="type='corrective'">Corrective</xsl:when>																						
							<xsl:otherwise>(unknown type)</xsl:otherwise>
						</xsl:choose>					
					</td>
				</tr>
				<xsl:if test="count(color)>0">
					<tr>
						<td>Farbe:</td>
						<td>
						<xsl:choose>
							<xsl:when test="color='light red'">Light red</xsl:when>
							<xsl:when test="color='red'">Red</xsl:when>
							<xsl:when test="color='deep red'">Dark red</xsl:when>
							<xsl:when test="color='orange'">Orange</xsl:when>
							<xsl:when test="color='light yellow'">Light yellow</xsl:when>
							<xsl:when test="color='deep yellow'">Dark yellow</xsl:when>
							<xsl:when test="color='yellow'">Yellow</xsl:when>
							<xsl:when test="color='yellow-green'">Yellow/Green</xsl:when>
							<xsl:when test="color='light green'">Light green</xsl:when>
							<xsl:when test="color='green'">Green</xsl:when>								
							<xsl:when test="color='medium blue'">Medium blue</xsl:when>
							<xsl:when test="color='pale blue'">Pale blue</xsl:when>
							<xsl:when test="color='blue'">Blue</xsl:when>
							<xsl:when test="color='deep blue'">Dark blue</xsl:when>									
							<xsl:when test="color='violet'">Violet</xsl:when>																					
							<xsl:otherwise>(unknown color)</xsl:otherwise>
						</xsl:choose>										
						</td>
					</tr>
				</xsl:if>					
				<xsl:if test="count(wratten)>0">
					<tr>
						<td>Wratten value:</td>
						<td>
							<xsl:value-of select="wratten"/>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="count(schott)>0">
					<tr>
						<td>Schott value:</td>
						<td>
							<xsl:value-of select="schott"/>
						</td>
					</tr>
				</xsl:if>				
			</xsl:if>
		</table>
		<xsl:call-template name="linkTop"/>
	</xsl:template>


	<xsl:template match="result">
		<ul>
			<xsl:if test="string-length(description)>0">
				<li>
					<xsl:value-of select="description"/>
					<br/>
				</li>
			</xsl:if>
            
			<xsl:if test="contains(./@type,'findingsDeepSkyType') or contains(./@type,'findingsDeepSkyOCType') or contains(./@type,'findingsDeepSkyDSType')">
				<!-- Print scale of german Deep Sky List -->
				<li>Visual rating: 
            				<xsl:choose>
						<xsl:when test="contains(./@type,'findingsDeepSkyOCType')">
							<!-- open starcluster -->
							<xsl:choose>
								<xsl:when test="rating = '1'">Very conspicuous, nice cluster; Eye-catcher</xsl:when>
								<xsl:when test="rating = '2'">Conspicuous cluster</xsl:when>
								<xsl:when test="rating = '3'">Clearly visible cluster</xsl:when>
								<xsl:when test="rating = '4'">Starcluster not very conspicuous</xsl:when>
								<xsl:when test="rating = '5'">Very unobtrusive cluster; can easily be overlooked while searching</xsl:when>
								<xsl:when test="rating = '6'">Dubiously sighted; star density not higher as in surrounding field</xsl:when>
								<xsl:when test="rating = '7'">Almost no stars at the given position</xsl:when>
							</xsl:choose>
						</xsl:when>


						<xsl:when test="contains(./@type,'findingsDeepSkyDSType')">
							<!-- Double stars -->
							<xsl:choose>
								<xsl:when test="rating = '1'">Doublestar could be resolved</xsl:when>
								<xsl:when test="rating = '2'">Doublestar appears as "8"</xsl:when>
								<xsl:when test="rating = '3'">Doublestar couldn't be resolved</xsl:when>
							</xsl:choose>
						</xsl:when>


						<xsl:otherwise>
							<!-- other objecttype -->
							<xsl:choose>
								<xsl:when test="rating = '1'">Simple conspicuous object in the eyepiece</xsl:when>
								<xsl:when test="rating = '2'">Good viewable with direct vision</xsl:when>
								<xsl:when test="rating = '3'">Viewable with direct vision</xsl:when>
								<xsl:when test="rating = '4'">Viewable only with averted vision</xsl:when>
								<xsl:when test="rating = '5'">Object can hardly be seen with averted vision</xsl:when>
								<xsl:when test="rating = '6'">Object dubiously sighted</xsl:when>
								<xsl:when test="rating = '7'">Object not sighted</xsl:when>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</li>


				<xsl:if test="./@stellar='true'">
					<li>Appears stellar<br/>
					</li>
				</xsl:if>


				<xsl:if test="./@resolved='true'">
					<li>Appears resolved<br/>
					</li>
				</xsl:if>


				<xsl:if test="./@mottled='true'">
					<li>Appears mottled<br/>
					</li>
				</xsl:if>

				

				<xsl:if test="count(smallDiameter)>0 and count(largeDiameter)>0">
					<li>Apparent size: <xsl:call-template name="angle">
							<xsl:with-param name="angle" select="smallDiameter"/>
						</xsl:call-template>   
                  &#215;<xsl:call-template name="angle">
							<xsl:with-param name="angle" select="largeDiameter"/>
						</xsl:call-template>
					</li>
				</xsl:if>
			</xsl:if>
			
			<xsl:if test="contains(./@type,'findingsVariableStarType')">
				<xsl:if test="string-length(visMag)>0">
				    <br/>
					<li>					    
						<xsl:if test="./visMag/@fainterThan='true'">Fainter than </xsl:if>					
						Magnitude: <xsl:value-of select="visMag"/>							
						<xsl:if test="./visMag/@uncertain='true'"> (Uncertain)</xsl:if>
						<br/>
					</li>
				</xsl:if>				
				<xsl:if test="string-length(chartID)>0">
					<li>
						Chart: <xsl:value-of select="chartID"/>
						<xsl:if test="./chartID/@nonAAVSOchart='true'"> (non-AAVSO Chart)</xsl:if>
						<br/>
					</li>
				</xsl:if>				
				<xsl:if test="count(comparisonStar) > 0"><li>Comparison stars:<br/>					
					<ul>
						<xsl:for-each select="comparisonStar">
							<li>
								<xsl:value-of select="."/>
							</li>
						</xsl:for-each>
					</ul>
					</li>
				</xsl:if>									
			</xsl:if>			
		</ul>
	</xsl:template>



	<xsl:template match="image">
        <xsl:param name="imgFile" select="."/>
        <xsl:param name="imgTag" select="concat('img src=&quot;', $imgFile, '&quot;')"/>
        <p><xsl:text disable-output-escaping="yes">&lt;</xsl:text><xsl:value-of select="$imgTag"/><xsl:text disable-output-escaping="yes">&gt;</xsl:text>
	    <br/><xsl:value-of select="$imgFile"/>
	    </p>
	    

    </xsl:template>




    <xsl:output method="html"/>
	<!-- resolve references  -->
	<xsl:key name="sessionKey" match="sessions/session" use="@id"/>
	<xsl:key name="targetKey" match="targets/target" use="@id"/>
	<xsl:key name="observerKey" match="observers/observer" use="@id"/>
	<xsl:key name="siteKey" match="sites/site" use="@id"/>
	<xsl:key name="scopeKey" match="scopes/scope" use="@id"/>
	<xsl:key name="eyepieceKey" match="eyepieces/eyepiece" use="@id"/>
	<xsl:key name="lensKey" match="lenses/lens" use="@id"/>

	<xsl:key name="imagerKey" match="imagers/imager" use="@id"/>

	<xsl:key name="filterKey" match="filters/filter" use="@id"/>


	<xsl:template match="/">
		<HTML>
			<HEAD>
				<TITLE>Observation logs</TITLE>
			</HEAD>
			<BODY>
				<div align="center" style="font-size:24;font-family:Verdana,Arial;color:#0000C0">Observation logs</div>
				<div style="font-size:12;font-family:Verdana, Arial">
					<h3>Observations</h3>
					<a name="obslist"/>
					<xsl:apply-templates select="//observation"/>

					<h3>References</h3>

					<xsl:for-each select="//sessions/session">
						<xsl:sort select="begin"/>
						<xsl:apply-templates select="."/>
					</xsl:for-each>

					<xsl:for-each select="//observers/observer">
						<xsl:sort select="name"/>
						<xsl:sort select="surname"/>
						<xsl:apply-templates select="."/>
					</xsl:for-each>


					<xsl:for-each select="//sites/site">
						<xsl:sort select="name"/>
						<xsl:apply-templates select="."/>
					</xsl:for-each>


					<xsl:for-each select="//scopes/scope">
						<xsl:sort select="model"/>
						<xsl:apply-templates select="."/>
					</xsl:for-each>


					<xsl:for-each select="//eyepieces/eyepiece">
						<xsl:sort select="focalLength"/>
						<xsl:sort select="model"/>
						<xsl:apply-templates select="."/>
					</xsl:for-each>
			

					<xsl:for-each select="//lenses/lens">

						<xsl:sort select="factor"/>

						<xsl:sort select="model"/>

						<xsl:apply-templates select="."/>

					</xsl:for-each>



					<xsl:for-each select="//filters/filter">

						<xsl:sort select="model"/>

						<xsl:sort select="type"/>

						<xsl:apply-templates select="."/>

					</xsl:for-each>



					<xsl:for-each select="//imagers/imager">

						<xsl:sort select="model"/>

						<xsl:sort select="type"/>

						<xsl:apply-templates select="."/>

					</xsl:for-each>										

					<script type="text/javascript">
						<xsl:text disable-output-escaping="yes">
                  &#60;!--
                  document.write("Created: " + document.lastModified);
                  //--&#62;
               </xsl:text>
					</script>
				</div>
			</BODY>
		</HTML>
	</xsl:template>




	<xsl:template match="observation">
		<xsl:apply-templates select="key('targetKey', target)"/>
		
		<table border="0" cellspacing="3" cellpadding="3" style="font-size:14;font-family:Verdana,Arial">
			<tr>
				<td valign="top">
					<table border="1" cellspacing="0" cellpadding="2" width="400" style="font-size:14;font-family:Verdana,Arial">
						<tr>
							<td>Observer</td>
							<td>
								<xsl:text disable-output-escaping="yes">&lt;a href="#observer</xsl:text>
								<xsl:value-of select="observer"/>
								<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
								<xsl:value-of select="key('observerKey', observer)/name"/>
								<xsl:text/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="key('observerKey', observer)/surname"/>
								<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
							</td>
						</tr>

						<xsl:if test="count(site) = 1">
							<tr>
								<td>Site</td>
								<td>
									<xsl:text disable-output-escaping="yes">&lt;a href="#site</xsl:text>
									<xsl:value-of select="site"/>
									<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
									<xsl:value-of select="key('siteKey', site)/name"/>
									<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
								</td>
							</tr>
						</xsl:if>

						<tr>
							<td>
								<xsl:choose>
									<xsl:when test="count(end) = 1">Begin</xsl:when>
									<xsl:otherwise>Time</xsl:otherwise>
								</xsl:choose>
							</td>
							<td>
								<xsl:value-of select="substring-before(begin, 'T')"/> um <xsl:value-of select="substring-after(begin, 'T')"/>
							</td>
							<xsl:if test="count(end) = 1">
								<tr>
									<td>End</td>
									<td>
										<xsl:value-of select="substring-before(end, 'T')"/> um <xsl:value-of select="substring-after(end, 'T')"/>
									</td>
								</tr>
							</xsl:if>
						</tr>

						<xsl:if test="count(faintestStar) = 1">
							<tr>
								<td>Faintest star</td>
								<td>
									<xsl:value-of select="faintestStar"/> mag</td>
							</tr>
						</xsl:if>

						<xsl:if test="count(seeing) = 1">
							<tr>
								<td>Seeing</td>
								<td>
									<xsl:value-of select="seeing"/>
									<xsl:choose>
										<xsl:when test="seeing = 1"> (very good)</xsl:when>
										<xsl:when test="seeing = 2"> (good)</xsl:when>
										<xsl:when test="seeing = 3"> (fair)</xsl:when>
										<xsl:when test="seeing = 4"> (bad)</xsl:when>
										<xsl:when test="seeing = 5"> (very bad)</xsl:when>
									</xsl:choose>
								</td>
							</tr>
						</xsl:if>


						<xsl:if test="count(scope) = 1">
							<tr>
								<td>Optics</td>
								<td>
									<xsl:text disable-output-escaping="yes">&lt;a href="#scope</xsl:text>
									<xsl:value-of select="scope"/>
									<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
									<xsl:value-of select="key('scopeKey', scope)/model"/>
									<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
								</td>
							</tr>
						</xsl:if>

						<xsl:if test="count(eyepiece) = 1 or count(magnification) = 1">
							<tr>
								<td>Eyepiece</td>
								<td>
									<xsl:choose>
										<xsl:when test="count(eyepiece) = 1">
											<xsl:text disable-output-escaping="yes">&lt;a href="#eyepiece</xsl:text>
											<xsl:value-of select="eyepiece"/>
											<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
											<xsl:value-of select="key('eyepieceKey', eyepiece)/model"/>
											<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
											<xsl:if test="count(magnification) = 1">
												<xsl:text disable-output-escaping="yes"> (V=</xsl:text>
												<xsl:value-of select="magnification"/>
												<xsl:text>)</xsl:text>
											</xsl:if>
										</xsl:when>
										<xsl:otherwise>
											<xsl:choose>
												<xsl:when test="count(magnification) = 1">
													<xsl:text disable-output-escaping="yes">V=</xsl:text>
													<xsl:value-of select="magnification"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>

								</td>
							</tr>
						</xsl:if>				

						<xsl:if test="count(filter) = 1">
							<tr>
								<td>Filter</td>
								<td>
									<xsl:text disable-output-escaping="yes">&lt;a href="#filter</xsl:text>
									<xsl:value-of select="filter"/>
									<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
									<xsl:value-of select="key('filterKey', filter)/model"/>
									<xsl:text disable-output-escaping="yes">&lt;/a&gt; </xsl:text>
									<xsl:choose>
										<xsl:when test="key('filterKey', filter)/type='other'">Other</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='broad band'">Broadband</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='narrow band'">Narrowband</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='O-III'">OIII</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='solar'">Solar</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='H-beta'">H-Beta</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='H-alpha'">H-Alpha</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='color'">Color</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='neutral'">Neutral</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='corrective'">Corrective</xsl:when>																						
										<xsl:otherwise>(unknown type)</xsl:otherwise>
									</xsl:choose>	
									
									<!--<xsl:value-of select="key('filterKey', filter)/type"/>										-->
								</td>
							</tr>
						</xsl:if>


						<xsl:if test="count(lens) = 1">

							<tr>

								<td>Lens</td>

								<td>

									<xsl:text disable-output-escaping="yes">&lt;a href="#lens</xsl:text>

									<xsl:value-of select="lens"/>

									<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

									<xsl:value-of select="key('lensKey', lens)/model"/>

									<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

								</td>

							</tr>

						</xsl:if>



						<xsl:if test="count(imager) = 1">

							<tr>

								<td>Camera</td>

								<td>

									<xsl:text disable-output-escaping="yes">&lt;a href="#imager</xsl:text>

									<xsl:value-of select="imager"/>

									<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

									<xsl:value-of select="key('imagerKey', imager)/model"/>

									<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

								</td>

							</tr>

						</xsl:if>



						<xsl:if test="count(session) = 1">
							<tr>
								<td>Session</td>
								<td>
									<xsl:text disable-output-escaping="yes">&lt;a href="#session</xsl:text>
									<xsl:value-of select="session"/>
									<xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="substring-before(begin, 'T')"/> um <xsl:value-of select="substring-after(begin, 'T')"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
								</td>
							</tr>
						</xsl:if>
					</table>
				</td>


				<td width="100%" valign="top" bgcolor="#EEEEEE">
					<h5>Visual impression</h5>
					<p>
						<xsl:for-each select="result">
							<xsl:apply-templates select="."/>
							<xsl:if test="position()!=last()">
								<hr/>
							</xsl:if>
						</xsl:for-each>
					</p>
				</td>
			</tr>
		</table>


		<xsl:for-each select="image">
			<xsl:apply-templates select="."/>
		</xsl:for-each>


		<hr/>
	</xsl:template>
	<xsl:template name="linkTop">
	
	<xsl:text disable-output-escaping="yes">&lt;a href="#obslist"&gt; &gt;&gt; Observations &lt;&lt;&lt;/a&gt;</xsl:text>
		<hr/>
	</xsl:template>
	

</xsl:stylesheet>


