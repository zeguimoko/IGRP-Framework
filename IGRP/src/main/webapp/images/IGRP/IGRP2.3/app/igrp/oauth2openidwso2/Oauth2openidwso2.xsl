<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:output method="html" omit-xml-declaration="yes" encoding="utf-8" indent="yes" doctype-system="about:legacy-compat"/><xsl:template match="/"><html><head><xsl:call-template name="IGRP-head"/><link rel="stylesheet" type="text/css" href="{$path}/core/igrp/toolsbar/toolsbar.css?v={$version}"/><style/></head><body class="{$bodyClass} sidebar-off"><xsl:call-template name="IGRP-topmenu"/><form method="POST" class="IGRP-form" name="formular_default" enctype="multipart/form-data"><div class="container-fluid"><div class="row"><xsl:call-template name="IGRP-sidebar"/><div class="col-sm-9 col-md-10 col-md-offset-2 col-sm-offset-3 main" id="igrp-contents"><div class="content"><div class="row row-msg"><div class="gen-column col-md-12"><div class="gen-inner"><xsl:apply-templates mode="igrp-messages" select="rows/content/messages"/></div></div></div><div class="row " id="row-b69dc4ef"><div class="gen-column col-sm-3"><div class="gen-inner"/></div><div class="gen-column col-sm-6"><div class="gen-inner"><xsl:if test="rows/content/box_1"><div class="box igrp-box-holder gen-container-item " gen-class="" item-name="box_1"><div class="box-body" gen-preserve-content="true"><xsl:apply-templates mode="form-hidden-fields" select="rows/content/box_1/fields"/><div><div class="row " id="row-3c29c6c6"><div class="gen-column col-sm-12"><div class="gen-inner"><xsl:if test="rows/content/sectionheader_1"><section class="content-header gen-container-item " gen-class="" item-name="sectionheader_1"><h2 class="disable-output-escaping"><xsl:value-of disable-output-escaping="yes" select="rows/content/sectionheader_1/fields/sectionheader_1_text/value"/></h2></section></xsl:if><xsl:if test="rows/content/toolsbar_1"><div class="toolsbar-holder default gen-container-item " gen-structure="toolsbar" gen-fields=".btns-holder&gt;a.btn" gen-class="" item-name="toolsbar_1"><div class="btns-holder btn-group-lg btn-group-justified" role="group"><xsl:apply-templates select="rows/content/toolsbar_1" mode="gen-buttons"><xsl:with-param name="vertical" select="'true'"/><xsl:with-param name="outline" select="'false'"/></xsl:apply-templates></div></div></xsl:if></div></div></div></div></div></div></xsl:if></div></div><div class="gen-column col-sm-3"><div class="gen-inner"/></div></div></div></div></div></div><xsl:call-template name="IGRP-bottom"/></form></body></html></xsl:template><xsl:include href="../../../xsl/tmpl/IGRP-functions.tmpl.xsl?v=30"/><xsl:include href="../../../xsl/tmpl/IGRP-variables.tmpl.xsl?v=30"/><xsl:include href="../../../xsl/tmpl/IGRP-home-include.tmpl.xsl?v=30"/><xsl:include href="../../../xsl/tmpl/IGRP-utils.tmpl.xsl?v=30"/></xsl:stylesheet>
