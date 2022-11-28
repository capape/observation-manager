<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">

<html >
<body style="font-family:Arial;font-size:12pt;background-color:#EEEEEE">


<table>
<tr style="font-weight:bold;background-color:teal;color:white;padding:4px">
<th>Target</th>
<th>Constellation</th>
<th>Position</th>
<th>Notes</th>
</tr>
<xsl:for-each select="oal:observations/targets/target"
  xmlns:oal="http://groups.google.com/group/openastronomylog">
  <tr>
  <td><xsl:value-of select="name"/></td>
  <td><xsl:value-of select="constellation"/></td>
  <td>
    <ul>
        <li><label>RA:</label><span><xsl:value-of select="position/ra"/></span></li>
        <li><label>DEC:</label><span><xsl:value-of select="position/dec"/></span></li>
    </ul>
  </td>
  <td><div><xsl:value-of select="notes"/></div></td>    
  </tr>
</xsl:for-each>
</table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
