<?xml version='1.0'?>

<!-- $FreeBSD: doc/share/xsl/freebsd-html-chunk.xsl,v 1.1 2003/01/03 05:06:14 trhodes Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'
                xmlns="http://www.w3.org/TR/xhtml1/transitional"
                exclude-result-prefixes="#default">

  <!-- Pull in the base stylesheets -->
  <xsl:import href="/usr/local/share/xsl/docbook/html/chunk.xsl"/>

  <!-- Redefine variables, and replace templates as necessary here -->

  <xsl:param name="toc.section.depth">3</xsl:param>
  <xsl:param name="chunk.first.sections" select="1"></xsl:param>
  <xsl:param name="chunk.section.depth" select="2"></xsl:param>
  <xsl:param name="generate.index" select="1"></xsl:param>

  <!-- HTML specific customisation goes here -->

  <xsl:param name="html.stylesheet" select="'docbook.css'"/>

  <xsl:include href="freebsd-common.xsl"/>
</xsl:stylesheet>

