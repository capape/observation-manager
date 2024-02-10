<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"                
                xmlns:external="http://ExternalFunction.xalan-c++.xml.apache.org" exclude-result-prefixes="external">
   
    
    <xsl:param name="LANGUAGE_TEXTS_CURRENT" select="document('./internal_texts_pt_br.xml')/texts"/>
    <xsl:include href="transform_to_include.xsl"/>
    
</xsl:stylesheet>

