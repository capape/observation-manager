<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:external="http://ExternalFunction.xalan-c++.xml.apache.org" exclude-result-prefixes="external">
    
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
        <html>
            <head>
                <title>Observation logs - internal XSL</title>
                <style type="text/css">
                    <![CDATA[
                    body {
                        font-family: 'Arial'
                    }
                ]]>
                </style>
            </head>
            <body>
                <h1>Observation</h1>
                
                
                
                <div class="sessions">
                    <a name="sessions"/>
                    <h2>Sessions</h2>
                    <ul>
                        <xsl:for-each select="//sessions/session">
                            <xsl:sort select="begin"/>
                            <li>
                                <div>                    
                                    <xsl:text disable-output-escaping="yes">&lt;a href="#session</xsl:text>
                                    <xsl:value-of select="@id"/>
                                    <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                                    <span><xsl:value-of select="begin"/> - <xsl:value-of select="end"/></span>                            
                                    <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                                </div>
                            </li>
                        </xsl:for-each>
                    </ul>
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
            </body>
        </html>
    </xsl:template>
    
    
    <xsl:template match="session">
        <xsl:variable name="currentSession" select="@id"/>
        <div class="session">
            <xsl:text disable-output-escaping="yes">&lt;a name="session</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <span>Session:</span><span class="date">
                <xsl:value-of select="begin"/>
                <xsl:value-of select="session"/>
                <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text></span>
        </div>
        
        
        
        <div class="dateSession">
            <div>
                <span>Begin:</span>
                <span><xsl:value-of select="begin"/></span>
            </div>
            <div>
                <span>End:</span>
                <span><xsl:value-of select="end"/></span>
            </div>
        </div>
        
        <!-- Coobservers -->
        <xsl:if test="count(coObserver)>0">
            <div class="observers">
                <span>Observers:</span>
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
            </div>
        </xsl:if>
        
        
        <xsl:if test="count(weather)>0 or count(equipment)>0 or count(comments)>0">
            <div class="sessionInfo">
                <ul>
                    <!-- Weather -->
                    <xsl:if test="count(weather)>0">
                        <li>
                            <span>Weather:</span>
                            <span><xsl:value-of select="weather"/></span>
                        </li>
                    </xsl:if>
                    
                    
                    <!-- Equipment -->
                    <xsl:if test="count(equipment)>0">
                        <li>
                            <span>Equipment:</span>
                            <span><xsl:value-of select="equipment"/></span>
                        </li>
                    </xsl:if>
                    
                    
                    <!-- Comments -->
                    <xsl:if test="count(comments)>0">
                        <li>
                            <span valign="top">Comments:</span>
                            <span valign="top"><xsl:value-of select="comments"/></span>
                        </li>
                    </xsl:if>
                </ul>
            </div>
        </xsl:if>
        <h3>Observations</h3>
        <ul>
            <xsl:for-each select="//observation[session=$currentSession]">   
            <xsl:sort select="begin" data-type="text"/>             
                <li>
                    <div>                    
                        <xsl:variable name="currentTarget" select="key('targetKey', target)"/>
                        <xsl:text disable-output-escaping="yes">&lt;a href="#observation</xsl:text>
                        <xsl:value-of select="@id"/>
                        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                        <span><xsl:value-of select="begin"/> - <xsl:value-of select="$currentTarget/name"/></span>                            
                        <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                    </div>
                </li>
            </xsl:for-each>
        </ul>
        <div class="observations">
            <xsl:for-each select="//observation[session=$currentSession]">   
                <xsl:sort select="begin" data-type="text"/>    
                <xsl:apply-templates select="."/>
            </xsl:for-each>
           
        </div>
    </xsl:template>
    
    <xsl:template match="observation">        
        <xsl:text disable-output-escaping="yes">&lt;a name="observation</xsl:text>
        <xsl:value-of select="@id"/>
        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>            
        <div class="observation">
            <xsl:apply-templates select="key('targetKey', target)"/>
            
            <div class="date">
                <div>
                    <span>Begin:</span>
                    <span><xsl:value-of select="begin"/></span>
                </div>
                <div>
                    <span>End:</span>
                    <span><xsl:value-of select="end"/></span>
                </div>
            </div>
            
            
            <div class="observer">
                <span>Observer:</span>
                <span>
                    <xsl:text disable-output-escaping="yes">&lt;a href="#observer</xsl:text>
                    <xsl:value-of select="observer"/>
                    <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                    <xsl:value-of select="key('observerKey', observer)/name"/><xsl:text/><xsl:value-of select="key('observerKey', observer)/surname"/>
                    <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                </span>
            </div>
            
            
            <xsl:if test="count(site) = 1">
                <div class="site">
                    <span>Site</span>
                    <span>
                        <xsl:text disable-output-escaping="yes">&lt;a href="#site</xsl:text>
                        <xsl:value-of select="site"/>
                        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                        <xsl:value-of select="key('siteKey', site)/name"/>
                        <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                    </span>
                </div>
            </xsl:if>
            
            <xsl:if test="count(faintestStar) = 1">
                <div><span>Faintest Star:</span><span><xsl:value-of select="faintestStar"/> mag</span></div>
            </xsl:if>
            <xsl:if test="count(seeing) = 1">
                <div><span>Seeing:</span><span><xsl:value-of select="seeing"/>
                        <xsl:choose>
                            <xsl:when test="seeing = 1"> (very good)</xsl:when>
                            <xsl:when test="seeing = 2"> (good)</xsl:when>
                            <xsl:when test="seeing = 3"> (fair)</xsl:when>
                            <xsl:when test="seeing = 4"> (bad)</xsl:when>
                            <xsl:when test="seeing = 5"> (very bad)</xsl:when>
                        </xsl:choose>
                    </span>
                </div>
            </xsl:if>
            <xsl:if test="count(imager) = 1">
                <div>
                    <span>
                        Camera:
                    </span>
                    <span>
                        <xsl:text disable-output-escaping="yes">&lt;a href="#imager</xsl:text>
                        <xsl:value-of select="imager"/>
                        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                        <xsl:value-of select="key('imagerKey', imager)/model"/>
                        <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                    </span>
                </div>
            </xsl:if>
            <div>
                <span>Visual impression</span>
                <div>
                    <xsl:for-each select="result">
                        <xsl:apply-templates select="."/>
                        <xsl:if test="position()!=last()">
                            
                        </xsl:if>
                    </xsl:for-each>
                </div>
            </div>
            <div>
                <xsl:for-each select="image"><xsl:apply-templates select="."/></xsl:for-each>
            </div>
            
        </div>
    </xsl:template>
    
    <xsl:template name="linkTop">
        <xsl:text disable-output-escaping="yes">&lt;a href="#obslist"&gt; &gt;&gt; Top &lt;&lt;&lt;/a&gt;</xsl:text>
    </xsl:template>
    
    
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
        <div class="target">
            <xsl:text disable-output-escaping="yes">&lt;a name="target</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <h4>Object</h4>
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
        </div>
        
        
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
        
        <xsl:if test="count(alias)>0">
            <div class="targetAlias"><span>Alias: </span><xsl:for-each select="alias">
                    <span>
                        <xsl:value-of select="."/>
                        <xsl:if test="position() != last()">, </xsl:if>
                    </span>
                </xsl:for-each>
            </div>
        </xsl:if>
        
        
        <div class="position">
            <xsl:if test="boolean(position/ra)">
                <div>
                    <span>RA:</span>
                    <span>
                        <xsl:call-template name="formatHHMM">
                            <xsl:with-param name="node" select="position/ra"/>
                        </xsl:call-template>
                    </span>
                </div>
            </xsl:if>
            
            
            <xsl:if test="boolean(position/dec)">
                <div>
                    <span>Dec:</span>
                    <span>
                        <xsl:call-template name="formatDDMM">
                            <xsl:with-param name="node" select="position/dec"/>
                        </xsl:call-template>
                    </span>
                </div>
            </xsl:if>
        </div>
        <div class="optionaldata">
            
            <!-- Output from attributes of Subclasses -->
            <xsl:if test="contains(@type,'oal:deepSky')">
                <!-- Deep Sky -->
                <xsl:if test="boolean(smallDiameter) and boolean(largeDiameter)">
                    <div class="deepksy">
                        <span>Size:</span>
                        <span>
                            <xsl:call-template name="angle">
                                <xsl:with-param name="angle" select="smallDiameter"/>
                            </xsl:call-template> &#215;
                            <xsl:call-template name="angle">
                                <xsl:with-param name="angle" select="largeDiameter"/>
                            </xsl:call-template>
                        </span>
                    </div>
                </xsl:if>
                
                
                <xsl:if test="boolean(visMag)">
                    <div class="magnitude">
                        <span>m(vis):</span>
                        <span>
                            <xsl:value-of select="visMag"/> mag</span>
                    </div>
                </xsl:if>
                
                
                <xsl:if test="boolean(surfBr)">
                    <div class="brightness">
                        <span>SB:</span>
                        <span>
                            <xsl:value-of select="surfBr"/> mags/sq.arcmin</span>
                    </div>
                </xsl:if>
                
                
                <!-- TODO -->
                <xsl:for-each select="surfBr/following-sibling::*">
                    <div class="brigthness">
                        <span>
                            <xsl:value-of select="local-name()"/>:</span>
                        <span>
                            <xsl:value-of select="."/>
                        </span>
                    </div>
                </xsl:for-each>
            </xsl:if>
            
            
            <!-- ################################################################### -->
            <!-- TODO: Other subclasses like planets                                 -->
            <!-- ################################################################### -->
            
            
            <xsl:if test="boolean(observer)">
                <div>
                    <span>Origin:</span>
                    <span>
                        <xsl:value-of select="key('observerKey', observer)/surname"/>,
                        <xsl:text/>
                        <xsl:value-of select="key('observerKey', observer)/name"/>
                    </span>
                </div>
            </xsl:if>
            
            
            <xsl:if test="boolean(datasource)">
                <div>
                    <span>Origin:</span>
                    <span>
                        <xsl:value-of select="datasource"/>
                    </span>
                </div>
            </xsl:if>
        </div>
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
        <div class="observer">
            <xsl:text disable-output-escaping="yes">&lt;a name="observer</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Observer: </b>
            <xsl:value-of select="name"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="surname"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        <xsl:if test="count(contact) > 0">Contacts:<br/>
            <ul>
                <xsl:for-each select="contact">
                    <li>
                        <xsl:value-of select="."/>
                    </li>
                </xsl:for-each>
            </ul>
        </xsl:if>
    </xsl:template>
    
    
    <xsl:template match="site">
        <div>
            <xsl:text disable-output-escaping="yes">&lt;a name="site</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Site: </b>
            <xsl:value-of select="name"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        
        
        <div class="site">
            <div>
                <span>Longitude:</span>
                <span>
                    <xsl:call-template name="angle">
                        <xsl:with-param name="angle" select="longitude"/>
                    </xsl:call-template>
                </span>
            </div>
            
            
            <div>
                <span>Latitude:</span>
                <span>
                    <xsl:call-template name="angle">
                        <xsl:with-param name="angle" select="latitude"/>
                    </xsl:call-template>
                </span>
            </div>
            
            
            <div>
                <span>Timezone:</span>
                <span>UT<xsl:if test="timezone >= 0">+</xsl:if>
                    <xsl:value-of select="timezone"/> min</span>
            </div>
        </div>
        
    </xsl:template>
    
    
    <xsl:template match="scope">
        <div>
            <xsl:text disable-output-escaping="yes">&lt;a name="scope</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Optics: </b>
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        
        
        
        <xsl:if test="count(type)>0">
            <div>
                <span>Type:</span>
                <span>
                    <xsl:value-of select="type"/>
                </span>
            </div>
        </xsl:if>
        
        
        <xsl:if test="count(vendor)>0">
            <div>
                <span>Vendor:</span>
                <span>
                    <xsl:value-of select="vendor"/>
                </span>
            </div>
        </xsl:if>
        
        
        <div>
            <span>Aperture:</span>
            <span>
                <xsl:value-of select="aperture"/> mm</span>
        </div>
        
        
        <xsl:if test="count(focalLength)>0">
            <div>
                <span>Focal length:</span>
                <span>
                    <xsl:value-of select="focalLength"/> mm</span>
            </div>
        </xsl:if>
        
        
        <xsl:if test="count(magnification)>0">
            <div>
                <span>Magnification:</span>
                <span>
                    <xsl:value-of select="magnification"/> &#215;</span>
            </div>
            
            
        </xsl:if>
        
        
        <xsl:if test="count(trueField)>0">
            <div>
                <span>True field of view:</span>
                <span>
                    <xsl:call-template name="angle">
                        <xsl:with-param name="angle" select="trueField"/>
                    </xsl:call-template>
                </span>
            </div>
        </xsl:if>
        
        
        <xsl:if test="count(lightGrasp)>0">
            <div>
                <span>Light grasp:</span>
                <span>
                    <xsl:value-of select="lightGrasp"/>
                </span>
            </div>
        </xsl:if>
        
    </xsl:template>
    
    
    <xsl:template match="eyepiece">
        <div class="eyepiece">
            <xsl:text disable-output-escaping="yes">&lt;a name="eyepiece</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <xsl:if test="count(maxFocalLength)>0">
                
                <span>Zoom eyepiece: </span>
                
            </xsl:if>
            
            <xsl:if test="count(maxFocalLength)=0">
                
                <span>Eyepiece: </span>
                
            </xsl:if>
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        
        <xsl:if test="count(vendor)>0">
            <div>
                <span>Vendor:</span>
                <span>
                    <xsl:value-of select="vendor"/>
                </span>
            </div>
        </xsl:if>
        <div>
            <span>Focal length:</span>
            <span>
                
                <xsl:value-of select="focalLength"/>
                
                <xsl:if test="count(maxFocalLength)>0">-<xsl:value-of select="maxFocalLength"/></xsl:if> mm
                
            </span>
        </div>
        <xsl:if test="count(apparentFOV)>0">
            <div>
                <span>Apparent field of view:</span>
                <span>
                    <xsl:call-template name="angle">
                        <xsl:with-param name="angle" select="apparentFOV"/>
                    </xsl:call-template>
                </span>
            </div>
        </xsl:if>
        
        
    </xsl:template>
    
    
    <xsl:template match="lens">
        
        <div class="lens">
            
            <xsl:text disable-output-escaping="yes">&lt;a name="lens</xsl:text>
            
            <xsl:value-of select="@id"/>
            
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            
            <span>Lens: </span>
            
            <xsl:value-of select="model"/>
            
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
            
        </div>
        
        
        <xsl:if test="count(vendor)>0">
            
            <div>
                <span>Vendor:</span>
                <span><xsl:value-of select="vendor"/></span>
            </div>
            
        </xsl:if>
        
        <div>
            <span>Focal length factor:</span>
            <span><xsl:value-of select="factor"/> mm</span>
        </div>
    </xsl:template>
    <xsl:template match="imager">
        
        <div class="imager">
            <xsl:text disable-output-escaping="yes">&lt;a name="imager</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            
            <xsl:if test="count(pixelsX)>0">
                <span>CCD Camera: </span>
            </xsl:if>
            
            <xsl:if test="count(pixelsX)=0">
                <span>Camera: </span>
            </xsl:if>
            
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        <xsl:if test="count(vendor)>0">
            <div>
                <span>Vendor:</span>
                <span><xsl:value-of select="vendor"/></span>
            </div>
        </xsl:if>
        
        <xsl:if test="count(pixelsX)>0">
            <div>name=""
                <span>Pixel:</span>
                <span><xsl:value-of select="pixelsX"/>x<xsl:value-of select="pixelsY"/></span>
            </div>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="filter">
        <div>
            <xsl:text disable-output-escaping="yes">&lt;a name="filter</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b>Filter: </b>
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        
        
        
        <xsl:if test="count(type)>0">
            <div>
                <span>Typ:</span>
                <span>
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
                </span>
            </div>
            <xsl:if test="count(color)>0">
                <div>
                    <span>Farbe:</span>
                    <span>
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
                    </span>
                </div>
            </xsl:if>
            <xsl:if test="count(wratten)>0">
                <div>
                    <span>Wratten value:</span>
                    <span>
                        <xsl:value-of select="wratten"/>
                    </span>
                </div>
            </xsl:if>
            <xsl:if test="count(schott)>0">
                <div>
                    <span>Schott value:</span>
                    <span>
                        <xsl:value-of select="schott"/>
                    </span>
                </div>
            </xsl:if>
        </xsl:if>
        
        
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
        <span><xsl:text disable-output-escaping="yes">&lt;</xsl:text><xsl:value-of select="$imgTag"/><xsl:text disable-output-escaping="yes">&gt;</xsl:text>
            <xsl:value-of select="$imgFile"/>
        </span>
    </xsl:template>
    <xsl:output method="html"/>
</xsl:stylesheet>

