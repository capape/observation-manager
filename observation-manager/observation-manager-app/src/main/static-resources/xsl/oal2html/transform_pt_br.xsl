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




	<xsl:template match="session">
		<p>
			<xsl:text disable-output-escaping="yes">&lt;a name="session</xsl:text>
			<xsl:value-of select="@id"/>
			<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
			<b>Sess\ufffdes: <xsl:value-of select="substring-before(begin, 'T')"/> um <xsl:value-of select="substring-after(begin, 'T')"/></b>
			<xsl:value-of select="session"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>



		<!-- Date of Observation -->
		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<tr>
				<td>In\ufffdcio:</td>
				<td>
					<xsl:value-of select="substring-before(begin, 'T')"/> um <xsl:value-of select="substring-after(begin, 'T')"/>
				</td>
			</tr>
			<tr>
				<td>Fim:</td>
				<td>
					<xsl:value-of select="substring-before(end, 'T')"/> um <xsl:value-of select="substring-after(end, 'T')"/>
				</td>
			</tr>
		</table>

		<!-- Coobservers -->
		<xsl:if test="count(coObserver)>0">
			<p>Observadores Adicionais:
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
						<td valign="top">Clima:</td>
						<td valign="top">
							<xsl:value-of select="weather"/>
						</td>
					</tr>
				</xsl:if>


				<!-- Equipment -->
				<xsl:if test="count(equipment)>0">
					<tr>
						<td valign="top">Equipamento:</td>
						<td valign="top">
							<xsl:value-of select="equipment"/>
						</td>
					</tr>
				</xsl:if>


				<!-- Comments -->
				<xsl:if test="count(comments)>0">
					<tr>
						<td valign="top">Coment\ufffdrios:</td>
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
						<xsl:when test="name='SUN'">Sol</xsl:when>
						<xsl:when test="name='MERCURY'">Merc\ufffdrio</xsl:when>
						<xsl:when test="name='VENUS'">V\ufffdnus</xsl:when>
						<xsl:when test="name='EARTH'">Terra</xsl:when>
						<xsl:when test="name='MOON'">Lua</xsl:when>
						<xsl:when test="name='MARS'">Marte</xsl:when>
						<xsl:when test="name='JUPITER'">J\ufffdpiter</xsl:when>
						<xsl:when test="name='SATURN'">Saturno</xsl:when>
						<xsl:when test="name='URANUS'">Urano</xsl:when>
						<xsl:when test="name='NEPTUNE'">Netuno</xsl:when>
						<xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise>
			</xsl:choose>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>


		<xsl:choose>
			<xsl:when test="@type='oal:deepSkyGX'">Gal\ufffdxia</xsl:when>
			<xsl:when test="@type='oal:deepSkyGC'">Aglomerado Globular</xsl:when>
			<xsl:when test="@type='oal:deepSkyGN'">Nebulosa Gal\ufffdctica</xsl:when>
			<xsl:when test="@type='oal:deepSkyOC'">Aglomerado Aberto</xsl:when>
			<xsl:when test="@type='oal:deepSkyPN'">Nebulosa Planet\ufffdria</xsl:when>
			<xsl:when test="@type='oal:deepSkyQS'">Quasar</xsl:when>
			<xsl:when test="@type='oal:deepSkyDS'">Estrela Bin\ufffdria</xsl:when>
			<xsl:when test="@type='oal:deepSkyDN'">Nebulosa Escura</xsl:when>
			<xsl:when test="@type='oal:deepSkyAS'">Asterismo</xsl:when>
			<xsl:when test="@type='oal:deepSkySC'">Nuvem de Estrelas</xsl:when>
			<xsl:when test="@type='oal:deepSkyMS'">Sistema de Estrelas M\ufffdltiplo</xsl:when>
			<xsl:when test="@type='oal:deepSkyCG'">Aglomerado de Gal\ufffdxias</xsl:when>
			<xsl:when test="@type='oal:variableStarTargetType'">Estrela Vari\ufffdvel</xsl:when>
			<xsl:when test="@type='oal:SunTargetType'">Sol</xsl:when>
			<xsl:when test="@type='oal:MoonTargetType'">Lua</xsl:when>
			<xsl:when test="@type='oal:PlanetTargetType'">Planeta</xsl:when>
			<xsl:when test="@type='oal:MinorPlanetTargetType'">Planetas Menores</xsl:when>
			<xsl:when test="@type='oal:CometTargetType'">Cometa</xsl:when>
			<xsl:when test="@type='oal:UndefinedTargetType'">(outro Objeto)</xsl:when>
			<xsl:otherwise>(tipo desconhecido)</xsl:otherwise>
		</xsl:choose>


		<xsl:if test="count(constellation)>0"> em <xsl:value-of select="constellation"/>

		</xsl:if>

		<p/>


		<xsl:if test="count(alias)>0">

			<div style="font-size:10">Apelido: <xsl:for-each select="alias">

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
						<td>Tamanho:</td>
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
					<td>Origem:</td>
					<td>
						<xsl:value-of select="key('observerKey', observer)/surname"/>,
						<xsl:text/>
						<xsl:value-of select="key('observerKey', observer)/name"/>
					</td>
				</tr>
			</xsl:if>


			<xsl:if test="boolean(datasource)">
				<tr>
					<td>Origem:</td>
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
			<b>Observador: </b>
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
			<b>Local: </b>
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
				<td>Fuso hor\ufffdrio:</td>
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
			<b>\ufffdticos: </b>
			<xsl:value-of select="model"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>


		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<xsl:if test="count(type)>0">
				<tr>
					<td>Tipo:</td>
					<td>
						<xsl:value-of select="type"/>
					</td>
				</tr>
			</xsl:if>


			<xsl:if test="count(vendor)>0">
				<tr>
					<td>Fabricante:</td>
					<td>
						<xsl:value-of select="vendor"/>
					</td>
				</tr>
			</xsl:if>


			<tr>
				<td>Abertura:</td>
				<td>
					<xsl:value-of select="aperture"/> mm</td>
			</tr>


			<xsl:if test="count(focalLength)>0">
				<tr>
					<td>Dist\ufffdncia focal:</td>
					<td>
						<xsl:value-of select="focalLength"/> mm</td>
				</tr>
			</xsl:if>


			<xsl:if test="count(magnification)>0">
				<tr>
					<td>Amplia\ufffd\ufffdo:</td>
					<td>
						<xsl:value-of select="magnification"/> &#215;</td>
				</tr>


			</xsl:if>


			<xsl:if test="count(trueField)>0">
				<tr>
					<td>Campo de vis\ufffdo verdadeiro:</td>
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

				<b>Ocular Zoom: </b>

			</xsl:if>

			<xsl:if test="count(maxFocalLength)=0">

				<b>Ocular: </b>

			</xsl:if>
			<xsl:value-of select="model"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>
		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<xsl:if test="count(vendor)>0">
				<tr>
					<td>Fabricante:</td>
					<td>
						<xsl:value-of select="vendor"/>
					</td>
				</tr>
			</xsl:if>
			<tr>
				<td>Dist\ufffdncia focal:</td>
				<td>

					<xsl:value-of select="focalLength"/>

					<xsl:if test="count(maxFocalLength)>0">-<xsl:value-of select="maxFocalLength"/></xsl:if> mm

				</td>
			</tr>
			<xsl:if test="count(apparentFOV)>0">
				<tr>
					<td>Campo de vis\ufffdo aparente:</td>
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

			<b>Lente: </b>

			<xsl:value-of select="model"/>

			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

		</p>

		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

			<xsl:if test="count(vendor)>0">

				<tr>

					<td>Fabricante:</td>

					<td>

						<xsl:value-of select="vendor"/>

					</td>

				</tr>

			</xsl:if>

			<tr>

				<td>Fator de dist\ufffdncia focal:</td>

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

				<b>C\ufffdmera CCD: </b>

			</xsl:if>

			<xsl:if test="count(pixelsX)=0">

				<b>C\ufffdmera: </b>

			</xsl:if>

			<xsl:value-of select="model"/>

			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

		</p>

		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

			<xsl:if test="count(vendor)>0">

				<tr>

					<td>Fabricante:</td>

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
			<b>Filtro: </b>
			<xsl:value-of select="model"/>
			<xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
		</p>


		<table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
			<xsl:if test="count(type)>0">
				<tr>
					<td>Tipo:</td>
					<td>
						<xsl:choose>
							<xsl:when test="type='other'">Outro</xsl:when>
							<xsl:when test="type='broad band'">Broadband</xsl:when>
							<xsl:when test="type='narrow band'">Narrowband</xsl:when>
							<xsl:when test="type='O-III'">OIII</xsl:when>
							<xsl:when test="type='Solar'">Solar</xsl:when>
							<xsl:when test="type='H-beta'">H-Beta</xsl:when>
							<xsl:when test="type='H-alpha'">H-Alpha</xsl:when>
							<xsl:when test="type='color'">Colorido</xsl:when>
							<xsl:when test="type='neutral'">Neutro</xsl:when>
							<xsl:when test="type='corrective'">Corretivo</xsl:when>
							<xsl:otherwise>(tipo desconhecido)</xsl:otherwise>
						</xsl:choose>
					</td>
				</tr>
				<xsl:if test="count(color)>0">
					<tr>
						<td>Farbe:</td>
						<td>
						<xsl:choose>
							<xsl:when test="color='light red'">Vermelho claro</xsl:when>
							<xsl:when test="color='red'">Vemelho</xsl:when>
							<xsl:when test="color='deep red'">Vermelho escuro</xsl:when>
							<xsl:when test="color='orange'">Laranja</xsl:when>
							<xsl:when test="color='light yellow'">Amarelho claro</xsl:when>
							<xsl:when test="color='deep yellow'">Amarelo esculo</xsl:when>
							<xsl:when test="color='yellow'">Amarelo</xsl:when>
							<xsl:when test="color='yellow-green'">Amarelo/Verde</xsl:when>
							<xsl:when test="color='light green'">Verde claro</xsl:when>
							<xsl:when test="color='green'">Verde</xsl:when>
							<xsl:when test="color='medium blue'">Azul m\ufffddio</xsl:when>
							<xsl:when test="color='pale blue'">Azul p\ufffdlido</xsl:when>
							<xsl:when test="color='blue'">Azul</xsl:when>
							<xsl:when test="color='deep blue'">Azul escuro</xsl:when>
							<xsl:when test="color='violet'">Violeta</xsl:when>
							<xsl:otherwise>(cor desconhecida)</xsl:otherwise>
						</xsl:choose>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="count(wratten)>0">
					<tr>
						<td>Valor Wratten:</td>
						<td>
							<xsl:value-of select="wratten"/>
						</td>
					</tr>
				</xsl:if>
				<xsl:if test="count(schott)>0">
					<tr>
						<td>Valor Schott:</td>
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
				<li>Classifica\ufffd\ufffdo de visualiza\ufffd\ufffdo:
            				<xsl:choose>
						<xsl:when test="contains(./@type,'findingsDeepSkyOCType')">
							<!-- open starcluster -->
							<xsl:choose>
								<xsl:when test="rating = '1'">Muito evidente, bom aglomerado;</xsl:when>
								<xsl:when test="rating = '2'">Aglomerado evidente</xsl:when>
								<xsl:when test="rating = '3'">Aglomerado claramente vis\ufffdvel</xsl:when>
								<xsl:when test="rating = '4'">Aglomerado n\ufffdo muito evidente</xsl:when>
								<xsl:when test="rating = '5'">Aglomerado bem modesto, facilmente passaria desapercebido</xsl:when>
								<xsl:when test="rating = '6'">Duvidosamente avistado, concentra\ufffd\ufffdo de estrelas igual das cercanias</xsl:when>
								<xsl:when test="rating = '7'">Quase nenhuma estrela na posi\ufffd\ufffdo informada</xsl:when>
							</xsl:choose>
						</xsl:when>


						<xsl:when test="contains(./@type,'findingsDeepSkyDSType')">
							<!-- Double stars -->
							<xsl:choose>
								<xsl:when test="rating = '1'">Estrela bin\ufffdria pode ser verificada</xsl:when>
								<xsl:when test="rating = '2'">Estrela bin\ufffdria aparece com um "8"</xsl:when>
								<xsl:when test="rating = '3'">Estrela bin\ufffdria n\ufffdo pode ser verificada</xsl:when>
							</xsl:choose>
						</xsl:when>


						<xsl:otherwise>
							<!-- other objecttype -->
							<xsl:choose>
								<xsl:when test="rating = '1'">Simplesmente evidente na ocular</xsl:when>
								<xsl:when test="rating = '2'">Boa vis\ufffdo com avisamento direto</xsl:when>
								<xsl:when test="rating = '3'">Vis\ufffdvel diretamente</xsl:when>
								<xsl:when test="rating = '4'">Vis\ufffdvel apenas de forma indireta</xsl:when>
								<xsl:when test="rating = '5'">Objeto mal pode ser visto com vis\ufffdo indireta</xsl:when>
								<xsl:when test="rating = '6'">Objeto duvidosamente avistado</xsl:when>
								<xsl:when test="rating = '7'">Objeto n\ufffdo avistado</xsl:when>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</li>


				<xsl:if test="./@stellar='true'">
					<li>Aparenta stellar<br/>
					</li>
				</xsl:if>


				<xsl:if test="./@resolved='true'">
					<li>Aparenta resolved<br/>
					</li>
				</xsl:if>


				<xsl:if test="./@mottled='true'">
					<li>Aparenta mottled<br/>
					</li>
				</xsl:if>



				<xsl:if test="count(smallDiameter)>0 and count(largeDiameter)>0">
					<li>Tamanho Aparente: <xsl:call-template name="angle">
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
						<xsl:if test="./visMag/@fainterThan='true'">Mais apagado que </xsl:if>
						Magnitude: <xsl:value-of select="visMag"/>
						<xsl:if test="./visMag/@uncertain='true'"> (Incerto)</xsl:if>
						<br/>
					</li>
				</xsl:if>
				<xsl:if test="string-length(chartID)>0">
					<li>
						Mapa: <xsl:value-of select="chartID"/>
						<xsl:if test="./chartID/@nonAAVSOchart='true'"> (Mapa n\ufffdo-AAVSO)</xsl:if>
						<br/>
					</li>
				</xsl:if>
				<xsl:if test="count(comparisonStar) > 0"><li>Estrelas de compara\ufffd\ufffdo:<br/>
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
				<TITLE>Logs de Observa\ufffd\ufffdo</TITLE>
			</HEAD>
			<BODY>
				<div align="center" style="font-size:24;font-family:Verdana,Arial;color:#0000C0">Logs de Observa\ufffd\ufffdo</div>
				<div style="font-size:12;font-family:Verdana, Arial">
					<h3>Observa\ufffd\ufffdes</h3>
					<a name="obslist"/>
					<xsl:apply-templates select="//observation"/>

					<h3>Refer\ufffdncias</h3>

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
                  document.write("Criado em: " + document.lastModified);
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
							<td>Observador</td>
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
								<td>Local</td>
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
									<xsl:when test="count(end) = 1">In\ufffdcio</xsl:when>
									<xsl:otherwise>Hora</xsl:otherwise>
								</xsl:choose>
							</td>
							<td>
								<xsl:value-of select="substring-before(begin, 'T')"/> um <xsl:value-of select="substring-after(begin, 'T')"/>
							</td>
							<xsl:if test="count(end) = 1">
								<tr>
									<td>Fim</td>
									<td>
										<xsl:value-of select="substring-before(end, 'T')"/> um <xsl:value-of select="substring-after(end, 'T')"/>
									</td>
								</tr>
							</xsl:if>
						</tr>

						<xsl:if test="count(faintestStar) = 1">
							<tr>
								<td>Estrela apagada</td>
								<td>
									<xsl:value-of select="faintestStar"/> mag</td>
							</tr>
						</xsl:if>

						<xsl:if test="count(seeing) = 1">
							<tr>
								<td>Avistamentos</td>
								<td>
									<xsl:value-of select="seeing"/>
									<xsl:choose>
										<xsl:when test="seeing = 1"> (muito bom)</xsl:when>
										<xsl:when test="seeing = 2"> (bom)</xsl:when>
										<xsl:when test="seeing = 3"> (razo\ufffdvel)</xsl:when>
										<xsl:when test="seeing = 4"> (ruim)</xsl:when>
										<xsl:when test="seeing = 5"> (muito ruim)</xsl:when>
									</xsl:choose>
								</td>
							</tr>
						</xsl:if>


						<xsl:if test="count(scope) = 1">
							<tr>
								<td>\ufffdticos</td>
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
								<td>Ocular</td>
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
								<td>Filtro</td>
								<td>
									<xsl:text disable-output-escaping="yes">&lt;a href="#filter</xsl:text>
									<xsl:value-of select="filter"/>
									<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
									<xsl:value-of select="key('filterKey', filter)/model"/>
									<xsl:text disable-output-escaping="yes">&lt;/a&gt; </xsl:text>
									<xsl:choose>
										<xsl:when test="key('filterKey', filter)/type='other'">Outro</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='broad band'">Broadband</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='narrow band'">Narrowband</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='O-III'">OIII</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='solar'">Solar</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='H-beta'">H-Beta</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='H-alpha'">H-Alpha</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='color'">Colorido</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='neutral'">Neutro</xsl:when>
										<xsl:when test="key('filterKey', filter)/type='corrective'">Corretivo</xsl:when>
										<xsl:otherwise>(tipo desconhecido)</xsl:otherwise>
									</xsl:choose>

									<!--<xsl:value-of select="key('filterKey', filter)/type"/>										-->
								</td>
							</tr>
						</xsl:if>


						<xsl:if test="count(lens) = 1">

							<tr>

								<td>Lente</td>

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

								<td>C\ufffdmera</td>

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
								<td>Sess\ufffdo</td>
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
					<h5>Impress\ufffdo visual</h5>
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

	<xsl:text disable-output-escaping="yes">&lt;a href="#obslist"&gt; &gt;&gt; Observa\ufffd\ufffdes &lt;&lt;&lt;/a&gt;</xsl:text>
		<hr/>
	</xsl:template>


</xsl:stylesheet>

