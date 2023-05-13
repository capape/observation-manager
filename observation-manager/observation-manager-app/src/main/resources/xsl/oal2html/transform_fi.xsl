<?xml version="1.0" encoding="utf-8"?>
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
            <xsl:text disable-output-escaping="yes">&lt;a name="istunto</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Istunnot: <xsl:value-of select="substring-before(begin, 'T')"/> klo <xsl:value-of select="substring-after(begin, 'T')"/></b>
            <xsl:value-of select="session"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </p>



        <!-- Date of Observation -->
        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
            <tr>
                <td>Alku:</td>
                <td>
                    <xsl:value-of select="substring-before(begin, 'T')"/> klo <xsl:value-of select="substring-after(begin, 'T')"/>
                </td>
            </tr>
            <tr>
                <td>Loppu:</td>
                <td>
                    <xsl:value-of select="substring-before(end, 'T')"/> klo <xsl:value-of select="substring-after(end, 'T')"/>
                </td>
            </tr>
        </table>

        <!-- Coobservers -->
        <xsl:if test="count(coObserver)>0">
            <p>Muut havaitsijat:
                <ul>
                    <xsl:for-each select="coObserver">
                        <xsl:sort select="key('observerKey', .)/name"/>
                        <li>
                            <xsl:text disable-output-escaping="yes">&lt;a href="#havaitsija</xsl:text>
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
                        <td valign="top">S&#228;&#228;:</td>
                        <td valign="top">
                            <xsl:value-of select="weather"/>
                        </td>
                    </tr>
                </xsl:if>


                <!-- Equipment -->
                <xsl:if test="count(equipment)>0">
                    <tr>
                        <td valign="top">Laitteet:</td>
                        <td valign="top">
                            <xsl:value-of select="equipment"/>
                        </td>
                    </tr>
                </xsl:if>


                <!-- Comments -->
                <xsl:if test="count(comments)>0">
                    <tr>
                        <td valign="top">Huomioita:</td>
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
            <xsl:text disable-output-escaping="yes">&lt;a name="kohde</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Kohde: </b>
            <xsl:choose>
                <xsl:when test="@type='oal:PlanetTargetType' or @type='oal:MoonTargetType' or  @type='oal:SunTargetType'">
                    <xsl:choose>
                        <xsl:when test="name='SUN'">Aurinko</xsl:when>
                        <xsl:when test="name='MERCURY'">Merkurius</xsl:when>
                        <xsl:when test="name='VENUS'">Venus</xsl:when>
                        <xsl:when test="name='EARTH'">Maa</xsl:when>
                        <xsl:when test="name='MOON'">Kuu</xsl:when>
                        <xsl:when test="name='MARS'">Mars</xsl:when>
                        <xsl:when test="name='JUPITER'">Jupiter</xsl:when>
                        <xsl:when test="name='SATURN'">Saturnus</xsl:when>
                        <xsl:when test="name='URANUS'">Uranus</xsl:when>
                        <xsl:when test="name='NEPTUNE'">Neptunus</xsl:when>
                        <xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise>
            </xsl:choose>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </p>


        <xsl:choose>
            <xsl:when test="@type='oal:deepSkyGX'">Galaksi</xsl:when>
            <xsl:when test="@type='oal:deepSkyGC'">Pallomainen t&#228;htijoukko</xsl:when>
            <xsl:when test="@type='oal:deepSkyGN'">Emissiosumu</xsl:when>
            <xsl:when test="@type='oal:deepSkyOC'">Avoin t&#228;htijoukko</xsl:when>
            <xsl:when test="@type='oal:deepSkyPN'">Planetaarinen sumu</xsl:when>
            <xsl:when test="@type='oal:deepSkyQS'">Kvasaari</xsl:when>
            <xsl:when test="@type='oal:deepSkyDS'">Kaksoist&#228;hti</xsl:when>
            <xsl:when test="@type='oal:deepSkyDN'">Pime&#228; sumu</xsl:when>
            <xsl:when test="@type='oal:deepSkyAS'">Asterismi</xsl:when>
            <xsl:when test="@type='oal:deepSkySC'">T&#228;htipilvi</xsl:when>
            <xsl:when test="@type='oal:deepSkyMS'">Usean t&#228;hden systeemi</xsl:when>
            <xsl:when test="@type='oal:deepSkyCG'">Galaksijoukko</xsl:when>
            <xsl:when test="@type='oal:variableStarTargetType'">Muuttuva t&#228;hti</xsl:when>
            <xsl:when test="@type='oal:SunTargetType'">Aurinko</xsl:when>
            <xsl:when test="@type='oal:MoonTargetType'">Kuu</xsl:when>
            <xsl:when test="@type='oal:PlanetTargetType'">Planeetta</xsl:when>
            <xsl:when test="@type='oal:MinorPlanetTargetType'">Pikkuplaneetta</xsl:when>
            <xsl:when test="@type='oal:CometTargetType'">Komeetta</xsl:when>
            <xsl:when test="@type='oal:UndefinedTargetType'">(muu kohde)</xsl:when>
            <xsl:otherwise>(tuntematon tyyppi)</xsl:otherwise>
        </xsl:choose>


        <xsl:if test="count(constellation)>0"> t&#228;hdist&#246;ss&#228; <xsl:value-of select="constellation"/>

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
                        <td>Koko:</td>
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
                            <xsl:value-of select="surfBr"/> mag/neli&#246;minuutti</td>
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
                    <td>Havaitsija:</td>
                    <td>
                        <xsl:value-of select="key('observerKey', observer)/surname"/>,
                        <xsl:text/>
                        <xsl:value-of select="key('observerKey', observer)/name"/>
                    </td>
                </tr>
            </xsl:if>


            <xsl:if test="boolean(datasource)">
                <tr>
                    <td>L&#228;hde:</td>
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
            <xsl:text disable-output-escaping="yes">&lt;a name="havaitsija</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Havaitsija: </b>
            <xsl:value-of select="name"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="surname"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </p>
        <xsl:if test="count(contact) > 0">Yhteystiedot:<br/>
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
            <xsl:text disable-output-escaping="yes">&lt;a name="paikka</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Paikka: </b>
            <xsl:value-of select="name"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </p>


        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
            <tr>
                <td>Pituusaste:</td>
                <td>
                    <xsl:call-template name="angle">
                        <xsl:with-param name="angle" select="longitude"/>
                    </xsl:call-template>
                </td>
            </tr>


            <tr>
                <td>Leveysaste:</td>
                <td>
                    <xsl:call-template name="angle">
                        <xsl:with-param name="angle" select="latitude"/>
                    </xsl:call-template>
                </td>
            </tr>


            <tr>
                <td>Aikavy&#246;hyke:</td>
                <td>UT<xsl:if test="timezone >= 0">+</xsl:if>
                    <xsl:value-of select="timezone"/> min</td>
            </tr>
        </table>
        <xsl:call-template name="linkTop"/>
    </xsl:template>


    <xsl:template match="scope">
        <p>
            <xsl:text disable-output-escaping="yes">&lt;a name="havaintov&#228;line</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Havaintov&#228;line: </b>
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </p>


        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
            <xsl:if test="count(type)>0">
                <tr>
                    <td>Tyyppi:</td>
                    <td>
                        <xsl:value-of select="type"/>
                    </td>
                </tr>
            </xsl:if>


            <xsl:if test="count(vendor)>0">
                <tr>
                    <td>Toimittaja:</td>
                    <td>
                        <xsl:value-of select="vendor"/>
                    </td>
                </tr>
            </xsl:if>


            <tr>
                <td>Aukko:</td>
                <td>
                    <xsl:value-of select="aperture"/> mm</td>
            </tr>


            <xsl:if test="count(focalLength)>0">
                <tr>
                    <td>Polttov&#228;li:</td>
                    <td>
                        <xsl:value-of select="focalLength"/> mm</td>
                </tr>
            </xsl:if>


            <xsl:if test="count(magnification)>0">
                <tr>
                    <td>Suurennus:</td>
                    <td>
                        <xsl:value-of select="magnification"/> &#215;</td>
                </tr>


            </xsl:if>


            <xsl:if test="count(trueField)>0">
                <tr>
                    <td>Todellinen n&#228;k&#246;kentt&#228;:</td>
                    <td>
                        <xsl:call-template name="angle">
                            <xsl:with-param name="angle" select="trueField"/>
                        </xsl:call-template>
                    </td>
                </tr>
            </xsl:if>


            <xsl:if test="count(lightGrasp)>0">
                <tr>
                    <td>Valoisuus:</td>
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
            <xsl:text disable-output-escaping="yes">&lt;a name="okulaari</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <xsl:if test="count(maxFocalLength)>0">

                <b>Zoom-okulaari: </b>

            </xsl:if>

            <xsl:if test="count(maxFocalLength)=0">

                <b>Okulaari: </b>

            </xsl:if>
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </p>
        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
            <xsl:if test="count(vendor)>0">
                <tr>
                    <td>Toimittaja:</td>
                    <td>
                        <xsl:value-of select="vendor"/>
                    </td>
                </tr>
            </xsl:if>
            <tr>
                <td>Polttov&#228;li:</td>
                <td>

                    <xsl:value-of select="focalLength"/>

                    <xsl:if test="count(maxFocalLength)>0">-<xsl:value-of select="maxFocalLength"/></xsl:if> mm

                </td>
            </tr>
            <xsl:if test="count(apparentFOV)>0">
                <tr>
                    <td>N&#228;enn&#228;inen n&#228;k&#246;kentt&#228;:</td>
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

            <xsl:text disable-output-escaping="yes">&lt;a name="linssi</xsl:text>

            <xsl:value-of select="@id"/>

            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

            <b>Lens: </b>

            <xsl:value-of select="model"/>

            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

        </p>

        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

            <xsl:if test="count(vendor)>0">

                <tr>

                    <td>Toimittaja:</td>

                    <td>

                        <xsl:value-of select="vendor"/>

                    </td>

                </tr>

            </xsl:if>

            <tr>

                <td>Polttov&#228;lin muunnoskerroin:</td>

                <td>

                    <xsl:value-of select="factor"/> mm</td>

            </tr>

        </table>

        <xsl:call-template name="linkTop"/>

    </xsl:template>





    <xsl:template match="imager">

        <p>

            <xsl:text disable-output-escaping="yes">&lt;a name="kamera</xsl:text>

            <xsl:value-of select="@id"/>

            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

            <xsl:if test="count(pixelsX)>0">

                <b>CCD Kamera: </b>

            </xsl:if>

            <xsl:if test="count(pixelsX)=0">

                <b>Kamera: </b>

            </xsl:if>

            <xsl:value-of select="model"/>

            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

        </p>

        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

            <xsl:if test="count(vendor)>0">

                <tr>

                    <td>Toimittaja:</td>

                    <td>

                        <xsl:value-of select="vendor"/>

                    </td>

                </tr>

            </xsl:if>

            <xsl:if test="count(pixelsX)>0">

                <tr>

                    <td>Pikseli&#228;:</td>

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
            <xsl:text disable-output-escaping="yes">&lt;a name="suodin</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Filter: </b>
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </p>


        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">
            <xsl:if test="count(type)>0">
                <tr>
                    <td>Tyyppi:</td>
                    <td>
                        <xsl:choose>
                            <xsl:when test="type='other'">Muu</xsl:when>
                            <xsl:when test="type='broad band'">Leve&#228;kaista</xsl:when>
                            <xsl:when test="type='narrow band'">Kapeakaista</xsl:when>
                            <xsl:when test="type='O-III'">OIII</xsl:when>
                            <xsl:when test="type='Solar'">Aurinko</xsl:when>
                            <xsl:when test="type='H-beta'">H-Beta</xsl:when>
                            <xsl:when test="type='H-alpha'">H-Alpha</xsl:when>
                            <xsl:when test="type='color'">V&#228;ri</xsl:when>
                            <xsl:when test="type='neutral'">Harmaa</xsl:when>
                            <xsl:when test="type='corrective'">Korjaava</xsl:when>
                            <xsl:otherwise>(tuntematon tyyppi)</xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
                <xsl:if test="count(color)>0">
                    <tr>
                        <td>V&#228;ri:</td>
                        <td>
                            <xsl:choose>
                                <xsl:when test="color='light red'">Vaalea punainen</xsl:when>
                                <xsl:when test="color='red'">Punainen</xsl:when>
                                <xsl:when test="color='deep red'">Tumma punainen</xsl:when>
                                <xsl:when test="color='orange'">Oranssi</xsl:when>
                                <xsl:when test="color='light yellow'">Vaalea keltainen</xsl:when>
                                <xsl:when test="color='deep yellow'">Tumma keltainen</xsl:when>
                                <xsl:when test="color='yellow'">Keltainen</xsl:when>
                                <xsl:when test="color='yellow-green'">Keltainen/Vihre&#228;</xsl:when>
                                <xsl:when test="color='light green'">Vaalea vihre&#228;</xsl:when>
                                <xsl:when test="color='green'">Vihre&#228;</xsl:when>
                                <xsl:when test="color='medium blue'">Keskisininen</xsl:when>
                                <xsl:when test="color='pale blue'">Vaalea sininen</xsl:when>
                                <xsl:when test="color='blue'">Sininen</xsl:when>
                                <xsl:when test="color='deep blue'">Tumma sininen</xsl:when>
                                <xsl:when test="color='violet'">Violetti</xsl:when>
                                <xsl:otherwise>(tuntematon v&#228;ri)</xsl:otherwise>
                            </xsl:choose>
                        </td>
                    </tr>
                </xsl:if>
                <xsl:if test="count(wratten)>0">
                    <tr>
                        <td>Wratten arvo:</td>
                        <td>
                            <xsl:value-of select="wratten"/>
                        </td>
                    </tr>
                </xsl:if>
                <xsl:if test="count(schott)>0">
                    <tr>
                        <td>Schott arvo:</td>
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
                <li>Visuaalinen luokitus:
                    <xsl:choose>
                        <xsl:when test="contains(./@type,'findingsDeepSkyOCType')">
                            <!-- open starcluster -->
                            <xsl:choose>
                                <xsl:when test="rating = '1'">Ilmeinen, eritt&#228;in kaunis t&#228;htijoukko</xsl:when>
                                <xsl:when test="rating = '2'">Ilmeinen, kaunis t&#228;htijoukko</xsl:when>
                                <xsl:when test="rating = '3'">Helposti havaittava t&#228;htijoukko</xsl:when>
                                <xsl:when test="rating = '4'">Ei kovin ilmeinen t&#228;htijoukko</xsl:when>
                                <xsl:when test="rating = '5'">Juuri n&#228;ht&#228;viss&#228; oleva t&#228;htijoukko</xsl:when>
                                <xsl:when test="rating = '6'">T&#228;htijoukko ep&#228;illen n&#228;hty</xsl:when>
                                <xsl:when test="rating = '7'">Paikassa ei t&#228;hti&#228; n&#228;kyviss&#228;</xsl:when>
                            </xsl:choose>
                        </xsl:when>


                        <xsl:when test="contains(./@type,'findingsDeepSkyDSType')">
                            <!-- Double stars -->
                            <xsl:choose>
                                <xsl:when test="rating = '1'">Kaksoist&#228;hti erotettavissa</xsl:when>
                                <xsl:when test="rating = '2'">Kaksoist&#228;hti n&#228;ytti samalta kuin "8"</xsl:when>
                                <xsl:when test="rating = '3'">Kaksoist&#228;hte&#228; ei erotettavissa</xsl:when>
                            </xsl:choose>
                        </xsl:when>


                        <xsl:otherwise>
                            <!-- other objecttype -->
                            <xsl:choose>
                                <xsl:when test="rating = '1'">Kohde ilmeinen okulaarissa</xsl:when>
                                <xsl:when test="rating = '2'">Helposti havaittavissa suoraan katsoen</xsl:when>
                                <xsl:when test="rating = '3'">Havaittavissa suoraan katsoen</xsl:when>
                                <xsl:when test="rating = '4'">Havaittavissa vain syrj&#228;silm&#228;ll&#228;</xsl:when>
                                <xsl:when test="rating = '5'">Kohde vaikeasti havaittavissa syrj&#228;silm&#228;ll&#228;</xsl:when>
                                <xsl:when test="rating = '6'">Kohde ep&#228;illen havaittavissa</xsl:when>
                                <xsl:when test="rating = '7'">Kohde ei havaittavissa</xsl:when>
                            </xsl:choose>
                        </xsl:otherwise>
                    </xsl:choose>
                </li>


                <xsl:if test="./@stellar='true'">
                    <li>N&#228;ytt&#228;&#228; t&#228;htim&#228;iselt&#228;<br/>
                    </li>
                </xsl:if>


                <xsl:if test="./@resolved='true'">
                    <li>Eroteltavissa<br/>
                    </li>
                </xsl:if>


                <xsl:if test="./@mottled='true'">
                    <li>L&#228;ikik&#228;s<br/>
                    </li>
                </xsl:if>



                <xsl:if test="count(smallDiameter)>0 and count(largeDiameter)>0">
                    <li>N&#228;enn&#228;inen koko: <xsl:call-template name="angle">
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
                        <xsl:if test="./visMag/@fainterThan='true'">Himme&#228;mpi kuin </xsl:if>
                        Magnitudi: <xsl:value-of select="visMag"/>
                        <xsl:if test="./visMag/@uncertain='true'"> (Ep&#228;varma)</xsl:if>
                        <br/>
                    </li>
                </xsl:if>
                <xsl:if test="string-length(chartID)>0">
                    <li>
                        Kartta: <xsl:value-of select="chartID"/>
                        <xsl:if test="./chartID/@nonAAVSOchart='true'"> (ei AVVSO-kartta)</xsl:if>
                        <br/>
                    </li>
                </xsl:if>
                <xsl:if test="count(comparisonStar) > 0"><li>Vertailut&#228;hdet:<br/>
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
                <TITLE>Havaintoloki</TITLE>
            </HEAD>
            <BODY>
                <div align="center" style="font-size:24;font-family:Verdana,Arial;color:#0000C0">Havaintoloki</div>
                <div style="font-size:12;font-family:Verdana, Arial">
                    <h3>Havainnot</h3>
                    <a name="obslist"/>
                    <xsl:apply-templates select="//observation"/>

                    <h3>Raferenssit</h3>

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
                  document.write("Luotu: " + document.lastModified);
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
                            <td>Havaitsija</td>
                            <td>
                                <xsl:text disable-output-escaping="yes">&lt;a href="#havaitsija</xsl:text>
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
                                <td>Paikka</td>
                                <td>
                                    <xsl:text disable-output-escaping="yes">&lt;a href="#paikka</xsl:text>
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
                                    <xsl:when test="count(end) = 1">Alku</xsl:when>
                                    <xsl:otherwise>Aika</xsl:otherwise>
                                </xsl:choose>
                            </td>
                            <td>
                                <xsl:value-of select="substring-before(begin, 'T')"/> klo <xsl:value-of select="substring-after(begin, 'T')"/>
                            </td>
                            <xsl:if test="count(end) = 1">
                                <tr>
                                    <td>Loppu</td>
                                    <td>
                                        <xsl:value-of select="substring-before(end, 'T')"/> klo <xsl:value-of select="substring-after(end, 'T')"/>
                                    </td>
                                </tr>
                            </xsl:if>
                        </tr>

                        <xsl:if test="count(faintestStar) = 1">
                            <tr>
                                <td>Himmein t&#228;hti</td>
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
                                        <xsl:when test="seeing = 1"> (eritt&#228;in hyv&#228;)</xsl:when>
                                        <xsl:when test="seeing = 2"> (hyv&#228;)</xsl:when>
                                        <xsl:when test="seeing = 3"> (keskinkertainen)</xsl:when>
                                        <xsl:when test="seeing = 4"> (huono)</xsl:when>
                                        <xsl:when test="seeing = 5"> (eritt&#228;in huono)</xsl:when>
                                    </xsl:choose>
                                </td>
                            </tr>
                        </xsl:if>


                        <xsl:if test="count(scope) = 1">
                            <tr>
                                <td>Havaintov&#228;line</td>
                                <td>
                                    <xsl:text disable-output-escaping="yes">&lt;a href="#havaintov&#228;line</xsl:text>
                                    <xsl:value-of select="scope"/>
                                    <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                                    <xsl:value-of select="key('scopeKey', scope)/model"/>
                                    <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                                </td>
                            </tr>
                        </xsl:if>

                        <xsl:if test="count(eyepiece) = 1 or count(magnification) = 1">
                            <tr>
                                <td>Okulaari</td>
                                <td>
                                    <xsl:choose>
                                        <xsl:when test="count(eyepiece) = 1">
                                            <xsl:text disable-output-escaping="yes">&lt;a href="#okulaari</xsl:text>
                                            <xsl:value-of select="eyepiece"/>
                                            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                                            <xsl:value-of select="key('eyepieceKey', eyepiece)/model"/>
                                            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                                            <xsl:if test="count(magnification) = 1">
                                                <xsl:text disable-output-escaping="yes"> (Suurennus=</xsl:text>
                                                <xsl:value-of select="magnification"/>
                                                <xsl:text>)</xsl:text>
                                            </xsl:if>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:choose>
                                                <xsl:when test="count(magnification) = 1">
                                                    <xsl:text disable-output-escaping="yes">Suurennus=</xsl:text>
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
                                    <xsl:text disable-output-escaping="yes">&lt;a href="#suodin</xsl:text>
                                    <xsl:value-of select="filter"/>
                                    <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                                    <xsl:value-of select="key('filterKey', filter)/model"/>
                                    <xsl:text disable-output-escaping="yes">&lt;/a&gt; </xsl:text>
                                    <xsl:choose>
                                        <xsl:when test="key('filterKey', filter)/type='other'">Muu</xsl:when>
                                        <xsl:when test="key('filterKey', filter)/type='broad band'">Laajakaista</xsl:when>
                                        <xsl:when test="key('filterKey', filter)/type='narrow band'">Kapeakaista</xsl:when>
                                        <xsl:when test="key('filterKey', filter)/type='O-III'">OIII</xsl:when>
                                        <xsl:when test="key('filterKey', filter)/type='solar'">Aurinko</xsl:when>
                                        <xsl:when test="key('filterKey', filter)/type='H-beta'">H-Beta</xsl:when>
                                        <xsl:when test="key('filterKey', filter)/type='H-alpha'">H-Alpha</xsl:when>
                                        <xsl:when test="key('filterKey', filter)/type='color'">V&#228;ri</xsl:when>
                                        <xsl:when test="key('filterKey', filter)/type='neutral'">Harmaa</xsl:when>
                                        <xsl:when test="key('filterKey', filter)/type='corrective'">korjaava</xsl:when>
                                        <xsl:otherwise>(tuntematon tyyppi)</xsl:otherwise>
                                    </xsl:choose>

                                    <!--<xsl:value-of select="key('filterKey', filter)/type"/>										-->
                                </td>
                            </tr>
                        </xsl:if>


                        <xsl:if test="count(lens) = 1">

                            <tr>

                                <td>Linssi</td>

                                <td>

                                    <xsl:text disable-output-escaping="yes">&lt;a href="#linssi</xsl:text>

                                    <xsl:value-of select="lens"/>

                                    <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

                                    <xsl:value-of select="key('lensKey', lens)/model"/>

                                    <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

                                </td>

                            </tr>

                        </xsl:if>



                        <xsl:if test="count(imager) = 1">

                            <tr>

                                <td>Kamera</td>

                                <td>

                                    <xsl:text disable-output-escaping="yes">&lt;a href="#kamera</xsl:text>

                                    <xsl:value-of select="imager"/>

                                    <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

                                    <xsl:value-of select="key('imagerKey', imager)/model"/>

                                    <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

                                </td>

                            </tr>

                        </xsl:if>



                        <xsl:if test="count(session) = 1">
                            <tr>
                                <td>Istunto</td>
                                <td>
                                    <xsl:text disable-output-escaping="yes">&lt;a href="#istunto</xsl:text>
                                    <xsl:value-of select="session"/>
                                    <xsl:text disable-output-escaping="yes">"&gt;</xsl:text><xsl:value-of select="substring-before(begin, 'T')"/> klo <xsl:value-of select="substring-after(begin, 'T')"/><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                                </td>
                            </tr>
                        </xsl:if>
                    </table>
                </td>


                <td width="100%" valign="top" bgcolor="#EEEEEE">
                    <h5>Kuvaus</h5>
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

        <xsl:text disable-output-escaping="yes">&lt;a href="#obslist"&gt; &gt;&gt; Havainnot &lt;&lt;&lt;/a&gt;</xsl:text>
        <hr/>
    </xsl:template>


</xsl:stylesheet>

