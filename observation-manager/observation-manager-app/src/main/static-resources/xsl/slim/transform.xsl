<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:external="http://ExternalFunction.xalan-c++.xml.apache.org" version="1.0" exclude-result-prefixes="external">
  <!-- Formatting Angle -->
  <xsl:template name="angle">
    <xsl:param name="angle"/>
    <xsl:value-of select="$angle"/>
    <xsl:choose>
      <xsl:when test="$angle[@unit='arcsec']">”</xsl:when>
      <xsl:when test="$angle[@unit='arcmin']">′</xsl:when>
      <xsl:when test="$angle[@unit='deg']">°</xsl:when>
      <xsl:when test="$angle[@unit='rad']"> rad</xsl:when>
    </xsl:choose>
  </xsl:template>
  <!-- Formatting Surface Brightness (DSO or Sky-quality)-->
  <xsl:template name="smag">
    <xsl:param name="smag"/>
    <xsl:value-of select="$smag"/>
    <xsl:choose>
      <xsl:when test="$smag[@unit='mags-per-squarearcsec']"> mag/arcsec²</xsl:when>
      <xsl:when test="$smag[@unit='mags-per-squarearcmin']"> mag/arcmin²</xsl:when>
    </xsl:choose>
  </xsl:template>
  <!-- Formatting text for line return carriage in HTML output-->
  <xsl:template name="MultilineTextOutput">
    <xsl:param name="text"/>
    <xsl:choose>
      <xsl:when test="contains($text, '&#10;')">
        <xsl:variable name="text-before-first-break">
          <xsl:value-of select="substring-before($text, '&#10;')"/>
        </xsl:variable>
        <xsl:variable name="text-after-first-break">
          <xsl:value-of select="substring-after($text, '&#10;')"/>
        </xsl:variable>
        <xsl:if test="not($text-before-first-break = '')">
          <xsl:value-of select="$text-before-first-break"/>
          <br/>
        </xsl:if>
        <xsl:if test="not($text-after-first-break = '')">
          <xsl:call-template name="MultilineTextOutput">
            <xsl:with-param name="text" select="$text-after-first-break"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
        <br/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <!-- converts FROM <date>2001-12-31T12:00:00</date> TO some new format (DEFINED below) -->
  <xsl:template name="FormatDate">
    <xsl:param name="DateTime"/>
    <xsl:variable name="year" select="substring($DateTime,1,4)"/>
    <xsl:variable name="month-temp" select="substring-after($DateTime,'-')"/>
    <xsl:variable name="month" select="substring-before($month-temp,'-')"/>
    <xsl:variable name="day-temp" select="substring-after($month-temp,'-')"/>
    <xsl:variable name="day" select="substring($day-temp,1,2)"/>
    <xsl:variable name="time" select="substring-after($DateTime,'T')"/>
    <!-- EUROPEAN FORMAT -->
    <xsl:value-of select="$day"/>
    <xsl:value-of select="'.'"/>
    <!--18.-->
    <xsl:value-of select="$month"/>
    <xsl:value-of select="'.'"/>
    <!--18.03.-->
    <xsl:value-of select="$year"/>
    <xsl:value-of select="' '"/>
    <!--18.03.1976 -->
    <!-- END: EUROPEAN FORMAT -->
  </xsl:template>
  <xsl:template match="session">
    <p>
      <xsl:text disable-output-escaping="yes">&lt;a name="session</xsl:text>
      <xsl:value-of select="@id"/>
      <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
      <b>• Session date: <xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="substring-before(begin, 'T')"/></xsl:call-template></b>
      <xsl:value-of select="session"/>
      <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
    </p>
    <!-- Date of Observation -->
    <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
      <tr>
        <td>Begin:</td>
        <td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="substring(begin, 'T')"/></xsl:call-template>

					 at <xsl:value-of select="substring(begin,12,8)"/>

				</td>
        <td rowspan="2">
          <span style="font-size:9;">UT<xsl:value-of select="substring(begin,20,6)"/></span>
        </td>
      </tr>
      <tr>
        <td>End:</td>
        <td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="substring(end, 'T')"/></xsl:call-template>
					
					 at <xsl:value-of select="substring(end,12,8)"/>

				</td>
      </tr>
      <tr>
        <td/>
      </tr>
    </table>
    <!-- Coobservers -->
    <xsl:if test="count(coObserver)&gt;0">
      <p>○ <span style="font-size:10;">Additional observers:

			
					<xsl:for-each select="coObserver"><xsl:sort select="key('observerKey', .)/name"/><xsl:text disable-output-escaping="yes">&lt;a href="#observer</xsl:text><xsl:value-of select="."/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="key('observerKey', .)/name"/><xsl:text> </xsl:text><xsl:value-of select="key('observerKey', .)/surname"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text><xsl:if test="position() != last()">, </xsl:if></xsl:for-each>

				</span>

			</p>
    </xsl:if>
    <xsl:if test="count(weather)&gt;0 or count(equipment)&gt;0 or count(comments)&gt;0 or count(site)&gt;0">
      <table border="0" cellspacing="0" cellpadding="2" style="font-size:11;font-family:Verdana, Arial;line-height:1.3">
        <!-- Site -->
        <xsl:if test="count(site)&gt;0">
          <tr>
            <td valign="top">Site:</td>
            <td valign="top">
              <xsl:text disable-output-escaping="yes">&lt;a href="#site</xsl:text>
              <xsl:value-of select="site"/>
              <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
              <xsl:value-of select="key('siteKey', site)/name"/>
              <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
            </td>
          </tr>
        </xsl:if>
        <!-- Weather -->
        <xsl:if test="count(weather)&gt;0">
          <tr>
            <td valign="top">Weather:</td>
            <td valign="top">
              <xsl:call-template name="MultilineTextOutput">
                <xsl:with-param name="text" select="weather"/>
              </xsl:call-template>
            </td>
          </tr>
        </xsl:if>
        <!-- Equipment -->
        <xsl:if test="count(equipment)&gt;0">
          <tr>
            <td valign="top">Equipment:</td>
            <td valign="top">
              <xsl:call-template name="MultilineTextOutput">
                <xsl:with-param name="text" select="equipment"/>
              </xsl:call-template>
            </td>
          </tr>
        </xsl:if>
        <!-- Comments -->
        <xsl:if test="count(comments)&gt;0">
          <tr>
            <td valign="top">Comments:</td>
            <td valign="top">
              <xsl:call-template name="MultilineTextOutput">
                <xsl:with-param name="text" select="comments"/>
              </xsl:call-template>
            </td>
          </tr>
        </xsl:if>
        <xsl:variable name="SessionCount" select="count(//sessions/session)"/>
        <xsl:if test="($SessionCount) = 1">
          <tr>
            <td valign="top" width="130px">Session targets<span style="font-size:9;"> (<xsl:value-of select="count(//targets/target)"/>)</span>:</td>
            <td valign="top">
              <xsl:for-each select="//targets/target">
                <xsl:sort select="position()" data-type="number" order="descending"/>
                <xsl:text disable-output-escaping="yes">&lt;a href="#target</xsl:text>
                <xsl:value-of select="@id"/>
                <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                <xsl:value-of select="name"/>
                <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                <xsl:if test="position() != last()"> - </xsl:if>
              </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
      </table>
    </xsl:if>
    <xsl:call-template name="linkTop"/>
  </xsl:template>
  <xsl:template match="target">
    <p align="center"><xsl:text disable-output-escaping="yes">&lt;a name="target</xsl:text><xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

			Target: <span style="font-size:16;font-weight:bold;font-family:Verdana, Arial;color:blue;text-decoration:underline;"><xsl:choose><xsl:when test="@type='oal:PlanetTargetType' or @type='oal:MoonTargetType' or  @type='oal:SunTargetType'"><xsl:choose><xsl:when test="name='SUN'">Sun</xsl:when><xsl:when test="name='MERCURY'">Mercury</xsl:when><xsl:when test="name='VENUS'">Venus</xsl:when><xsl:when test="name='EARTH'">Earth</xsl:when><xsl:when test="name='MOON'">Moon</xsl:when><xsl:when test="name='MARS'">Mars</xsl:when><xsl:when test="name='JUPITER'">Jupiter</xsl:when><xsl:when test="name='SATURN'">Saturn</xsl:when><xsl:when test="name='URANUS'">Uranus</xsl:when><xsl:when test="name='NEPTUNE'">Neptune</xsl:when><xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise></xsl:choose><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text></span>

