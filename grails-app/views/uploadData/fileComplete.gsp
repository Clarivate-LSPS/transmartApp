<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="shortctu icon" href="${resource(dir: 'images', file: 'searchtool.ico')}">
    <link rel="icon" href="${resource(dir: 'images', file: 'searchtool.ico')}">
    <link rel="stylesheet" href="${resource(dir: 'js', file: 'ext/resources/css/ext-all.css')}"></link>
    <link rel="stylesheet" href="${resource(dir: 'js', file: 'ext/resources/css/xtheme-gray.css')}"></link>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"></link>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'uploadData.css')}"></link>

    <!--[if IE 7]>
    <style type="text/css">
         div#gfilterresult,div#ptfilterresult, div#jubfilterresult, div#dqfilterresult {
            width: 99%;
        }
    </style>
<![endif]-->

    <title>${grailsApplication.config.com.recomdata.dataUpload.appTitle}</title>

    <script type="text/javascript"
            src="${resource(dir: 'plugins/prototype-1.0/js/prototype', file: 'prototype.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'ext/adapter/ext/ext-base.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/jQuery', file: 'jquery.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/jQuery', file: 'jquery-ui-1.9.1.custom.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/jQuery', file: 'jquery.tablesorter.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.cookie.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.dynatree.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.paging.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.loadmask.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.ajaxmanager.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.numeric.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.colorbox-min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.simplemodal.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.dataTables.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'facetedSearch/facetedSearchBrowse.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/ui.multiselect.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/jquery.validate.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jQuery/additional-methods.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'ajax_queue.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'ext/ext-all.js')}"></script>

    <r:layoutResources/>
</head>

<body>

<script type="text/javascript">
    var sessionSearch = "${rwgSearchFilter}";
    var sessionOperators = "${rwgSearchOperators}";
    var sessionSearchCategory = "${rwgSearchCategory}";
    var searchPage = "datasetExplorer";
    var $j = jQuery.noConflict();
    Ext.BLANK_IMAGE_URL = "${resource(dir:'js', file:'ext/resources/images/default/s.gif')}";
    var dseOpenedNodes = "${dseOpenedNodes}";
    var dseClosedNodes = "${dseClosedNodes}";

    //set ajax to 600*1000 milliseconds
    Ext.Ajax.timeout = 1800000;

    // this overrides the above
    Ext.Updater.defaults.timeout = 1800000;

    var helpURL = '${grailsApplication.config.com.recomdata.adminHelpURL}';
</script>

<div id="header-div">
    <g:render template="/layouts/commonheader" model="['app': 'uploaddata', 'utilitiesMenu': 'true']"/>
</div>
<br/><br/>

<div class="uploadwindow">

    <div>The file was uploaded successfully and has been added to the study.
    </div>
    <br/>
    <a href="${createLink([action: 'index', controller: 'uploadData'])}">Upload another file</a>
    <br/><br/>
    <a href="${createLink([action: 'index', controller: 'RWG'])}">Return to the search page</a>
</div>

</div>
<r:layoutResources/><%-- XXX: Use template --%>
</body>
</html>