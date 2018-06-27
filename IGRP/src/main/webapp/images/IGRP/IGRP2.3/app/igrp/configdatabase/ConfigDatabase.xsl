<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:output method="html" omit-xml-declaration="yes" encoding="utf-8" indent="yes" doctype-system="about:legacy-compat"/><xsl:template match="/"><html><head><xsl:call-template name="IGRP-head"/><link rel="stylesheet" type="text/css" href="{$path}/core/igrp/toolsbar/toolsbar.css?v={$version}"/><link rel="stylesheet" type="text/css" href="{$path}/core/igrp/table/datatable/dataTables.bootstrap.css?v={$version}"/><link rel="stylesheet" type="text/css" href="{$path}/core/igrp/table/igrp.tables.css?v={$version}"/><link rel="stylesheet" type="text/css" href="{$path}/plugins/select2/select2.min.css?v={$version}"/><link rel="stylesheet" type="text/css" href="{$path}/plugins/select2/select2.style.css?v={$version}"/><style/></head><body class="{$bodyClass} sidebar-off"><xsl:call-template name="IGRP-topmenu"/><form method="POST" class="IGRP-form" name="formular_default" enctype="multipart/form-data"><div class="container-fluid"><div class="row"><xsl:call-template name="IGRP-sidebar"/><div class="col-sm-9 col-md-10 col-md-offset-2 col-sm-offset-3 main" id="igrp-contents"><div class="content"><div class="row row-msg"><div class="gen-column col-md-12"><div class="gen-inner"><xsl:apply-templates mode="igrp-messages" select="rows/content/messages"/></div></div></div><div class="row " id="row-f5de86f6"><div class="gen-column col-sm-9"><div class="gen-inner"><xsl:if test="rows/content/sectionheader_1"><section class="content-header gen-container-item " gen-class="" item-name="sectionheader_1"><h2><xsl:value-of select="rows/content/sectionheader_1/fields/sectionheader_1_text/value"/></h2></section></xsl:if></div></div><div class="gen-column col-sm-3"><div class="gen-inner"/></div></div><div class="row " id="row-ef34665a"><div class="gen-column col-sm-9"><div class="gen-inner"><xsl:if test="rows/content/form_1"><div class="box igrp-forms gen-container-item " gen-class="" item-name="form_1"><div class="box-body"><div role="form"><xsl:apply-templates mode="form-hidden-fields" select="rows/content/form_1/fields"/><xsl:if test="rows/content/form_1/fields/aplicacao"><div class="col-sm-4 form-group  gen-fields-holder" item-name="aplicacao" item-type="select" required="required"><label for="{rows/content/form_1/fields/aplicacao/@name}"><xsl:value-of select="rows/content/form_1/fields/aplicacao/label"/></label><select class="form-control select2 " id="form_1_aplicacao" name="{rows/content/form_1/fields/aplicacao/@name}" required="required"><xsl:call-template name="setAttributes"><xsl:with-param name="field" select="rows/content/form_1/fields/aplicacao"/></xsl:call-template><xsl:for-each select="rows/content/form_1/fields/aplicacao/list/option"><option value="{value}" label="{text}"><xsl:if test="@selected='true'"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if><span><xsl:value-of select="text"/></span></option></xsl:for-each></select></div></xsl:if><xsl:if test="rows/content/form_1/fields/tipo_base_dados"><div class="col-sm-4 form-group  gen-fields-holder" item-name="tipo_base_dados" item-type="select" required="required"><label for="{rows/content/form_1/fields/tipo_base_dados/@name}"><xsl:value-of select="rows/content/form_1/fields/tipo_base_dados/label"/></label><select class="form-control select2 IGRP_change" id="form_1_tipo_base_dados" name="{rows/content/form_1/fields/tipo_base_dados/@name}" required="required"><xsl:call-template name="setAttributes"><xsl:with-param name="field" select="rows/content/form_1/fields/tipo_base_dados"/></xsl:call-template><xsl:for-each select="rows/content/form_1/fields/tipo_base_dados/list/option"><option value="{value}" label="{text}"><xsl:if test="@selected='true'"><xsl:attribute name="selected">selected</xsl:attribute></xsl:if><span><xsl:value-of select="text"/></span></option></xsl:for-each></select></div></xsl:if><xsl:if test="rows/content/form_1/fields/nome_de_conexao"><div class="form-group col-sm-4   gen-fields-holder" item-name="nome_de_conexao" item-type="text" required="required"><label for="{rows/content/form_1/fields/nome_de_conexao/@name}"><span><xsl:value-of select="rows/content/form_1/fields/nome_de_conexao/label"/></span></label><input type="text" value="{rows/content/form_1/fields/nome_de_conexao/value}" class="form-control " id="{rows/content/form_1/fields/nome_de_conexao/@name}" name="{rows/content/form_1/fields/nome_de_conexao/@name}" required="required" maxlength="30" placeholder="A name ex: myConn, DEVNOSI"><xsl:call-template name="setAttributes"><xsl:with-param name="field" select="rows/content/form_1/fields/nome_de_conexao"/></xsl:call-template></input></div></xsl:if><xsl:if test="rows/content/form_1/fields/config"><div class="box-head subtitle gen-fields-holder" text-color="1"><span><xsl:value-of select="rows/content/form_1/fields/config/label"/></span></div></xsl:if><xsl:if test="rows/content/form_1/fields/hostname"><div class="form-group col-sm-4   gen-fields-holder" item-name="hostname" item-type="text" required="required"><label for="{rows/content/form_1/fields/hostname/@name}"><span><xsl:value-of select="rows/content/form_1/fields/hostname/label"/></span></label><input type="text" value="{rows/content/form_1/fields/hostname/value}" class="form-control " id="{rows/content/form_1/fields/hostname/@name}" name="{rows/content/form_1/fields/hostname/@name}" required="required" maxlength="100" placeholder="hostname ex: localhost, IP, nosidev02.gov.cv"><xsl:call-template name="setAttributes"><xsl:with-param name="field" select="rows/content/form_1/fields/hostname"/></xsl:call-template></input></div></xsl:if><xsl:if test="rows/content/form_1/fields/port"><div class="form-group col-sm-4   gen-fields-holder" item-name="port" item-type="number" required="required"><label for="{rows/content/form_1/fields/port/@name}"><span><xsl:value-of select="rows/content/form_1/fields/port/label"/></span></label><input type="number" value="{rows/content/form_1/fields/port/value}" class="form-control " id="{rows/content/form_1/fields/port/@name}" name="{rows/content/form_1/fields/port/@name}" required="required" min="" max="" maxlength="30" placeholder=""><xsl:call-template name="setAttributes"><xsl:with-param name="field" select="rows/content/form_1/fields/port"/></xsl:call-template></input></div></xsl:if><xsl:if test="rows/content/form_1/fields/nome_de_bade_dados"><div class="form-group col-sm-4   gen-fields-holder" item-name="nome_de_bade_dados" item-type="text" required="required"><label for="{rows/content/form_1/fields/nome_de_bade_dados/@name}"><span><xsl:value-of select="rows/content/form_1/fields/nome_de_bade_dados/label"/></span></label><input type="text" value="{rows/content/form_1/fields/nome_de_bade_dados/value}" class="form-control " id="{rows/content/form_1/fields/nome_de_bade_dados/@name}" name="{rows/content/form_1/fields/nome_de_bade_dados/@name}" required="required" maxlength="50" placeholder="db name ex: db, devnosi.gov.cv"><xsl:call-template name="setAttributes"><xsl:with-param name="field" select="rows/content/form_1/fields/nome_de_bade_dados"/></xsl:call-template></input></div></xsl:if><xsl:if test="rows/content/form_1/fields/credenciais"><div class="box-head subtitle gen-fields-holder" text-color="1"><span><xsl:value-of select="rows/content/form_1/fields/credenciais/label"/></span></div></xsl:if><xsl:if test="rows/content/form_1/fields/username"><div class="form-group col-sm-4   gen-fields-holder" item-name="username" item-type="text" required="required"><label for="{rows/content/form_1/fields/username/@name}"><span><xsl:value-of select="rows/content/form_1/fields/username/label"/></span></label><input type="text" value="{rows/content/form_1/fields/username/value}" class="form-control " id="{rows/content/form_1/fields/username/@name}" name="{rows/content/form_1/fields/username/@name}" required="required" maxlength="80" placeholder=""><xsl:call-template name="setAttributes"><xsl:with-param name="field" select="rows/content/form_1/fields/username"/></xsl:call-template></input></div></xsl:if><xsl:if test="rows/content/form_1/fields/password"><div class="form-group col-sm-4   gen-fields-holder" item-name="password" item-type="password"><label for="{rows/content/form_1/fields/password/@name}"><span><xsl:value-of select="rows/content/form_1/fields/password/label"/></span></label><input type="password" value="{rows/content/form_1/fields/password/value}" class="form-control " id="{rows/content/form_1/fields/password/@name}" name="{rows/content/form_1/fields/password/@name}" maxlength="80" placeholder=""><xsl:call-template name="setAttributes"><xsl:with-param name="field" select="rows/content/form_1/fields/password"/></xsl:call-template></input></div></xsl:if></div></div><xsl:apply-templates select="rows/content/form_1/tools-bar" mode="form-buttons"/></div></xsl:if></div></div><div class="gen-column col-sm-3"><div class="gen-inner"><xsl:if test="rows/content/toolsbar_1"><div class="toolsbar-holder default gen-container-item " gen-structure="toolsbar" gen-fields=".btns-holder&gt;a.btn" gen-class="" item-name="toolsbar_1"><div class="btns-holder  btn-group-justified" role="group"><xsl:apply-templates select="rows/content/toolsbar_1" mode="gen-buttons"><xsl:with-param name="vertical" select="'true'"/><xsl:with-param name="outline" select="'false'"/></xsl:apply-templates></div></div></xsl:if><xsl:if test="rows/content/paragraph_1"><div class="box gen-container-item " gen-class="" item-name="paragraph_1"><xsl:call-template name="box-header"><xsl:with-param name="title" select="rows/content/paragraph_1/@title"/><xsl:with-param name="collapsible" select="'true'"/><xsl:with-param name="collapsed" select="'true'"/></xsl:call-template><div class="box-body"><p><xsl:value-of select="rows/content/paragraph_1/fields/paragraph_1_text/value"/></p></div></div></xsl:if></div></div></div><div class="row " id="row-0605f496"><div class="gen-column col-md-12"><div class="gen-inner"><xsl:if test="rows/content/table_1"><div class="box box-table-contents gen-container-item " gen-class="" item-name="table_1"><div class="box-body "><xsl:apply-templates mode="form-hidden-fields" select="rows/content/table_1/fields"/><div class="table-contents-head"><div class="table-contents-inner"></div></div><div class="table-box"><div class="table-box-inner"><table id="table_1" class="table table-striped  IGRP_contextmenu " exports=""><thead><tr><xsl:if test="rows/content/table_1/fields/nome_de_conexao_tabela"><th align="left" class=" gen-fields-holder"><span><xsl:value-of select="rows/content/table_1/fields/nome_de_conexao_tabela/label"/></span></th></xsl:if><xsl:if test="rows/content/table_1/fields/hostname_tabela"><th align="left" class=" gen-fields-holder"><span><xsl:value-of select="rows/content/table_1/fields/hostname_tabela/label"/></span></th></xsl:if><xsl:if test="rows/content/table_1/fields/porta_tabela"><th align="left" class=" gen-fields-holder"><span><xsl:value-of select="rows/content/table_1/fields/porta_tabela/label"/></span></th></xsl:if><xsl:if test="rows/content/table_1/fields/nome_base_de_dados_tabela"><th align="left" class=" gen-fields-holder"><span><xsl:value-of select="rows/content/table_1/fields/nome_base_de_dados_tabela/label"/></span></th></xsl:if><xsl:if test="rows/content/table_1/fields/user_name_tabela"><th align="left" class=" gen-fields-holder"><span><xsl:value-of select="rows/content/table_1/fields/user_name_tabela/label"/></span></th></xsl:if><xsl:if test="rows/content/table_1/fields/tipo_de_base_de_dados_tabela"><th align="left" class=" gen-fields-holder"><span><xsl:value-of select="rows/content/table_1/fields/tipo_de_base_de_dados_tabela/label"/></span></th></xsl:if><xsl:if test="rows/content/table_1/table/context-menu/item"><th class="igrp-table-ctx-th"/></xsl:if></tr></thead><tbody><xsl:for-each select="rows/content/table_1/table/value/row[not(@total='yes')]"><tr><xsl:apply-templates mode="context-param" select="context-menu"/><input type="hidden" name="p_id_fk" value="{id}"/><input type="hidden" name="p_id_fk_desc" value="{id_desc}"/><xsl:if test="nome_de_conexao_tabela"><td align="left" data-order="{nome_de_conexao_tabela}" data-row="{position()}" data-title="{../../../fields/nome_de_conexao_tabela/label}" class="text" item-name="nome_de_conexao_tabela"><span class=""><xsl:value-of select="nome_de_conexao_tabela"/></span></td></xsl:if><xsl:if test="hostname_tabela"><td align="left" data-order="{hostname_tabela}" data-row="{position()}" data-title="{../../../fields/hostname_tabela/label}" class="text" item-name="hostname_tabela"><span class=""><xsl:value-of select="hostname_tabela"/></span></td></xsl:if><xsl:if test="porta_tabela"><td align="left" data-order="{porta_tabela}" data-row="{position()}" data-title="{../../../fields/porta_tabela/label}" class="number" item-name="porta_tabela"><span class=""><xsl:value-of select="porta_tabela"/></span></td></xsl:if><xsl:if test="nome_base_de_dados_tabela"><td align="left" data-order="{nome_base_de_dados_tabela}" data-row="{position()}" data-title="{../../../fields/nome_base_de_dados_tabela/label}" class="text" item-name="nome_base_de_dados_tabela"><span class=""><xsl:value-of select="nome_base_de_dados_tabela"/></span></td></xsl:if><xsl:if test="user_name_tabela"><td align="left" data-order="{user_name_tabela}" data-row="{position()}" data-title="{../../../fields/user_name_tabela/label}" class="text" item-name="user_name_tabela"><span class=""><xsl:value-of select="user_name_tabela"/></span></td></xsl:if><xsl:if test="tipo_de_base_de_dados_tabela"><td align="left" data-order="{tipo_de_base_de_dados_tabela}" data-row="{position()}" data-title="{../../../fields/tipo_de_base_de_dados_tabela/label}" class="text" item-name="tipo_de_base_de_dados_tabela"><span class=""><xsl:value-of select="tipo_de_base_de_dados_tabela"/></span></td></xsl:if><xsl:if test="//rows/content/table_1/table/context-menu/item"><td class="igrp-table-ctx-td"><xsl:apply-templates select="../../context-menu" mode="table-context-inline"><xsl:with-param name="row-params" select="context-menu"/></xsl:apply-templates></td></xsl:if></tr></xsl:for-each></tbody></table></div></div></div></div></xsl:if></div></div></div></div></div></div></div><xsl:call-template name="IGRP-bottom"/></form><script type="text/javascript" src="{$path}/core/igrp/form/igrp.forms.js?v={$version}"/><script type="text/javascript" src="{$path}/core/igrp/table/datatable/jquery.dataTables.min.js?v={$version}"/><script type="text/javascript" src="{$path}/core/igrp/table/datatable/dataTables.bootstrap.min.js?v={$version}"/><script type="text/javascript" src="{$path}/core/igrp/table/igrp.table.js?v={$version}"/><script type="text/javascript" src="{$path}/core/igrp/table/bootstrap-contextmenu.js?v={$version}"/><script type="text/javascript" src="{$path}/core/igrp/table/table.contextmenu.js?v={$version}"/><script type="text/javascript" src="{$path}/plugins/select2/select2.full.min.js?v={$version}"/><script type="text/javascript" src="{$path}/plugins/select2/select2.init.js?v={$version}"/></body></html></xsl:template><xsl:include href="../../../xsl/tmpl/IGRP-functions.tmpl.xsl?v=1530057164108"/><xsl:include href="../../../xsl/tmpl/IGRP-variables.tmpl.xsl?v=1530057164108"/><xsl:include href="../../../xsl/tmpl/IGRP-home-include.tmpl.xsl?v=1530057164108"/><xsl:include href="../../../xsl/tmpl/IGRP-utils.tmpl.xsl?v=1530057164108"/><xsl:include href="../../../xsl/tmpl/IGRP-form-utils.tmpl.xsl?v=1530057164108"/><xsl:include href="../../../xsl/tmpl/IGRP-table-utils.tmpl.xsl?v=1530057164108"/></xsl:stylesheet>