<xsl:if test="count(constellation)&gt;0"><span style="font-size:10px;"> (<xsl:value-of select="constellation"/>)</span></xsl:if>

<hr style="width:200px;"/>
		




		<xsl:choose><xsl:when test="@type='oal:deepSkyGX'"><center>Galaxy</center></xsl:when><xsl:when test="@type='oal:deepSkyGC'"><center>Globular cluster</center></xsl:when><xsl:when test="@type='oal:deepSkyGN'"><center>Diffuse nebula<xsl:if test="boolean(nebulaType)"><span style="font-size:9;"> (<xsl:value-of select="nebulaType"/>)</span></xsl:if></center></xsl:when><xsl:when test="@type='oal:deepSkyOC'"><center>Open cluster</center></xsl:when><xsl:when test="@type='oal:deepSkyPN'"><center>Planetary nebula</center></xsl:when><xsl:when test="@type='oal:deepSkyQS'"><center>Quasar</center></xsl:when><xsl:when test="@type='oal:deepSkyDS'"><center>Double Star</center></xsl:when><xsl:when test="@type='oal:deepSkyDN'"><center>Dark Nebula</center></xsl:when><xsl:when test="@type='oal:deepSkyAS'"><center>Asterism</center></xsl:when><xsl:when test="@type='oal:deepSkySC'"><center>Star cloud</center></xsl:when><xsl:when test="@type='oal:deepSkyMS'"><center>Multiple star system</center></xsl:when><xsl:when test="@type='oal:deepSkyCG'"><center>Cluster of galaxies</center></xsl:when><xsl:when test="@type='oal:variableStarTargetType'"><center>Variable star</center></xsl:when><xsl:when test="@type='oal:SunTargetType'"><center>Sun</center></xsl:when><xsl:when test="@type='oal:MoonTargetType'"><center>Moon</center></xsl:when><xsl:when test="@type='oal:PlanetTargetType'"><center>Planet</center></xsl:when><xsl:when test="@type='oal:MinorPlanetTargetType'"><center>Minor planet</center></xsl:when><xsl:when test="@type='oal:CometTargetType'"><center>Comet</center></xsl:when><xsl:when test="@type='oal:observationTargetType'"><center>Generic object</center></xsl:when><xsl:when test="@type='oal:UndefinedTargetType'"><center>(other object)</center></xsl:when><xsl:otherwise><center>(unknown Type)</center></xsl:otherwise></xsl:choose>


