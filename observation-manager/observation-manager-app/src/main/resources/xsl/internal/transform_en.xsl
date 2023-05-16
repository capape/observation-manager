<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"                
                xmlns:external="http://ExternalFunction.xalan-c++.xml.apache.org" exclude-result-prefixes="external">
    
    
    <xsl:param name="LANGUAGE_TEXTS_CURRENT" select="document('./internal_texts_en.xml')/texts"/>
    
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
                <title><xsl:call-template name="language-text"><xsl:with-param name="text">document.title</xsl:with-param></xsl:call-template></title>
                <style type="text/css">
                    <![CDATA[


                                        
body {
    font-family: 'Arial';
    font-size: 12px;
}

h1 {
    align-content: center;
}
.sessions {
    border-top-style: solid;
    margin-left: 20px;    
}

.sessions ul li{
    font-size: 10px;
    
}

.session {
    background-color: lightgray;
}

.session div {
    margin-left: 10px;
}


.session span.date {
    font-weight: bold;
}


.observation {
       background-color: white;
    margin-left: 60px;    
}

.position {
    font-family: Courier;   
    color: black;
}


   

                
]]>
                </style>
            </head>
            <body>
                <h1><xsl:call-template name="language-text"><xsl:with-param name="text">h1.observations</xsl:with-param></xsl:call-template></h1>
                
                <div class="summary">
                    
                    <div><span class="label"><xsl:call-template name="language-text"><xsl:with-param name="text">summary.sessions</xsl:with-param></xsl:call-template></span><span><xsl:value-of select="count(//sessions/session)"/></span></div>
                    <div><span class="label"><xsl:call-template name="language-text"><xsl:with-param name="text">summary.observations</xsl:with-param></xsl:call-template></span><span><xsl:value-of select="count(//observation)"/></span></div>                      
                    <a href="targetlist">List of targets</a>
                    <div class="observers">
                        <xsl:for-each select="//observers/observer">                        
                            <xsl:sort select="surname"/>
                            <xsl:sort select="name"/>
                            <xsl:apply-templates select="."/>
                        </xsl:for-each>
                    </div>
                </div>
                
                <div class="sessions">
                    
                    
                    <a name="sessions"/>
                    <h2><xsl:call-template name="language-text"><xsl:with-param name="text">session.sessions.list</xsl:with-param></xsl:call-template></h2>
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
                    
                    <h2><xsl:call-template name="language-text"><xsl:with-param name="text">targets.targetlist</xsl:with-param></xsl:call-template></h2>
                    <a name="targetlist"/>
                    <div class="targetlist">
                        <ul>
                            <xsl:for-each select="//targets/target">
                                <xsl:sort select="constellation"/> 
                                <xsl:sort select="name"/>                           
                                <li>
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
                                    
                                    
                                    <!--
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
                                         
                                    -->
                                    <xsl:if test="count(constellation)>0"> ( <xsl:value-of select="constellation"/> )</xsl:if>
                                </li>
                            </xsl:for-each>
                        </ul>
                    </div>
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
            <span class="date">
                <xsl:value-of select="begin"/>
                <xsl:value-of select="session"/>
                <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text></span>          
            
            
            
            <div class="date">
                <div>
                    <span class="datelabel"><xsl:call-template name="language-text"><xsl:with-param name="text">session.date.begin</xsl:with-param></xsl:call-template></span>
                    <span><xsl:value-of select="begin"/></span>
                </div>
                <div>
                    <span class="datelabel"><xsl:call-template name="language-text"><xsl:with-param name="text">session.date.end</xsl:with-param></xsl:call-template></span>
                    <span><xsl:value-of select="end"/></span>
                </div>
            </div>
            
            <!-- Coobservers -->
            <xsl:if test="count(coObserver)>0">
                <div class="observers">                
                    <ul>
                        <xsl:for-each select="coObserver">
                            <xsl:sort select="key('observerKey', .)/name"/>
                            <li>
                                <xsl:text disable-output-escaping="yes">&lt;a href="#observer</xsl:text>
                                <xsl:value-of select="."/>
                                <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                                <xsl:value-of select="key('observerKey', .)/name"/><xsl:text> </xsl:text><xsl:value-of select="key('observerKey', .)/surname"/>
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
            <h3><xsl:call-template name="language-text"><xsl:with-param name="text">session.observations</xsl:with-param></xsl:call-template></h3>
            <ul>
                <xsl:for-each select="//observation[session=$currentSession]">   
                    <xsl:sort select="begin" data-type="text"/>             
                    <li>
                        <div>                    
                            <xsl:variable name="currentTarget" select="key('targetKey', target)"/>
                            <xsl:text disable-output-escaping="yes">&lt;a href="#observation</xsl:text>
                            <xsl:value-of select="@id"/>
                            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                            <span><xsl:value-of select="begin"/><xsl:text> </xsl:text><xsl:value-of select="$currentTarget/name"/></span>                            
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
                    <span class="datelabel"><xsl:call-template name="language-text"><xsl:with-param name="text">observation.date.begin</xsl:with-param></xsl:call-template></span>
                    <span><xsl:value-of select="begin"/></span>
                </div>
                <div>
                    <span class="datelabel"><xsl:call-template name="language-text"><xsl:with-param name="text">observation.date.end</xsl:with-param></xsl:call-template></span>
                    <span><xsl:value-of select="end"/></span>
                </div>
            </div>
            
            
            <!--
                 <div class="observer">
                 <span><xsl:call-template name="language-text"><xsl:with-param name="text">observation.observer</xsl:with-param></xsl:call-template></span>
                 <span>
                 <xsl:text disable-output-escaping="yes">&lt;a href="#observer</xsl:text>
                 <xsl:value-of select="observer"/>
                 <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                 <xsl:value-of select="key('observerKey', observer)/name"/><xsl:text/><xsl:value-of select="key('observerKey', observer)/surname"/>
                 <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
                 </span>
                 </div>
            -->  
            
            <xsl:if test="count(site) = 1">
                <div class="site">
                    <span><xsl:call-template name="language-text"><xsl:with-param name="text">observation.site</xsl:with-param></xsl:call-template></span>
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
                <div><span><xsl:call-template name="language-text"><xsl:with-param name="text">observation.faintest.star</xsl:with-param></xsl:call-template></span><span><xsl:value-of select="faintestStar"/> mag</span></div>
            </xsl:if>
            <xsl:if test="count(seeing) = 1">
                <div><span> <xsl:call-template name="language-text"><xsl:with-param name="text">observation.seeing</xsl:with-param></xsl:call-template>S</span><span><xsl:value-of select="seeing"/>
                        <xsl:choose>
                            <xsl:when test="seeing = 1"> <xsl:call-template name="language-text"><xsl:with-param name="text">observation.seeing.very.good</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="seeing = 2"> <xsl:call-template name="language-text"><xsl:with-param name="text">observation.seeing.good</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="seeing = 3"> <xsl:call-template name="language-text"><xsl:with-param name="text">observation.seeing.fair</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="seeing = 4"> <xsl:call-template name="language-text"><xsl:with-param name="text">observation.seeing.bad</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="seeing = 5"> <xsl:call-template name="language-text"><xsl:with-param name="text">observation.seeing.very.bad</xsl:with-param></xsl:call-template></xsl:when>
                        </xsl:choose>
                    </span>
                </div>
            </xsl:if>
            <xsl:if test="count(imager) = 1">
                <div>
                    <span>
                        <xsl:call-template name="language-text"><xsl:with-param name="text">observation.camera</xsl:with-param></xsl:call-template>
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
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">observation.visual.impresion</xsl:with-param></xsl:call-template></span>
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
            <h4>
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
            </h4>
        </div>
        
        <span class="objectype">
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
            <xsl:if test="count(constellation)>0"> in <xsl:value-of select="constellation"/></xsl:if>
        </span>
        
        <xsl:if test="count(alias)>0">
            <div class="targetAlias">
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">target.alias</xsl:with-param></xsl:call-template> </span>
                <ul>
                    <xsl:for-each select="alias">
                        <li><xsl:value-of select="."/></li>
                    </xsl:for-each>
                </ul>
            </div>
        </xsl:if>
        
        
        <div class="position">
            <xsl:if test="boolean(position/ra)">
                <div>
                    <span>##position.ra#</span>
                    <span>
                        <xsl:call-template name="formatHHMM">
                            <xsl:with-param name="node" select="position/ra"/>
                        </xsl:call-template>
                    </span>
                </div>
            </xsl:if>
            
            
            <xsl:if test="boolean(position/dec)">
                <div>
                    <span><xsl:call-template name="language-text"><xsl:with-param name="text">position.dec</xsl:with-param></xsl:call-template></span>
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
                        <span><xsl:call-template name="language-text"><xsl:with-param name="text">target.ds.size</xsl:with-param></xsl:call-template></span>
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
                        <span><xsl:call-template name="language-text"><xsl:with-param name="text">target.ds.magnitude</xsl:with-param></xsl:call-template></span>
                        <span>
                            <xsl:value-of select="visMag"/> <xsl:call-template name="language-text"><xsl:with-param name="text">target.ds.magnitude.mag</xsl:with-param></xsl:call-template></span>
                    </div>
                </xsl:if>
                
                
                <xsl:if test="boolean(surfBr)">
                    <div class="brightness">
                        <span><xsl:call-template name="language-text"><xsl:with-param name="text">target.ds.brightness</xsl:with-param></xsl:call-template></span>
                        <span>
                            <xsl:value-of select="surfBr"/> <xsl:call-template name="language-text"><xsl:with-param name="text">target.ds.brightness.surfbr</xsl:with-param></xsl:call-template></span>
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
            
            
            <!-- ############################################################### -->
            <!-- TODO: Other subclasses like planets                             -->
            <!-- ############################################################### -->
            
            
            <xsl:if test="boolean(observer)">
                <div>
                    <span><xsl:call-template name="language-text"><xsl:with-param name="text">target.ds.origin.observer</xsl:with-param></xsl:call-template></span>
                    <span>
                        <xsl:value-of select="key('observerKey', observer)/surname"/>,
                        <xsl:text/>
                        <xsl:value-of select="key('observerKey', observer)/name"/>
                    </span>
                </div>
            </xsl:if>
            
            
            <xsl:if test="boolean(datasource)">
                <div>
                    <span> <xsl:call-template name="language-text"><xsl:with-param name="text">target.ds.origin.source</xsl:with-param></xsl:call-template></span>
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
            <xsl:value-of select="name"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="surname"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        <!-- 
             <xsl:if test="count(contact) > 0"> <xsl:call-template name="language-text"><xsl:with-param name="text">observer.contact</xsl:with-param></xsl:call-template><br/>
             <ul>
             <xsl:for-each select="contact">
             <li>
             <xsl:value-of select="."/>
             </li>
             </xsl:for-each>
             </ul>
             </xsl:if>
        -->
    </xsl:template>
    
    
    <xsl:template match="site">
        <div>
            <xsl:text disable-output-escaping="yes">&lt;a name="site</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b><xsl:call-template name="language-text"><xsl:with-param name="text">site.site</xsl:with-param></xsl:call-template></b>
            <xsl:value-of select="name"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        
        
        <div class="site">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">site.longitude</xsl:with-param></xsl:call-template></span>
                <span>
                    <xsl:call-template name="angle">
                        <xsl:with-param name="angle" select="longitude"/>
                    </xsl:call-template>
                </span>
            </div>
            
            
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">site.latitude</xsl:with-param></xsl:call-template></span>
                <span>
                    <xsl:call-template name="angle">
                        <xsl:with-param name="angle" select="latitude"/>
                    </xsl:call-template>
                </span>
            </div>
            
            
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">site.timezone</xsl:with-param></xsl:call-template></span>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">site.ut</xsl:with-param></xsl:call-template><xsl:if test="timezone >= 0">+</xsl:if>
                    <xsl:value-of select="timezone"/> min</span>
            </div>
        </div>
        
    </xsl:template>
    
    
    <xsl:template match="scope">
        <div>
            <xsl:text disable-output-escaping="yes">&lt;a name="scope</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b><xsl:call-template name="language-text"><xsl:with-param name="text">scope.optics</xsl:with-param></xsl:call-template> </b>
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        
        
        
        <xsl:if test="count(type)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">scope.type</xsl:with-param></xsl:call-template></span>
                <span>
                    <xsl:value-of select="type"/>
                </span>
            </div>
        </xsl:if>
        
        
        <xsl:if test="count(vendor)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">scope.vendor</xsl:with-param></xsl:call-template> </span>
                <span>
                    <xsl:value-of select="vendor"/>
                </span>
            </div>
        </xsl:if>
        
        
        <div>
            <span><xsl:call-template name="language-text"><xsl:with-param name="text">scope.aperture</xsl:with-param></xsl:call-template> </span>
            <span>
                <xsl:value-of select="aperture"/> mm</span>
        </div>
        
        
        <xsl:if test="count(focalLength)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">scope.focal.length</xsl:with-param></xsl:call-template></span>
                <span>
                    <xsl:value-of select="focalLength"/> <xsl:call-template name="language-text"><xsl:with-param name="text">scope.focal.length.unit</xsl:with-param></xsl:call-template> </span>
            </div>
        </xsl:if>
        
        
        <xsl:if test="count(magnification)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">scope.magnification</xsl:with-param></xsl:call-template></span>
                <span>
                    <xsl:value-of select="magnification"/> &#215;</span>
            </div>
            
            
        </xsl:if>
        
        
        <xsl:if test="count(trueField)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">scope.true.field.view</xsl:with-param></xsl:call-template> </span>
                <span>
                    <xsl:call-template name="angle">
                        <xsl:with-param name="angle" select="trueField"/>
                    </xsl:call-template>
                </span>
            </div>
        </xsl:if>
        
        
        <xsl:if test="count(lightGrasp)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">scope.light.grasp</xsl:with-param></xsl:call-template> </span>
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
                
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">lens.eyepiece.zoom</xsl:with-param></xsl:call-template> </span>
                
            </xsl:if>
            
            <xsl:if test="count(maxFocalLength)=0">
                
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">lens.eyepiece</xsl:with-param></xsl:call-template>  </span>
                
            </xsl:if>
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        
        <xsl:if test="count(vendor)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">lens.eyepice.vendor</xsl:with-param></xsl:call-template> </span>
                <span>
                    <xsl:value-of select="vendor"/>
                </span>
            </div>
        </xsl:if>
        <div>
            <span><xsl:call-template name="language-text"><xsl:with-param name="text">lens.eyepice.focal.length</xsl:with-param></xsl:call-template> </span>
            <span>
                
                <xsl:value-of select="focalLength"/>
                
                <xsl:if test="count(maxFocalLength)>0">-<xsl:value-of select="maxFocalLength"/></xsl:if> <xsl:call-template name="language-text"><xsl:with-param name="text">lens.eyepiece.focal.length.unit</xsl:with-param></xsl:call-template> 
                
            </span>
        </div>
        <xsl:if test="count(apparentFOV)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">lens.eyepiece.true.field.view</xsl:with-param></xsl:call-template> </span>
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
            
            <span><xsl:call-template name="language-text"><xsl:with-param name="text">lens.lens</xsl:with-param></xsl:call-template>  </span>
            
            <xsl:value-of select="model"/>
            
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
            
        </div>
        
        
        <xsl:if test="count(vendor)>0">
            
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">lens.vendor</xsl:with-param></xsl:call-template> </span>
                <span><xsl:value-of select="vendor"/></span>
            </div>
            
        </xsl:if>
        
        <div>
            <span><xsl:call-template name="language-text"><xsl:with-param name="text">lens.focal.length.factor</xsl:with-param></xsl:call-template> </span>
            <span><xsl:value-of select="factor"/> <xsl:call-template name="language-text"><xsl:with-param name="text">lens.focal.length.unit</xsl:with-param></xsl:call-template></span>
        </div>
    </xsl:template>
    <xsl:template match="imager">
        
        <div class="imager">
            <xsl:text disable-output-escaping="yes">&lt;a name="imager</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            
            <xsl:if test="count(pixelsX)>0">
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">imager.camera.ccd</xsl:with-param></xsl:call-template></span>
            </xsl:if>
            
            <xsl:if test="count(pixelsX)=0">
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">imager.camera</xsl:with-param></xsl:call-template> </span>
            </xsl:if>
            
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        <xsl:if test="count(vendor)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">imager.vendor</xsl:with-param></xsl:call-template></span>
                <span><xsl:value-of select="vendor"/></span>
            </div>
        </xsl:if>
        
        <xsl:if test="count(pixelsX)>0">
            <div>name=""
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">imager.pixel</xsl:with-param></xsl:call-template></span>
                <span><xsl:value-of select="pixelsX"/>x<xsl:value-of select="pixelsY"/></span>
            </div>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="filter">
        <div>
            <xsl:text disable-output-escaping="yes">&lt;a name="filter</xsl:text>
            <xsl:value-of select="@id"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <b> <xsl:call-template name="language-text"><xsl:with-param name="text">filter.filter</xsl:with-param></xsl:call-template></b>
            <xsl:value-of select="model"/>
            <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
        </div>
        
        
        
        <xsl:if test="count(type)>0">
            <div>
                <span><xsl:call-template name="language-text"><xsl:with-param name="text">filter.type</xsl:with-param></xsl:call-template></span>
                <span>
                    <xsl:choose>
                        <xsl:when test="type='other'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.other</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:when test="type='broad band'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.broadband</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:when test="type='narrow band'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.narrowband</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:when test="type='O-III'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.OIII</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:when test="type='Solar'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.solar</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:when test="type='H-beta'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.h-beta</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:when test="type='H-alpha'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.h-alpha</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:when test="type='color'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.color</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:when test="type='neutral'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.neutral</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:when test="type='corrective'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.corrective</xsl:with-param></xsl:call-template></xsl:when>
                        <xsl:otherwise><xsl:call-template name="language-text"><xsl:with-param name="text">filter.unknown</xsl:with-param></xsl:call-template></xsl:otherwise>
                    </xsl:choose>
                </span>
            </div>
            <xsl:if test="count(color)>0">
                <div>
                    <span><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe</xsl:with-param></xsl:call-template></span>
                    <span>
                        <xsl:choose>
                            <xsl:when test="color='light red'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.lightred</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='red'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.red</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='deep red'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.deepred</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='orange'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.orange</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='light yellow'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.lightyellow</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='deep yellow'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.deepyellow</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='yellow'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.yellow</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='yellow-green'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.yellow-green</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='light green'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.lightgreen</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='green'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.green</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='medium blue'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.mediumble</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='pale blue'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.pableblue</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='blue'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.blue</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='deep blue'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.deepblue</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:when test="color='violet'"><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.violet</xsl:with-param></xsl:call-template></xsl:when>
                            <xsl:otherwise><xsl:call-template name="language-text"><xsl:with-param name="text">filter.farbe.unknown</xsl:with-param></xsl:call-template></xsl:otherwise>
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
                <li><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating</xsl:with-param></xsl:call-template>
                    <xsl:choose>
                        <xsl:when test="contains(./@type,'findingsDeepSkyOCType')">
                            <!-- open starcluster -->
                            <xsl:choose>
                                <xsl:when test="rating = '1'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.oc.type.1</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '2'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.oc.type.2</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '3'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.oc.type.3</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '4'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.oc.type.4</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '5'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.oc.type.5</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '6'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.oc.type.6</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '7'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.oc.type.7</xsl:with-param></xsl:call-template></xsl:when>
                            </xsl:choose>
                        </xsl:when>
                        
                        
                        <xsl:when test="contains(./@type,'findingsDeepSkyDSType')">
                            <!-- Double stars -->
                            <xsl:choose>
                                <xsl:when test="rating = '1'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.ds.type.1</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '2'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.ds.type.2</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '3'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.deep.sky.ds.type.3</xsl:with-param></xsl:call-template></xsl:when>
                            </xsl:choose>
                        </xsl:when>
                        
                        
                        <xsl:otherwise>
                            <!-- other objecttype -->
                            <xsl:choose>
                                <xsl:when test="rating = '1'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.generic.type.1</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '2'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.generic.type.2</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '3'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.generic.type.3</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '4'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.generic.type.4</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '5'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.generic.type.5</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '6'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.generic.type.6</xsl:with-param></xsl:call-template></xsl:when>
                                <xsl:when test="rating = '7'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.rating.generic.type.7</xsl:with-param></xsl:call-template></xsl:when>
                            </xsl:choose>
                        </xsl:otherwise>
                    </xsl:choose>
                </li>
                
                
                <xsl:if test="./@stellar='true'">
                    <li><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.stellar</xsl:with-param></xsl:call-template><br/>
                    </li>
                </xsl:if>
                
                
                <xsl:if test="./@resolved='true'">
                    <li><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.resolved</xsl:with-param></xsl:call-template><br/>
                    </li>
                </xsl:if>
                
                
                <xsl:if test="./@mottled='true'">
                    <li> <xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.mottled</xsl:with-param></xsl:call-template><br/>
                    </li>
                </xsl:if>
                
                
                
                <xsl:if test="count(smallDiameter)>0 and count(largeDiameter)>0">
                    <li><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.apparent.size</xsl:with-param></xsl:call-template> <xsl:call-template name="angle">
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
                        <xsl:if test="./visMag/@fainterThan='true'"><xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.variable.visual.magnitude.fainter.than</xsl:with-param></xsl:call-template> </xsl:if>
                        <xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.variable.visual.magnitude</xsl:with-param></xsl:call-template> <xsl:value-of select="visMag"/>
                        <xsl:if test="./visMag/@uncertain='true'"> <xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.variable.visual.magnitude.fainter.uncertain</xsl:with-param></xsl:call-template></xsl:if>
                        <br/>
                    </li>
                </xsl:if>
                <xsl:if test="string-length(chartID)>0">
                    <li>
                        <xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.variable.aavso.chart</xsl:with-param></xsl:call-template> <xsl:value-of select="chartID"/>
                        <xsl:if test="./chartID/@nonAAVSOchart='true'"> <xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.variable.aavso.no.chart</xsl:with-param></xsl:call-template></xsl:if>
                        <br/>
                    </li>
                </xsl:if>
                <xsl:if test="count(comparisonStar) > 0"><li> <xsl:call-template name="language-text"><xsl:with-param name="text">result.visual.deep.sky.variable.comparison.stars</xsl:with-param></xsl:call-template><br/>
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
        <div class="image">  
            <xsl:param name="imgTag" select="concat('img alt=&quot;-IMG-&quot; src=&quot;', $imgFile, '&quot;')"/>                 
            <span><xsl:text disable-output-escaping="yes">&lt;</xsl:text><xsl:value-of select="$imgTag"/><xsl:text disable-output-escaping="yes">&gt;</xsl:text></span>
        </div>
    
    </xsl:template>
        
    <xsl:template name="language-text">
        <xsl:param name="text"></xsl:param>
        <xsl:choose>
            <xsl:when
                test="$LANGUAGE_TEXTS_CURRENT/text[@ident = $text]">
                <xsl:value-of
                    select="$LANGUAGE_TEXTS_CURRENT/text[@ident = $text]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>    
        </xsl:choose>
    </xsl:template>
    <!--    
         <xsl:function name="om:text" as="xs:string" >
         <xsl:param name="input"  as="xs:string"/>
         <xsl:choose>
         <xsl:when
         test="$LANGUAGE_TEXTS_CURRENT/text[@ident = $input]">
         <xsl:value-of
         select="$LANGUAGE_TEXTS_CURRENT/text[@ident = $input]"/>
         </xsl:when>
         <xsl:otherwise>
         <xsl:value-of select="$input"/>
         </xsl:otherwise>    
         </xsl:choose>
         </xsl:function>
         
    --> 
    <xsl:output method="html"/>
</xsl:stylesheet>

