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



    <!-- Formatting Surface Brightness (DSO or Sky-quality)-->

    <xsl:template name="smag">

        <xsl:param name="smag"/>

        <xsl:value-of select="$smag"/>

        <xsl:choose>

            <xsl:when test="$smag[@unit='mags-per-squarearcsec']"> mag/arcsec&#178;</xsl:when>

            <xsl:when test="$smag[@unit='mags-per-squarearcmin']"> mag/arcmin&#178;</xsl:when>

        </xsl:choose>

    </xsl:template>


    <!-- Formatting text for line return carriage in HTML output-->

    <xsl:template name="MultilineTextOutput">
        <xsl:param name="text"/>
        <xsl:choose>
            <xsl:when test="contains($text, '&#10;')">
                <xsl:variable name="text-before-first-break">
                    <xsl:value-of select="substring-before($text, '&#10;')" />
                </xsl:variable>
                <xsl:variable name="text-after-first-break">
                    <xsl:value-of select="substring-after($text, '&#10;')" />
                </xsl:variable>

                <xsl:if test="not($text-before-first-break = '')">
                    <xsl:value-of select="$text-before-first-break" /><br />
                </xsl:if>

                <xsl:if test="not($text-after-first-break = '')">
                    <xsl:call-template name="MultilineTextOutput">
                        <xsl:with-param name="text" select="$text-after-first-break" />
                    </xsl:call-template>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text" /><br />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>







    <!-- converts FROM <date>2001-12-31T12:00:00</date> TO some new format (DEFINED below) -->
    <xsl:template name="FormatDate">
        <xsl:param name="DateTime" />

        <xsl:variable name="year" select="substring($DateTime,1,4)" />
        <xsl:variable name="month-temp" select="substring-after($DateTime,'-')" />
        <xsl:variable name="month" select="substring-before($month-temp,'-')" />
        <xsl:variable name="day-temp" select="substring-after($month-temp,'-')" />
        <xsl:variable name="day" select="substring($day-temp,1,2)" />
        <xsl:variable name="time" select="substring-after($DateTime,'T')" />


        <!-- EUROPEAN FORMAT -->
        <xsl:value-of select="$day"/>
        <xsl:value-of select="'.'"/> <!--18.-->
        <xsl:value-of select="$month"/>
        <xsl:value-of select="'.'"/> <!--18.03.-->
        <xsl:value-of select="$year"/>
        <xsl:value-of select="' '"/> <!--18.03.1976 -->

        <!-- END: EUROPEAN FORMAT -->

    </xsl:template>







    <xsl:template match="session">

        <p>

            <xsl:text disable-output-escaping="yes">&lt;a name="session</xsl:text>

            <xsl:value-of select="@id"/>

            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

            <b>&#8226; Date de la session: <xsl:call-template name="FormatDate">

                    <xsl:with-param name="DateTime" select="substring-before(begin, 'T')"/>

                </xsl:call-template></b>

            <xsl:value-of select="session"/>

            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

        </p>







        <!-- Date of Observation -->

        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

            <tr>

                <td>Début:</td>

                <td>

                    <xsl:call-template name="FormatDate">

                        <xsl:with-param name="DateTime" select="substring(begin, 'T')"/>

                    </xsl:call-template>

                    à <xsl:value-of select="substring(begin,12,8)"/>

                </td>
                <td rowspan="2"><span style="font-size:9;">TU<xsl:value-of select="substring(begin,20,6)"/></span>

                </td>

            </tr>

            <tr>

                <td>Fin:</td>

                <td>
                    <xsl:call-template name="FormatDate">

                        <xsl:with-param name="DateTime" select="substring(end, 'T')"/>

                    </xsl:call-template>

                    à <xsl:value-of select="substring(end,12,8)"/>

                </td>

            </tr>

            <tr><td></td></tr>
        </table>



        <!-- Coobservers -->

        <xsl:if test="count(coObserver)>0">

            <p>&#9675; <span style="font-size:10;">Observateurs additionnels:


                    <xsl:for-each select="coObserver">

                        <xsl:sort select="key('observerKey', .)/name"/>

                        <xsl:text disable-output-escaping="yes">&lt;a href="#observer</xsl:text>

                        <xsl:value-of select="."/>

                        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

                        <xsl:value-of select="key('observerKey', .)/name"/><xsl:text> </xsl:text><xsl:value-of select="key('observerKey', .)/surname"/>

                        <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

                        <xsl:if test="position() != last()">, </xsl:if>

                    </xsl:for-each>

                </span>

            </p>

        </xsl:if>





        <xsl:if test="count(weather)>0 or count(equipment)>0 or count(comments)>0 or count(site)>0">

            <table border="0" cellspacing="0" cellpadding="2" style="font-size:11;font-family:Verdana, Arial;line-height:1.3">

                <!-- Site -->

                <xsl:if test="count(site)>0">

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

                <xsl:if test="count(weather)>0">

                    <tr>

                        <td valign="top">Météo:</td>

                        <td valign="top">

                            <xsl:call-template name="MultilineTextOutput">
                                <xsl:with-param name="text" select="weather" />
                            </xsl:call-template>

                        </td>

                    </tr>

                </xsl:if>





                <!-- Equipment -->

                <xsl:if test="count(equipment)>0">

                    <tr>

                        <td valign="top">Équipement:</td>

                        <td valign="top">

                            <xsl:call-template name="MultilineTextOutput">
                                <xsl:with-param name="text" select="equipment" />
                            </xsl:call-template>

                        </td>

                    </tr>

                </xsl:if>





                <!-- Comments -->

                <xsl:if test="count(comments)>0">

                    <tr>

                        <td valign="top">Commentaires:</td>

                        <td valign="top">

                            <xsl:call-template name="MultilineTextOutput">
                                <xsl:with-param name="text" select="comments" />
                            </xsl:call-template>

                        </td>

                    </tr>

                </xsl:if>

            </table>

        </xsl:if>

        <xsl:call-template name="linkTop"/>

    </xsl:template>







    <xsl:template match="target">

        <p align="center">

            <xsl:text disable-output-escaping="yes">&lt;a name="target</xsl:text>

            <xsl:value-of select="@id"/>

            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

            Objet: <span style="font-size:16;font-weight:bold;font-family:Verdana, Arial;color:blue;text-decoration:underline;">

                <xsl:choose>

                    <xsl:when test="@type='oal:PlanetTargetType' or @type='oal:MoonTargetType' or  @type='oal:SunTargetType'">

                        <xsl:choose>

                            <xsl:when test="name='SUN'">Soleil</xsl:when>

                            <xsl:when test="name='MERCURY'">Mercure</xsl:when>

                            <xsl:when test="name='VENUS'">Vénus</xsl:when>

                            <xsl:when test="name='EARTH'">Terre</xsl:when>

                            <xsl:when test="name='MOON'">Lune</xsl:when>

                            <xsl:when test="name='MARS'">Mars</xsl:when>

                            <xsl:when test="name='JUPITER'">Jupiter</xsl:when>

                            <xsl:when test="name='SATURN'">Saturne</xsl:when>

                            <xsl:when test="name='URANUS'">Uranus</xsl:when>

                            <xsl:when test="name='NEPTUNE'">Neptune</xsl:when>

                            <xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise>

                        </xsl:choose>

                    </xsl:when>

                    <xsl:otherwise><xsl:value-of select="name"/></xsl:otherwise>

                </xsl:choose>

                <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
            </span>

            <xsl:if test="count(constellation)>0"><span style="font-size:10px;"> (<xsl:value-of select="constellation"/>)</span>



            </xsl:if>

            <hr style="width:200px;" />





            <xsl:choose>

                <xsl:when test="@type='oal:deepSkyGX'"><center>Galaxie</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyGC'"><center>Amas globulaire</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyGN'"><center>Nébuleuse diffuse</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyOC'"><center>Amas ouvert</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyPN'"><center>Nébuleuse planétaire</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyQS'"><center>Quasar</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyDS'"><center>Étoile double</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyDN'"><center>Nébuleuse obscure</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyAS'"><center>Astérisme</center></xsl:when>

                <xsl:when test="@type='oal:deepSkySC'"><center>Nuage stellaire</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyMS'"><center>Système d'étoiles multiples</center></xsl:when>

                <xsl:when test="@type='oal:deepSkyCG'"><center>Amas de galaxies</center></xsl:when>

                <xsl:when test="@type='oal:variableStarTargetType'"><center>Étoile variable</center></xsl:when>

                <xsl:when test="@type='oal:SunTargetType'"><center>Soleil</center></xsl:when>

                <xsl:when test="@type='oal:MoonTargetType'"><center>Lune</center></xsl:when>

                <xsl:when test="@type='oal:PlanetTargetType'"><center>Planète</center></xsl:when>

                <xsl:when test="@type='oal:MinorPlanetTargetType'"><center>Objet mineur</center></xsl:when>

                <xsl:when test="@type='oal:CometTargetType'"><center>Comète</center></xsl:when>

                <xsl:when test="@type='oal:UndefinedTargetType'"><center>(Objet indéterminé)</center></xsl:when>

                <xsl:otherwise><center>(Type non précisé)</center></xsl:otherwise>

            </xsl:choose>


        </p>

        <p>

            <xsl:if test="count(alias)>0">



                <div align="center" style="font-size:10">Alias: <span style="color:blue;"><xsl:for-each select="alias">



                            <xsl:value-of select="."/>

                            <xsl:if test="position() != last()">, </xsl:if>

                        </xsl:for-each></span>

                </div>

                <hr style="width:200px;" />

            </xsl:if>

        </p>


        <table border="0" cellspacing="3" cellpadding="3" style="font-size:10;font-family:Verdana,Arial">

            <tr>

                <td valign="top">

                    <table border="1" cellspacing="0" cellpadding="2" width="400" style="font-size:12;font-family:Verdana, Arial;">

                        <xsl:if test="boolean(position/ra)">

                            <tr>

                                <td>AD:</td>

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

                                    <td>Dimensions:</td>

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

                                    <td>Magnitude (v):</td>

                                    <td>

                                        <xsl:value-of select="visMag"/> mag</td>

                                </tr>

                            </xsl:if>


                            <!-- Double Stars -->

                            <xsl:if test="boolean(magComp)">

                                <tr>

                                    <td>Compagnon:</td>

                                    <td>

                                        <xsl:value-of select="magComp"/> mag


                                    </td>

                                </tr>

                            </xsl:if>

                            <xsl:if test="boolean(separation)">

                                <tr>

                                    <td>Séparation:</td>

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

                                    <td>Magnitude surfacique:</td>

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

                                    <td>

                                        <xsl:value-of select="stars"/> étoiles


                                    </td>

                                </tr>

                            </xsl:if>

                            <xsl:if test="boolean(class)">

                                <tr>

                                    <td>Classe Trumpler:</td>

                                    <td>

                                        <xsl:value-of select="class"/>


                                    </td>

                                </tr>

                            </xsl:if>

                            <!-- Galaxy -->


                            <xsl:if test="boolean(hubbleType)">

                                <tr>

                                    <td>Type Hubble:</td>

                                    <td>

                                        <xsl:value-of select="hubbleType"/>

                                        <xsl:choose>

                                            <xsl:when test="(hubbleType = 'Sa') or (hubbleType = 'Sb') or (hubbleType = 'Sc') or (hubbleType = 'Sd')"> (Spirale)</xsl:when>

                                            <xsl:when test="contains(hubbleType, 'SB')"> (Spirale barrée)</xsl:when>

                                            <xsl:when test="(hubbleType = 'S0') or (hubbleType = 'Sb0') or (hubbleType = 'SB0')"> (Lenticulaire)</xsl:when>

                                            <xsl:when test="contains(hubbleType, 'E')"> (Elliptique)</xsl:when>

                                            <xsl:when test="contains(hubbleType, 'Ir')"> (Irrégulière)</xsl:when>

                                            <xsl:otherwise> </xsl:otherwise>

                                        </xsl:choose>


                                    </td>

                                </tr>

                            </xsl:if>

                            <xsl:if test="boolean(pa)">

                                <tr>

                                    <td>Angle de position:</td>

                                    <td>

                                        <xsl:value-of select="pa"/>&#176;


                                    </td>

                                </tr>

                            </xsl:if>


                            <!-- TODO -->

                            <xsl:for-each select="pa/following-sibling::*">

                                <tr>

                                    <td>

                                        <xsl:value-of select="local-name()"/>:</td>

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

                                    <td>

                                        <xsl:value-of select="maxApparentMag"/> mag


                                    </td>

                                </tr>

                            </xsl:if>

                            <xsl:if test="boolean(apparentMag)">

                                <tr>

                                    <td>Minimum:</td>

                                    <td>

                                        <xsl:value-of select="apparentMag"/> mag


                                    </td>

                                </tr>

                            </xsl:if>

                            <xsl:if test="boolean(period)">

                                <tr>

                                    <td>Période:</td>

                                    <td>

                                        <xsl:value-of select="period"/> jours


                                    </td>

                                </tr>

                            </xsl:if>
                        </xsl:if>

                        <!-- ################################################################### -->

                        <!-- TODO: Other subclasses like planets                                 -->

                        <!-- ################################################################### -->





                        <xsl:if test="boolean(observer)">

                            <tr>

                                <td>Source:</td>

                                <td>

                                    <xsl:value-of select="key('observerKey', observer)/surname"/>,

                                    <xsl:text/>

                                    <xsl:value-of select="key('observerKey', observer)/name"/>

                                </td>

                            </tr>

                        </xsl:if>





                        <xsl:if test="boolean(datasource)">

                            <tr>

                                <td>Source:</td>

                                <td>

                                    <xsl:value-of select="datasource"/>

                                </td>

                            </tr>

                        </xsl:if>

                    </table>

                </td>

                <td width="100%" valign="top" bgcolor="#EEEEEE">


                    <xsl:choose>
                        <xsl:when test="boolean(notes)">
                            <b>Informations additionnelles</b>

                            <ul style="margin-left:-13px;">
                                <li type="circle">
                                    <xsl:call-template name="MultilineTextOutput">
                                        <xsl:with-param name="text" select="notes" />
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

            <b>Observateur: </b>

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

                <td>Fuseau horaire:</td>

                <td>TU<xsl:if test="timezone >= 0">+</xsl:if>

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

            <b>Instrument: </b>

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

                    <td>Marque:</td>

                    <td>

                        <xsl:value-of select="vendor"/>

                    </td>

                </tr>

            </xsl:if>





            <tr>

                <td>Diamètre:</td>

                <td>

                    <xsl:value-of select="aperture"/> mm</td>

            </tr>





            <xsl:if test="count(focalLength)>0">

                <tr>

                    <td>Longueur focale:</td>

                    <td>

                        <xsl:value-of select="focalLength"/> mm</td>

                </tr>

            </xsl:if>





            <xsl:if test="count(magnification)>0">

                <tr>

                    <td>Grossissement:</td>

                    <td>

                        <xsl:value-of select="magnification"/> &#215;</td>

                </tr>





            </xsl:if>





            <xsl:if test="count(trueField)>0">

                <tr>

                    <td>Champ de vision réel:</td>

                    <td>

                        <xsl:call-template name="angle">

                            <xsl:with-param name="angle" select="trueField"/>

                        </xsl:call-template>

                    </td>

                </tr>

            </xsl:if>





            <xsl:if test="count(lightGrasp)>0">

                <tr>

                    <td>Transmission lumineuse:</td>

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



                <b>Oculaire zoom: </b>



            </xsl:if>



            <xsl:if test="count(maxFocalLength)=0">



                <b>Oculaire: </b>



            </xsl:if>

            <xsl:value-of select="model"/>

            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

        </p>

        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

            <xsl:if test="count(vendor)>0">

                <tr>

                    <td>Marque:</td>

                    <td>

                        <xsl:value-of select="vendor"/>

                    </td>

                </tr>

            </xsl:if>

            <tr>

                <td>Longueur focale:</td>

                <td>



                    <xsl:value-of select="focalLength"/>



                    <xsl:if test="count(maxFocalLength)>0">-<xsl:value-of select="maxFocalLength"/></xsl:if> mm



                </td>

            </tr>

            <xsl:if test="count(apparentFOV)>0">

                <tr>

                    <td>Champ de vision apparent:</td>

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



            <b>Lentille: </b>



            <xsl:value-of select="model"/>



            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>



        </p>



        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">



            <xsl:if test="count(vendor)>0">



                <tr>



                    <td>Marque:</td>



                    <td>



                        <xsl:value-of select="vendor"/>



                    </td>



                </tr>



            </xsl:if>



            <tr>



                <td>Facteur de longueur focale:</td>



                <td>



                    <xsl:value-of select="factor"/>x</td>



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



                <b>Imageur CCD: </b>



            </xsl:if>



            <xsl:if test="count(pixelsX)=0">



                <b>Imageur: </b>



            </xsl:if>



            <xsl:value-of select="model"/>



            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>



        </p>



        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">



            <xsl:if test="count(vendor)>0">



                <tr>



                    <td>Marque:</td>



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

            <b>Filtre: </b>

            <xsl:value-of select="model"/>

            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

        </p>





        <table border="0" cellspacing="0" cellpadding="2" style="font-size:12;font-family:Verdana, Arial">

            <xsl:if test="count(type)>0">

                <tr>

                    <td>Type:</td>

                    <td>

                        <xsl:choose>

                            <xsl:when test="type='other'">Autre</xsl:when>

                            <xsl:when test="type='broad band'">Broadband</xsl:when>

                            <xsl:when test="type='narrow band'">Narrowband</xsl:when>

                            <xsl:when test="type='O-III'">OIII</xsl:when>

                            <xsl:when test="type='Solar'">Solaire</xsl:when>

                            <xsl:when test="type='H-beta'">H-Beta</xsl:when>

                            <xsl:when test="type='H-alpha'">H-Alpha</xsl:when>

                            <xsl:when test="type='color'">Couleur</xsl:when>

                            <xsl:when test="type='neutral'">Neutre</xsl:when>

                            <xsl:when test="type='corrective'">Correctif</xsl:when>

                            <xsl:otherwise>(Type non précisé)</xsl:otherwise>

                        </xsl:choose>

                    </td>

                </tr>

                <xsl:if test="count(color)>0">

                    <tr>

                        <td>Couleur:</td>

                        <td>

                            <xsl:choose>

                                <xsl:when test="color='light red'">Rouge clair</xsl:when>

                                <xsl:when test="color='red'">Rouge</xsl:when>

                                <xsl:when test="color='deep red'">Rouge foncé</xsl:when>

                                <xsl:when test="color='orange'">Orange</xsl:when>

                                <xsl:when test="color='light yellow'">Jaune clair</xsl:when>

                                <xsl:when test="color='deep yellow'">Jaune foncé</xsl:when>

                                <xsl:when test="color='yellow'">Jaune</xsl:when>

                                <xsl:when test="color='yellow-green'">Jaune/Vert</xsl:when>

                                <xsl:when test="color='light green'">Vert clair</xsl:when>

                                <xsl:when test="color='green'">Vert</xsl:when>

                                <xsl:when test="color='medium blue'">Bleu moyen</xsl:when>

                                <xsl:when test="color='pale blue'">Bleu pâle</xsl:when>

                                <xsl:when test="color='blue'">Bleu</xsl:when>

                                <xsl:when test="color='deep blue'">Bleu foncé</xsl:when>

                                <xsl:when test="color='violet'">Violet</xsl:when>

                                <xsl:otherwise>(Couleur non précisée)</xsl:otherwise>

                            </xsl:choose>

                        </td>

                    </tr>

                </xsl:if>

                <xsl:if test="count(wratten)>0">

                    <tr>

                        <td>Code Wratten:</td>

                        <td>

                            <xsl:value-of select="wratten"/>

                        </td>

                    </tr>

                </xsl:if>

                <xsl:if test="count(schott)>0">

                    <tr>

                        <td>Code Schott:</td>

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

            <xsl:if test="string-length(description)>0">

                <li style="line-height:1.3">

                    <xsl:call-template name="MultilineTextOutput">
                        <xsl:with-param name="text" select="description" />
                    </xsl:call-template>

                    <br/>

                </li>

            </xsl:if>

            <xsl:if test="contains(./@type,'findingsDeepSkyType') or contains(./@type,'findingsDeepSkyOCType') or contains(./@type,'findingsDeepSkyDSType')">

                <!-- Print scale of german Deep Sky List -->

                <li>Appréciation visuelle:

                    <xsl:choose>

                        <xsl:when test="contains(./@type,'findingsDeepSkyOCType')">

                            <!-- open starcluster -->

                            <xsl:choose>

                                <xsl:when test="rating = '1'">Amas splendide, observation fascinante </xsl:when>

                                <xsl:when test="rating = '2'">Amas remarquable</xsl:when>

                                <xsl:when test="rating = '3'">Amas assez intéressant</xsl:when>

                                <xsl:when test="rating = '4'">Amas plutôt discret</xsl:when>

                                <xsl:when test="rating = '5'">Amas très discret; peut facilement passer inaperçu lors d'une recherche</xsl:when>

                                <xsl:when test="rating = '6'">Amas incertain; densité stellaire similaire à l'environnement</xsl:when>

                                <xsl:when test="rating = '7'">Amas indiscernable, quasiment aucune étoile à la position donnée</xsl:when>

                            </xsl:choose>

                        </xsl:when>





                        <xsl:when test="contains(./@type,'findingsDeepSkyDSType')">

                            <!-- Double stars -->

                            <xsl:choose>

                                <xsl:when test="rating = '1'">Étoile double pouvant être séparée</xsl:when>

                                <xsl:when test="rating = '2'">Étoile double apparaissant comme un "8"</xsl:when>

                                <xsl:when test="rating = '3'">Étoile double impossible à séparer</xsl:when>

                            </xsl:choose>

                        </xsl:when>





                        <xsl:otherwise>

                            <!-- other objecttype -->

                            <xsl:choose>

                                <xsl:when test="rating = '1'">Remarquable objet à l'oculaire</xsl:when>

                                <xsl:when test="rating = '2'">Bien visible en vision directe</xsl:when>

                                <xsl:when test="rating = '3'">Visible en vision directe</xsl:when>

                                <xsl:when test="rating = '4'">Visible seulement en vision décalée</xsl:when>

                                <xsl:when test="rating = '5'">Difficilement visible en vision décalée</xsl:when>

                                <xsl:when test="rating = '6'">Objet difficilement discernable</xsl:when>

                                <xsl:when test="rating = '7'">Objet indiscernable</xsl:when>

                            </xsl:choose>

                        </xsl:otherwise>

                    </xsl:choose>

                </li>





                <xsl:if test="./@stellar='true'">

                    <li>Apparaît stellaire<br/>

                    </li>

                </xsl:if>





                <xsl:if test="./@resolved='true'">

                    <li>Apparaît résolu<br/>

                    </li>

                </xsl:if>





                <xsl:if test="./@mottled='true'">

                    <li>Apparaît tacheté<br/>

                    </li>

                </xsl:if>





                <xsl:if test="./@extended='true'">

                    <li>Apparaît laminaire<br/>

                    </li>

                </xsl:if>



                <xsl:if test="count(smallDiameter)>0 and count(largeDiameter)>0">

                    <li>Dimension apparente: <xsl:call-template name="angle">

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



                    <li>

                        <xsl:if test="./visMag/@fainterThan='true'">Plus faible que </xsl:if>

                        Magnitude: <xsl:value-of select="visMag"/>

                        <xsl:if test="./visMag/@uncertain='true'"> (Incertain)</xsl:if>

                        <br/>

                    </li>

                </xsl:if>

                <xsl:if test="string-length(chartID)>0">

                    <li>

                        Carte: <xsl:value-of select="chartID"/>

                        <xsl:if test="./chartID/@nonAAVSOchart='true'"> (carte non-AAVSO)</xsl:if>

                        <br/>

                    </li>

                </xsl:if>

                <xsl:if test="count(comparisonStar) > 0"><li>Étoiles de comparaison (mag):


                        <xsl:for-each select="comparisonStar">

                            &#8226;<xsl:value-of select="."/>

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

        <p align="center"><xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text><xsl:value-of select="$imgFile"/><xsl:text disable-output-escaping="yes">" target="_blank"&gt;</xsl:text><xsl:text disable-output-escaping="yes">&lt;</xsl:text><xsl:value-of select="$imgTag"/><xsl:text disable-output-escaping="yes">&gt;&lt;/a&gt;</xsl:text><xsl:text disable-output-escaping="yes"> </xsl:text>

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

                <TITLE>Compte-rendu d'Observation Astronomique (CROA)</TITLE>

            </HEAD>

            <BODY>

                <div align="center" style="font-size:24;font-family:Verdana,Arial;color:#0000C0">Compte-Rendu d'Observation Astronomique (CROA)<hr/></div>

                <div style="font-size:12;font-family:Verdana, Arial">


                    <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;"><p>Observateur(s)</p></div>
                    <xsl:for-each select="//observers/observer">

                        <xsl:sort select="name"/>

                        <xsl:sort select="surname"/>

                        <xsl:apply-templates select="."/>

                    </xsl:for-each>


                    <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;"><p>Détails session(s)</p></div>



                    <xsl:for-each select="//sessions/session">

                        <xsl:sort select="begin"/>

                        <xsl:apply-templates select="."/>

                    </xsl:for-each>

                    <a name="obslist"/>

                    <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;"><p>Liste des observations</p></div>



                    <xsl:apply-templates select="//observation">
                        <xsl:sort select="begin" data-type="number" order="ascending"/>
                    </xsl:apply-templates>







                    <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;"><p>Site(s) d'observation</p></div>

                    <xsl:for-each select="//sites/site">

                        <xsl:sort select="latitude" data-type="number" order="descending"/>

                        <xsl:apply-templates select="."/>

                    </xsl:for-each>



                    <div align="center" style="font-size:20;color:#0000C0;background-color:lightsteelblue;"><p>Matériel</p></div>

                    <div align="center" style="text-decoration:underline;font-size:10px;">Instruments</div>
                    <xsl:for-each select="//scopes/scope">

                        <xsl:sort select="aperture" data-type="number" order="descending"/>

                        <xsl:sort select="magnification" data-type="number" order="descending"/>

                        <xsl:apply-templates select="."/>

                    </xsl:for-each>



                    <div align="center" style="text-decoration:underline;font-size:10px;">Oculaires</div>

                    <xsl:for-each select="//eyepieces/eyepiece">

                        <xsl:sort select="focalLength" data-type="number" order="descending"/>

                        <xsl:sort select="apparentFOV"/>

                        <xsl:apply-templates select="."/>

                    </xsl:for-each>



                    <div align="center" style="text-decoration:underline;font-size:10px;">Lentilles</div>

                    <xsl:for-each select="//lenses/lens">



                        <xsl:sort select="factor"/>



                        <xsl:sort select="model"/>



                        <xsl:apply-templates select="."/>



                    </xsl:for-each>






                    <div align="center" style="text-decoration:underline;font-size:10px;">Filtres</div>
                    <xsl:for-each select="//filters/filter">



                        <xsl:sort select="model"/>



                        <xsl:sort select="type"/>



                        <xsl:apply-templates select="."/>



                    </xsl:for-each>






                    <div align="center" style="text-decoration:underline;font-size:10px;">Imageurs</div>
                    <xsl:for-each select="//imagers/imager">



                        <xsl:sort select="model"/>



                        <xsl:sort select="type"/>



                        <xsl:apply-templates select="."/>



                    </xsl:for-each>


                    <div style="font-size:10;"><xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
                        <script type="text/javascript">

                            <xsl:text disable-output-escaping="yes">

                  &#60;!--

                  document.write("Créé le " + document.lastModified.replace(/(\d{2})\/(\d{2})/,"$2/$1"));

                  //--&#62;

               </xsl:text>

                        </script>
                        <xsl:text disable-output-escaping="yes">&lt;br&gt;avec &lt;a href="https://github.com/capape/observation-manager" target="_blank"&gt;Observation Manager&lt;/a&gt;</xsl:text>
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

                    <table border="1" cellspacing="0" cellpadding="2" width="400" style="font-size:14;font-family:Verdana,Arial">

                        <tr>

                            <td>Observateur</td>

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

                                    <xsl:when test="count(end) = 1">Début</xsl:when>

                                    <xsl:otherwise>Heure</xsl:otherwise>

                                </xsl:choose>

                            </td>

                            <td>

                                <xsl:call-template name="FormatDate">

                                    <xsl:with-param name="DateTime" select="substring(begin, 'T')"/>

                                </xsl:call-template>

                                à <xsl:value-of select="substring(begin,12,8)"/>

                            </td>

                            <xsl:if test="count(end) = 1">

                                <tr>

                                    <td>Fin</td>

                                    <td>

                                        <xsl:call-template name="FormatDate">

                                            <xsl:with-param name="DateTime" select="substring(end, 'T')"/>

                                        </xsl:call-template>

                                        à <xsl:value-of select="substring(end,12,8)"/>

                                    </td>

                                </tr>

                            </xsl:if>

                        </tr>



                        <xsl:if test="count(faintestStar) = 1">

                            <tr>

                                <td>Mvlon</td>

                                <td>

                                    <xsl:value-of select="faintestStar"/> mag

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

                                        <xsl:when test="seeing = 1"> (très bon)</xsl:when>

                                        <xsl:when test="seeing = 2"> (bon)</xsl:when>

                                        <xsl:when test="seeing = 3"> (moyen)</xsl:when>

                                        <xsl:when test="seeing = 4"> (mauvais)</xsl:when>

                                        <xsl:when test="seeing = 5"> (très mauvais)</xsl:when>

                                    </xsl:choose>

                                </td>

                            </tr>

                        </xsl:if>





                        <xsl:if test="count(scope) = 1">

                            <tr>

                                <td>Instrument</td>

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

                                <td>Oculaire</td>

                                <td>

                                    <xsl:choose>

                                        <xsl:when test="count(eyepiece) = 1">

                                            <xsl:text disable-output-escaping="yes">&lt;a href="#eyepiece</xsl:text>

                                            <xsl:value-of select="eyepiece"/>

                                            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

                                            <xsl:value-of select="key('eyepieceKey', eyepiece)/model"/>

                                            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

                                            <xsl:if test="count(magnification) = 1">

                                                <xsl:text disable-output-escaping="yes"> (G=</xsl:text>

                                                <xsl:value-of select="magnification"/>

                                                <xsl:text>)</xsl:text>

                                            </xsl:if>

                                        </xsl:when>

                                        <xsl:otherwise>

                                            <xsl:choose>

                                                <xsl:when test="count(magnification) = 1">

                                                    <xsl:text disable-output-escaping="yes">G=</xsl:text>

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

                                <td>Filtre</td>

                                <td>

                                    <xsl:text disable-output-escaping="yes">&lt;a href="#filter</xsl:text>

                                    <xsl:value-of select="filter"/>

                                    <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

                                    <xsl:value-of select="key('filterKey', filter)/model"/>

                                    <xsl:text disable-output-escaping="yes">&lt;/a&gt; </xsl:text>

                                    <xsl:choose>

                                        <xsl:when test="key('filterKey', filter)/type='other'">Autre</xsl:when>

                                        <xsl:when test="key('filterKey', filter)/type='broad band'">Broadband</xsl:when>

                                        <xsl:when test="key('filterKey', filter)/type='narrow band'">Narrowband</xsl:when>

                                        <xsl:when test="key('filterKey', filter)/type='O-III'">OIII</xsl:when>

                                        <xsl:when test="key('filterKey', filter)/type='solar'">Solaire</xsl:when>

                                        <xsl:when test="key('filterKey', filter)/type='H-beta'">H-Beta</xsl:when>

                                        <xsl:when test="key('filterKey', filter)/type='H-alpha'">H-Alpha</xsl:when>

                                        <xsl:when test="key('filterKey', filter)/type='color'">Couleur</xsl:when>

                                        <xsl:when test="key('filterKey', filter)/type='neutral'">Neutre</xsl:when>

                                        <xsl:when test="key('filterKey', filter)/type='corrective'">Correctif</xsl:when>

                                        <xsl:otherwise>(Type non précisé)</xsl:otherwise>

                                    </xsl:choose>



                                    <!--<xsl:value-of select="key('filterKey', filter)/type"/>										-->

                                </td>

                            </tr>

                        </xsl:if>





                        <xsl:if test="count(lens) = 1">



                            <tr>



                                <td>Lentille</td>



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



                                <td>Imageur</td>



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

                                    </xsl:call-template><span style="font-size:9;">TU<xsl:value-of select="substring(begin,20,6)"/></span>

                                    <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>

                                </td>

                            </tr>

                        </xsl:if>

                    </table>

                </td>





                <td width="100%" valign="top" bgcolor="#EEEEEE">

                    <h5>Description de l'observation</h5>

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



        <div style="font-size:10"><xsl:text disable-output-escaping="yes">&lt;a href="#obslist"&gt; &gt;&gt; Observations &lt;&lt;&lt;/a&gt;</xsl:text></div>

        <hr/>

    </xsl:template>





</xsl:stylesheet>