</p>
    <p>
      <xsl:if test="count(alias)&gt;0">
        <div align="center" style="font-size:10">Alias: <span style="color:blue;"><xsl:for-each select="alias"><xsl:value-of select="."/><xsl:if test="position() != last()">, </xsl:if></xsl:for-each></span>

			</div>
        <hr style="width:200px;"/>
      </xsl:if>
    </p>
    <table border="0" cellspacing="3" cellpadding="3" style="font-size:10;font-family:Verdana,Arial">
      <tr>
        <td valign="top">
          <table border="1" cellspacing="0" cellpadding="2" width="400" bgcolor="#F4F8FF" style="font-size:12;font-family:Verdana, Arial;">
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
                  <td><xsl:call-template name="angle"><xsl:with-param name="angle" select="smallDiameter"/></xsl:call-template> ×

				   <xsl:call-template name="angle"><xsl:with-param name="angle" select="largeDiameter"/></xsl:call-template>

						</td>
                </tr>
              </xsl:if>
              <xsl:if test="boolean(visMag)">
                <tr>
                  <td>Magnitude (vis):</td>
                  <td><xsl:value-of select="visMag"/> mag</td>
                </tr>
              </xsl:if>
              <!-- Double Stars -->
              <xsl:if test="boolean(magComp)">
                <tr>
                  <td>Companion:</td>
                  <td><xsl:value-of select="magComp"/> mag
							
							
							</td>
                </tr>
              </xsl:if>
              <xsl:if test="boolean(separation)">
                <tr>
                  <td>Separation:</td>
                  <td>
                    <xsl:call-template name="angle">
                      <xsl:with-param name="angle" select="separation"/>
                    </xsl:call-template>
                  </td>
                </tr>
              </xsl:if>
              <!-- Deep Sky -->
              <xsl:if test="boolean(surfBr)">
                <tr>
                  <td>Surface brightness:</td>
                  <td>
                    <xsl:call-template name="smag">
                      <xsl:with-param name="smag" select="surfBr"/>
                    </xsl:call-template>
                  </td>
                </tr>
              </xsl:if>
              <!-- OpenCluster -->
              <xsl:if test="boolean(stars)">
                <tr>
                  <td>Population:</td>
                  <td><xsl:value-of select="stars"/> stars
							
							
							</td>
                </tr>
              </xsl:if>
              <xsl:if test="boolean(class)">
                <tr>
                  <td>Trumpler class:</td>
                  <td>
                    <xsl:value-of select="class"/>
                  </td>
                </tr>
              </xsl:if>
              <!-- Galaxy -->
              <xsl:if test="boolean(hubbleType)">
                <tr>
                  <td>Hubble Type:</td>
                  <td>
                    <xsl:value-of select="hubbleType"/>
                    <xsl:choose>
                      <xsl:when test="(hubbleType = 'Sa') or (hubbleType = 'Sb') or (hubbleType = 'Sc') or (hubbleType = 'Sd')"> (Spiral)</xsl:when>
                      <xsl:when test="contains(hubbleType, 'SB')"> (Barred Spiral)</xsl:when>
                      <xsl:when test="(hubbleType = 'S0') or (hubbleType = 'Sb0') or (hubbleType = 'SB0')"> (Lenticular)</xsl:when>
                      <xsl:when test="contains(hubbleType, 'E')"> (Elliptic)</xsl:when>
                      <xsl:when test="contains(hubbleType, 'Ir')"> (Irregular)</xsl:when>
                      <xsl:otherwise> </xsl:otherwise>
                    </xsl:choose>
                  </td>
                </tr>
              </xsl:if>
              <xsl:if test="boolean(pa)">
                <tr>
                  <td>Position Angle:</td>
                  <td><xsl:value-of select="pa"/>°
							
							
							</td>
                </tr>
              </xsl:if>
              <!-- TODO -->
              <xsl:for-each select="pa/following-sibling::*">
                <tr>
                  <td><xsl:value-of select="local-name()"/>:</td>
                  <td>
                    <xsl:value-of select="."/>
                  </td>
                </tr>
              </xsl:for-each>
            </xsl:if>
            <!-- Variable Star Output -->
            <xsl:if test="contains(@type,'oal:variableStar')">
              <!-- Variable Stars -->
              <xsl:if test="boolean(maxApparentMag)">
                <tr>
                  <td>Maximum:</td>
                  <td><xsl:value-of select="maxApparentMag"/> mag
							
							
							</td>
                </tr>
              </xsl:if>
              <xsl:if test="boolean(apparentMag)">
                <tr>
                  <td>Minimum:</td>
                  <td><xsl:value-of select="apparentMag"/> mag
							
							
							</td>
                </tr>
              </xsl:if>
              <xsl:if test="boolean(period)">
                <tr>
                  <td>Period:</td>
                  <td><xsl:value-of select="period"/> days
							
							
							</td>
                </tr>
              </xsl:if>
            </xsl:if>
            <!-- ################################################################### -->
            <!-- TODO: Other subclasses like planets                                 -->
            <!-- ################################################################### -->
            <xsl:if test="boolean(observer)">
              <tr>
                <td>Origin:</td>
                <td><xsl:value-of select="key('observerKey', observer)/surname"/>, 

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
            <xsl:if test="not(contains(./@type,'oal:observationTargetType') or contains(./@type,'oal:PlanetTargetType') or contains(./@type,'oal:MoonTargetType') or contains(./@type,'oal:SunTargetType') or contains(./@type,'oal:MinorPlanetTargetType') or contains(./@type,'oal:CometTargetType'))">
              <tr>
                <td>DSS fields:</td>
                <td>
                  <span style="font-size:10;">
                    <xsl:text disable-output-escaping="yes">[&lt;a href="http://archive.stsci.edu/cgi-bin/dss_search?v=phase2_gsc1&amp;r=</xsl:text>
                    <xsl:value-of select="position/ra"/>
                    <xsl:text disable-output-escaping="yes">&amp;d=</xsl:text>
                    <xsl:value-of select="position/dec"/>
                    <xsl:text disable-output-escaping="yes">&amp;e=J2000&amp;h=30.0&amp;w=30.0&amp;f=gif&amp;c=none&amp;fov=NONE&amp;v3=" style="text-decoration:none;" title="GIF 1000px • HST mag 16 • ©2001 STScI Digitized Sky Survey" target="DSS" onclick="window.open('','DSS','width=550,height=550,menubars=no,toolbars=no,directories=no,resizable=yes,scrollbars=yes,left=50,top=50')"&gt;</xsl:text>
                    <xsl:value-of select="name"/>
                    <xsl:text disable-output-escaping="yes"> (GSC1)&lt;/a&gt;] • [&lt;a href="http://archive.stsci.edu/cgi-bin/dss_search?v=phase2_gsc2&amp;r=</xsl:text>
                    <xsl:value-of select="position/ra"/>
                    <xsl:text disable-output-escaping="yes">&amp;d=</xsl:text>
                    <xsl:value-of select="position/dec"/>
                    <xsl:text disable-output-escaping="yes">&amp;e=J2000&amp;h=30.0&amp;w=30.0&amp;f=gif&amp;c=none&amp;fov=NONE&amp;v3=" style="text-decoration:none;" title="GIF 1800px • HST mag 21 • ©2008 STScI Digitized Sky Survey" target="DSS" onclick="window.open('','DSS','width=550,height=550,menubars=no,toolbars=no,directories=no,resizable=yes,scrollbars=yes,left=50,top=50')"&gt;</xsl:text>
                    <xsl:value-of select="name"/>
                    <xsl:text disable-output-escaping="yes"> (GSC2)&lt;/a&gt;]</xsl:text>
                  </span>
                </td>
              </tr>
            </xsl:if>
          </table>
        </td>
        <td width="100%" valign="top" bgcolor="#EEEEEE">
          <xsl:choose>
            <xsl:when test="boolean(notes)">
              <b>Additional notes</b>
              <ul style="margin-left:-13px;">
                <li type="circle" style="line-height:1.5">
                  <xsl:call-template name="MultilineTextOutput">
                    <xsl:with-param name="text" select="notes"/>
                  </xsl:call-template>
                </li>
              </ul>
            </xsl:when>
            <xsl:otherwise> </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
    </table>
  </xsl:template>
  <xsl:template name="formatHHMM">
    <xsl:param name="node"/>
    <xsl:param name="hrs">
      <xsl:value-of select="floor($node div 15)"/>
    </xsl:param>
    <xsl:param name="hrs_rest">
      <xsl:value-of select="$node - ($hrs * 15)"/>
    </xsl:param>
    <xsl:param name="minutes">
      <xsl:value-of select="floor($hrs_rest * 60 div 15)"/>
    </xsl:param>
    <xsl:param name="minutes_rest">
      <xsl:value-of select="$hrs_rest - ($minutes div 60 * 15)"/>
    </xsl:param>
    <xsl:param name="sec">
      <xsl:value-of select="round($minutes_rest * 3600 div 15)"/>
    </xsl:param>
    <result><xsl:value-of select="$hrs"/>h <xsl:if test="$minutes &lt; 10">0</xsl:if><xsl:value-of select="$minutes"/>mn <xsl:if test="$sec  &lt; 10">0</xsl:if><xsl:value-of select="$sec"/>s</result>
  </xsl:template>
  <xsl:template name="formatDDMM">
    <xsl:param name="node"/>
    <xsl:if test="$node &lt; 0">
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
    <xsl:param name="abs_degrees">
      <xsl:value-of select="- $node"/>
    </xsl:param>
    <xsl:param name="degs">
      <xsl:value-of select="floor($abs_degrees)"/>
    </xsl:param>
    <xsl:param name="degs_rest">
      <xsl:value-of select="$abs_degrees -  $degs"/>
    </xsl:param>
    <xsl:param name="minutes">
      <xsl:value-of select="floor(60 * ($degs_rest))"/>
    </xsl:param>
    <xsl:param name="minutes_rest">
      <xsl:value-of select="$degs_rest - ($minutes div 60)"/>
    </xsl:param>
    <xsl:param name="sec">
      <xsl:value-of select="round($minutes_rest * 3600)"/>
    </xsl:param>
    <result>-<xsl:value-of select="$degs"/><xsl:text>° </xsl:text><xsl:if test="$minutes &lt; 10">0</xsl:if><xsl:value-of select="$minutes"/><xsl:text>' </xsl:text><xsl:if test="$sec &lt; 10">0</xsl:if><xsl:value-of select="$sec"/><xsl:text>"</xsl:text></result>
  </xsl:template>
  <xsl:template name="formatDDMM_higher">
    <xsl:param name="node"/>
    <xsl:param name="degs">
      <xsl:value-of select="floor($node)"/>
    </xsl:param>
    <xsl:param name="degs_rest">
      <xsl:value-of select="$node -  $degs"/>
    </xsl:param>
    <xsl:param name="minutes">
      <xsl:value-of select="floor(60 * ($degs_rest))"/>
    </xsl:param>
    <xsl:param name="minutes_rest">
      <xsl:value-of select="$degs_rest - ($minutes div 60)"/>
    </xsl:param>
    <xsl:param name="sec">
      <xsl:value-of select="round($minutes_rest * 3600)"/>
    </xsl:param>
    <result>
      <xsl:value-of select="$degs"/>
      <xsl:text>° </xsl:text>
      <xsl:if test="$minutes &lt; 10">0</xsl:if>
      <xsl:value-of select="$minutes"/>
      <xsl:text>' </xsl:text>
      <xsl:if test="$sec &lt; 10">0</xsl:if>
      <xsl:value-of select="$sec"/>
      <xsl:text>"</xsl:text>
    </result>
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
    <xsl:if test="count(contact) &gt; 0">Contacts:<br/>

			<ul><xsl:for-each select="contact"><li><xsl:value-of select="."/></li></xsl:for-each></ul>

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
      <xsl:if test="boolean(elevation)">
        <tr>
          <td>Altitude:</td>
          <td><xsl:value-of select="elevation"/> m</td>
        </tr>
      </xsl:if>
      <tr>
        <td>Timezone:</td>
        <td>UT<xsl:if test="timezone &gt;= 0">+</xsl:if>

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
      <xsl:if test="count(type)&gt;0">
        <tr>
          <td>Type:</td>
          <td>
            <xsl:value-of select="type"/>
          </td>
        </tr>
      </xsl:if>
      <xsl:if test="count(vendor)&gt;0">
        <tr>
          <td>Vendor:</td>
          <td>
            <xsl:value-of select="vendor"/>
          </td>
        </tr>
      </xsl:if>
      <tr>
        <td>Aperture:</td>
        <td><xsl:value-of select="aperture"/> mm</td>
      </tr>
      <xsl:if test="count(focalLength)&gt;0">
        <tr>
          <td>Focal length:</td>
          <td><xsl:value-of select="focalLength"/> mm</td>
        </tr>
      </xsl:if>
      <xsl:if test="count(magnification)&gt;0">
        <tr>
          <td>Magnification:</td>
          <td><xsl:value-of select="magnification"/> ×</td>
        </tr>
      </xsl:if>
      <xsl:if test="count(trueField)&gt;0">
        <tr>
          <td>True field of view:</td>
          <td>
            <xsl:call-template name="angle">
              <xsl:with-param name="angle" select="trueField"/>
            </xsl:call-template>
          </td>
        </tr>
      </xsl:if>
      <xsl:if test="count(lightGrasp)&gt;0">
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
      <xsl:if test="count(maxFocalLength)&gt;0">
        <b>Zoom eyepiece: </b>
      </xsl:if>
      <xsl:if test="count(maxFocalLength)=0">
        <b>Eyepiece: </b>
      </xsl:if>
      <xsl:value-of select="model"/>
      <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
    </p>
    <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
      <xsl:if test="count(vendor)&gt;0">
        <tr>
          <td>Vendor:</td>
          <td>
            <xsl:value-of select="vendor"/>
          </td>
        </tr>
      </xsl:if>
      <tr>
        <td>Focal length:</td>
        <td><xsl:value-of select="focalLength"/><xsl:if test="count(maxFocalLength)&gt;0">-<xsl:value-of select="maxFocalLength"/></xsl:if> mm									



				</td>
      </tr>
      <xsl:if test="count(apparentFOV)&gt;0">
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
      <xsl:if test="count(vendor)&gt;0">
        <tr>
          <td>Vendor:</td>
          <td>
            <xsl:value-of select="vendor"/>
          </td>
        </tr>
      </xsl:if>
      <tr>
        <td>Focal length factor:</td>
        <td><xsl:value-of select="factor"/>x</td>
      </tr>
    </table>
    <xsl:call-template name="linkTop"/>
  </xsl:template>
  <xsl:template match="imager">
    <p>
      <xsl:text disable-output-escaping="yes">&lt;a name="imager</xsl:text>
      <xsl:value-of select="@id"/>
      <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
      <xsl:if test="count(pixelsX)&gt;0">
        <b>CCD Camera: </b>
      </xsl:if>
      <xsl:if test="count(pixelsX)=0">
        <b>camera: </b>
      </xsl:if>
      <xsl:value-of select="model"/>
      <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
    </p>
    <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
      <xsl:if test="count(vendor)&gt;0">
        <tr>
          <td>Vendor:</td>
          <td>
            <xsl:value-of select="vendor"/>
          </td>
        </tr>
      </xsl:if>
      <xsl:if test="count(pixelsX)&gt;0">
        <tr>
          <td>Pixel:</td>
          <td><xsl:value-of select="pixelsX"/>x<xsl:value-of select="pixelsY"/></td>
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
      <xsl:if test="count(type)&gt;0">
        <tr>
          <td>Type:</td>
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
              <xsl:otherwise>(unknown Type)</xsl:otherwise>
            </xsl:choose>
          </td>
        </tr>
        <xsl:if test="count(color)&gt;0">
          <tr>
            <td>Colour:</td>
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
        <xsl:if test="count(wratten)&gt;0">
          <tr>
            <td>Wratten value:</td>
            <td>
              <xsl:value-of select="wratten"/>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="count(schott)&gt;0">
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
    <ul style="line-height:1.8;margin-left:-13px;" type="square">
      <xsl:if test="string-length(description)&gt;0">
        <li style="line-height:1.3">
          <xsl:call-template name="MultilineTextOutput">
            <xsl:with-param name="text" select="description"/>
          </xsl:call-template>
          <br/>
        </li>
      </xsl:if>
      <xsl:if test="contains(./@type,'findingsDeepSkyType') or contains(./@type,'findingsDeepSkyOCType') or contains(./@type,'findingsDeepSkyDSType')">
        <!-- Print scale of german Deep Sky List -->
        <li>Visual rating: 

            				<xsl:choose><xsl:when test="contains(./@type,'findingsDeepSkyOCType')"><!-- open starcluster --><xsl:choose><xsl:when test="rating = '1'">Very conspicuous, nice cluster; Eye-catcher</xsl:when><xsl:when test="rating = '2'">Conspicuous cluster</xsl:when><xsl:when test="rating = '3'">Clearly visible cluster</xsl:when><xsl:when test="rating = '4'">Starcluster not very conspicuous</xsl:when><xsl:when test="rating = '5'">Very unobtrusive cluster; can easily be overlooked while searching</xsl:when><xsl:when test="rating = '6'">Dubiously sighted; star density not higher as in surrounding field</xsl:when><xsl:when test="rating = '7'">Almost no stars at the given position</xsl:when></xsl:choose></xsl:when><xsl:when test="contains(./@type,'findingsDeepSkyDSType')"><!-- Double stars --><xsl:choose><xsl:when test="rating = '1'">Doublestar could be resolved</xsl:when><xsl:when test="rating = '2'">Doublestar appears as "8"</xsl:when><xsl:when test="rating = '3'">Doublestar couldn't be resolved</xsl:when></xsl:choose></xsl:when><xsl:otherwise><!-- other objecttype --><xsl:choose><xsl:when test="rating = '1'">Simple conspicuous object in the eyepiece</xsl:when><xsl:when test="rating = '2'">Good viewable with direct vision</xsl:when><xsl:when test="rating = '3'">Viewable with direct vision</xsl:when><xsl:when test="rating = '4'">Viewable only with averted vision</xsl:when><xsl:when test="rating = '5'">Object can hardly be seen with averted vision</xsl:when><xsl:when test="rating = '6'">Object dubiously sighted</xsl:when><xsl:when test="rating = '7'">Object not sighted</xsl:when></xsl:choose></xsl:otherwise></xsl:choose>

				</li>
        <xsl:if test="./@stellar='true'">
          <li>Appears stellar<br/>

					</li>
        </xsl:if>
        <xsl:if test="./@resolved='true'">
          <li>Appears resolved<br/>

					</li>
        </xsl:if>
        <xsl:if test="./@resolved='false'">
          <li>Not resolved<br/>

					</li>
        </xsl:if>
        <xsl:if test="./@mottled='true'">
          <li>Appears mottled<br/>

					</li>
        </xsl:if>
        <xsl:if test="./@extended='true'">
          <li>Appears laminar<br/>

					</li>
        </xsl:if>
        <xsl:if test="count(smallDiameter)&gt;0 and count(largeDiameter)&gt;0">
          <li>Apparent size: <xsl:call-template name="angle"><xsl:with-param name="angle" select="smallDiameter"/></xsl:call-template>   

                  ×<xsl:call-template name="angle"><xsl:with-param name="angle" select="largeDiameter"/></xsl:call-template>

					</li>
        </xsl:if>
      </xsl:if>
      <xsl:if test="contains(./@type,'findingsVariableStarType')">
        <xsl:if test="string-length(visMag)&gt;0">
          <li><xsl:if test="./visMag/@fainterThan='true'">Fainter than </xsl:if>

						Magnitude: <xsl:value-of select="visMag"/>							

						<xsl:if test="./visMag/@uncertain='true'"> (Uncertain)</xsl:if>

						<br/>

					</li>
        </xsl:if>
        <xsl:if test="string-length(chartID)&gt;0">
          <li>

						Chart: <xsl:value-of select="chartID"/>

						<xsl:if test="./chartID/@nonAAVSOchart='true'"> (non-AAVSO chart)</xsl:if>

						<br/>

					</li>
        </xsl:if>
        <xsl:if test="count(comparisonStar) &gt; 0">
          <li>Comparison stars (mag): 					

					
              <xsl:for-each select="comparisonStar">

								•<xsl:value-of select="."/>

							<xsl:if test="position() != last()"> </xsl:if>

						</xsl:for-each>


					</li>
        </xsl:if>
      </xsl:if>
    </ul>
  </xsl:template>
  <xsl:template match="image">
    <xsl:param name="imgFile" select="."/>
    <xsl:param name="imgTag" select="concat('img src=&quot;', $imgFile, '&quot; style=&quot;width:20%;&quot; title=&quot;', $imgFile, '&quot;')"/>
    <xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text>
    <xsl:value-of select="$imgFile"/>
    <xsl:text disable-output-escaping="yes">" target="_blank"&gt;</xsl:text>
    <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
    <xsl:value-of select="$imgTag"/>
    <xsl:text disable-output-escaping="yes">&gt;&lt;/a&gt;</xsl:text>
    <xsl:text disable-output-escaping="yes"> </xsl:text>
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
        <TITLE>Observation Logs</TITLE>
      </HEAD>
      <BODY>
        <div align="center" style="font-size:24;font-family:Verdana,Arial;color:#0000C0"><hr style="width:50%"/>Observation Logs<hr style="width:50%"/></div>
        <div style="font-size:12;font-family:Verdana, Arial">
          <table border="0" cellspacing="0" cellpadding="0" style="font-size:12;font-family:Verdana, Arial;line-height:1.3">
            <xsl:variable name="SessionCount" select="count(//sessions/session)"/>
            <xsl:if test="($SessionCount) &gt; 1">
              <tr>
                <td valign="top" width="170px"><b>• Observed targets</b><span style="font-size:10;"> (<xsl:value-of select="count(//targets/target)"/>)</span>:</td>
                <td valign="top">
                  <xsl:for-each select="//targets/target">
                    <xsl:sort select="position()" data-type="number" order="ascending"/>
                    <xsl:text disable-output-escaping="yes">&lt;a href="#target</xsl:text>
                    <xsl:value-of select="@id"/>
                    <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                    <xsl:value-of select="name"/>
                    <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                    <xsl:if test="position() != last()"> - </xsl:if>
                  </xsl:for-each>
                </td>
              </tr>
            </xsl:if>
          </table>
          <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;">
            <p>Observer(s)</p>
          </div>
          <xsl:for-each select="//observers/observer">
            <xsl:sort select="name"/>
            <xsl:sort select="surname"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;">
            <p>Session details</p>
          </div>
          <xsl:for-each select="//sessions/session">
            <xsl:sort select="begin"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          <a name="obslist"/>
          <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;">
            <p>Observation list</p>
          </div>
          <xsl:apply-templates select="//observation">
            <xsl:sort select="begin" data-type="number" order="ascending"/>
          </xsl:apply-templates>
          <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;">
            <p>Site(s)</p>
          </div>
          <xsl:for-each select="//sites/site">
            <xsl:sort select="latitude" data-type="number" order="descending"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;">
            <p>Equipment</p>
          </div>
          <div align="center" style="text-decoration:underline;font-size:10px;">Optics</div>
          <xsl:for-each select="//scopes/scope">
            <xsl:sort select="aperture" data-type="number" order="descending"/>
            <xsl:sort select="magnification" data-type="number" order="descending"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          <div align="center" style="text-decoration:underline;font-size:10px;">Eyepieces</div>
          <xsl:for-each select="//eyepieces/eyepiece">
            <xsl:sort select="focalLength" data-type="number" order="descending"/>
            <xsl:sort select="apparentFOV"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          <div align="center" style="text-decoration:underline;font-size:10px;">Lenses</div>
          <xsl:for-each select="//lenses/lens">
            <xsl:sort select="factor"/>
            <xsl:sort select="model"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          <div align="center" style="text-decoration:underline;font-size:10px;">Filters</div>
          <xsl:for-each select="//filters/filter">
            <xsl:sort select="model"/>
            <xsl:sort select="type"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          <div align="center" style="text-decoration:underline;font-size:10px;">Cameras</div>
          <xsl:for-each select="//imagers/imager">
            <xsl:sort select="model"/>
            <xsl:sort select="type"/>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          <div style="font-size:10;">
            <xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
            <script type="text/javascript">
              <xsl:text disable-output-escaping="yes">

                  &lt;!--

                  document.write("Created on " + document.lastModified.replace(/(\d{2})\/(\d{2})/,"$2/$1"));

                  //--&gt;

               </xsl:text>
            </script>
            <xsl:text disable-output-escaping="yes">&lt;br&gt;with &lt;a href="https://github.com/capape/observation-manager" target="_blank"&gt;Observation Manager&lt;/a&gt;</xsl:text>
          </div>
        </div>
      </BODY>
    </HTML>
  </xsl:template>
  <xsl:template match="observation">
    <xsl:apply-templates select="key('targetKey', target)"/>
    <table border="0" cellspacing="3" cellpadding="3" style="font-size:12;font-family:Verdana,Arial">
      <tr>
        <td valign="top">
          <table border="1" cellspacing="0" cellpadding="4" width="400" bgcolor="#FFF2F2" style="font-size:12;font-family:Verdana,Arial">
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
              <td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="substring(begin, 'T')"/></xsl:call-template>

					 at <xsl:value-of select="substring(begin,12,8)"/>

							</td>
              <xsl:if test="count(end) = 1">
                <tr>
                  <td>End</td>
                  <td><xsl:call-template name="FormatDate"><xsl:with-param name="DateTime" select="substring(end, 'T')"/></xsl:call-template>
					
					 at <xsl:value-of select="substring(end,12,8)"/>

									</td>
                </tr>
              </xsl:if>
            </tr>
            <xsl:if test="count(faintestStar) = 1">
              <tr>
                <td>NELM</td>
                <td><xsl:value-of select="faintestStar"/> mag 
							
							</td>
              </tr>
            </xsl:if>
            <xsl:if test="count(sky-quality) = 1">
              <tr>
                <td>SQM</td>
                <td>
                  <xsl:call-template name="smag">
                    <xsl:with-param name="smag" select="sky-quality"/>
                  </xsl:call-template>
                </td>
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
                        <xsl:text disable-output-escaping="yes"> (x</xsl:text>
                        <xsl:value-of select="magnification"/>
                        <xsl:text>)</xsl:text>
                      </xsl:if>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:choose>
                        <xsl:when test="count(magnification) = 1">
                          <xsl:text disable-output-escaping="yes">x</xsl:text>
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
                    <xsl:otherwise>(unknown Type)</xsl:otherwise>
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
                  <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                  <xsl:call-template name="FormatDate">
                    <xsl:with-param name="DateTime" select="substring-before(begin, 'T')"/>
                  </xsl:call-template>
                  <span style="font-size:9;">UT<xsl:value-of select="substring(begin,20,6)"/></span>
                  <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
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
    <div style="text-align:center">
      <xsl:for-each select="image">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </div>
    <hr/>
  </xsl:template>
  <xsl:template name="linkTop">
    <div style="font-size:10;padding-top:7px;">
      <xsl:text disable-output-escaping="yes">&lt;a href="#obslist"&gt; &gt;&gt; Observations &lt;&lt;&lt;/a&gt;</xsl:text>
    </div>
    <hr/>
  </xsl:template>
</xsl:stylesheet>
