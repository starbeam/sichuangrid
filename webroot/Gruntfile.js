// js文件的合并规则

module.exports = function(grunt) {
	var baseUrl="resource/external/";
	var sysUrl="resource/system/js/"
    grunt.initConfig({
    	jsVersion: grunt.file.readJSON('package.json'),
    	concat: {
    		options:{
                separator:'\n'
            },
    	    dist: {
    	      files: {
                  'resource/fileMerge/baseStage1.<%=jsVersion.jsVersionNo%>.js':[ 
                      'resource/external/jquery.min.js'
                      ,'resource/external/jquery.cookie.js'
                      ,'resource/external/jquery.bgiframe.js'
                      ,'resource/external/jqueryui/jquery-ui-1.9.2.custom.min.js'
                      ,'resource/external/jqueryui/jquery-ui-timepicker-addon.js'
                      ,'resource/external/jqueryui/jquery.ui.datepicker-zh-CN.js'
                      ,'resource/external/jsValidate/jquery.form.js'
                      ,'resource/external/jsValidate/jquery.metadata.js'
                      ,'resource/external/jsValidate/jquery.validate.min.js'
                      ,'resource/external/jsValidate/additional-methods.min.js'
                      ,'resource/external/jsValidate/messages_cn.js'
                      ,'resource/external/validatePassword/digitalspaghetti.password.js'
                      ,'resource/external/jqGrid/js/grid.locale-cn.js'
                      ,'resource/external/jqGrid/js/jquery.jqGrid.min.js'
                      ,'resource/external/jqGrid/plugins/grid.setcolumns.js'
                      ,'resource/external/contextmenu/jquery.contextmenu.js'
                      ,'resource/external/poshytip/jquery.poshytip.js'
                     // ,'resource/external/xheditor/xheditor-1.1.13-zh-cn.min.js'
                      ,'resource/external/selectInPlace/jquery-selectInPlace-0.1.js'
                      ,'resource/system/js/uploadFile.js' 
                  ]
                  ,'resource/fileMerge/baseStage2.<%=jsVersion.jsVersionNo%>.js':[
                      'resource/external/ext/ext-base.js'
                      ,'resource/external/ext/ext-all.js'
                      ,'resource/external/uploadify/swfobject.js'
                      ,'resource/external/uploadify/jquery.uploadify.v2.1.4.min.js'
                      ,'resource/external/reportList/sigmareport.js'
                      ,'resource/external/reportList/sigmabase.js'
                      ,'resource/external/highcharts/highcharts.js'
                      ,'resource/external/highcharts/exporting.js'
                      ,'resource/external/highcharts/highcharts-3d.js'
                      ,'resource/external/imgareaselect/jquery.imgareaselect.pack.js'
                      ,'resource/external/jrac/jquery.jrac.js'
                      ,'resource/external/paging/pagenav.min.js'
                      ,'resource/external/jquery.raty.js'
                      ,'resource/external/jquery.SuperSlide.js'
                      ,'resource/external/jstorage/jquery.json.min.js'
                      ,'resource/external/jstorage/jstorage.min.js'
                      ,'resource/external/loadmask/jquery.loadmask.min.js'
                      ,'resource/system/js/primaryOrgMemberAutoComplete.js'
                      ,'resource/external/raphael/raphael-min.js'
                      ,'resource/external/template.js'
                  ]
                  ,'resource/fileMerge/baseStage3.<%=jsVersion.jsVersionNo%>.js':[
                      'resource/system/js/userAutocompele.js'
                      ,'resource/system/js/idCheckUtil.js'
                      ,'resource/system/js/dialog.js'
                      ,'resource/system/js/actualPopulationDialog.js'
                      ,'resource/system/js/tabDialog.js'
                      ,'resource/system/js/formValidate.js'
                      ,'resource/system/js/gridUtil.js'
                      ,'resource/system/js/AreaData.js'
                      ,'resource/system/js/threeSelect.js'
                      ,'resource/system/js/propertyDictAutocomplete.js'
                      ,'resource/system/js/richText.js'
                      ,'resource/system/js/personnelComplete.js'
                      ,'resource/system/js/typeSelect.js'
                      ,'resource/system/js/announcement.js'
                      ,'resource/system/js/orgTreeManage.js'
                  ]
                  ,'resource/fileMerge/baseStage4.<%=jsVersion.jsVersionNo%>.js':[
                      'resource/system/js/resourcePoolPreminssinTreeManage.js'
                      ,'resource/system/js/resourcePoolTreeManage.js'
                      ,'resource/system/js/permissionTreeManage.js'
                      ,'resource/system/js/dailydirectoryTreeManage.js'
                      ,'resource/system/js/treeSelect.js'
                      ,'resource/system/js/issueManage/formatter.js'
                      ,'resource/system/js/issueManage/issueManage.js'
                      ,'resource/system/js/issueManage/supervise.js'
                      ,'resource/system/js/charts.js'
                      ,'resource/system/js/issueManage/urgent.js'
                      ,'resource/system/js/printArea.js'
                      ,'resource/system/js/jqgridMultiCheck.js'
                      ,'resource/system/js/excelDownload.js'
                      ,'resource/system/js/issueFlow.js'
                      ,'resource/system/js/isUpdate.js'
                      ,'resource/system/js/isUpdate1.js'
                      ,'resource/system/js/mailUtil.js'
                      ,'resource/system/js/myProfileTreeManage.js'
                      ,'resource/system/js/spin.min.js'
                  ]		
                  ,'resource/fileMerge/baseStage5.<%=jsVersion.jsVersionNo%>.js':[
                      'resource/system/js/inhabitantAutocomplete.js'
                      ,'resource/system/js/actualPopulationAutocomplete.js'
                      ,'resource/system/js/houseAutoComplete.js'
                      ,'resource/system/js/validationExtend.js'
                      ,'resource/system/js/uuid.js'
                      ,'resource/system/js/pagenav.js'
                      ,'resource/system/js/orgSelect.js'
                      ,'resource/system/js/dynamicTagToElement.js'
                      ,'resource/system/js/raphael.widget.js'
                  ]
                  ,'resource/fileMerge/baseStage6.<%=jsVersion.jsVersionNo%>.js':[
                      'resource/system/js/widget/spin.min.js'
                      ,'resource/system/js/widget/widget.js'
                      ,'resource/system/js/init.js'
                      ,'resource/system/js/incident/init.js'
                      ,'resource/system/js/incident/incidentTypeTreeManager.js'
                      ,'resource/system/js/component.js'
                      ,'resource/workBench/js/jquery.SuperSlide.js'
                      ,'resource/workBench/js/workbench.widget.js'
                      ,'resource/workBench/js/peopleLog/peopleLog.js'
                      ,'resource/workBench/js/statAnalyse/statAnalyse.js'
                      ,'resource/workBench/js/calendarWidget.js'
                      ,'resource/external/multiselect/jquery.multiselect.min.js'
                      ,'resource/system/js/schoolAutoComplete.js'
                      ,'resource/system/js/imageGallery/js/load-image.js'
                      ,'resource/system/js/imageGallery/js/jquery.image-gallery.min.js'
                      //,'resource/openLayersMap/js/OpenLayers-1/OpenLayers.js'
                      //,'resource/openLayersMap/js/GeoTile.js'
                      //,'resource/openLayersMap/js/ModifiedXYZ.js'
                      //,'resource/openLayersMap/js/TQMap.js'
                      ,'resource/openLayersMap/js/gis.widget.js'
                      ,'resource/openLayersMap/js/gisList.js'
                      ,'resource/openLayersMap/js/constant.js'
                      ,'resource/openLayersMap/js/pagenav.min.js'
                      ,'resource/openLayersMap/js/convert.js'
                      ,'resource/system/js/primaryMemberComplete.js'
                      ,'resource/system/js/fillGenderByIdCardNo.js'
                      ,'resource/system/js/otherMenu.js'
                      ,'resource/system/js/main.js'
                  ]
                  ,'resource/fileMerge/baseCss.css':[
                       'resource/css/formgrid.css'
                      ,'resource/external/jqGrid/css/ui.jqgrid.css'
                      ,'resource/external/poshytip/tip-yellowsimple/tip-yellowsimple.css'
                      ,'resource/external/loadmask/jquery.loadmask.css'
                      ,'resource/external/ext/css/ext-all.css'
                      ,'resource/external/contextmenu/css/contextmenu.css'
                      ,'resource/system/js/imageGallery/css/jquery.image-gallery.min.css'
                      ,'resource/xichang/css/accountReport.css'
                      ,'resource/external/imgareaselect/imgareaselect-default.css'
                      ,'resource/external/reportList/css/sigmawidgets.css'
                      ,'resource/external/jrac/style.jrac.css'
                      ,'resource/external/jqueryui/default/jquery-ui-1.9.2.custom.min.css'
                      ,'resource/system/css/main.css'
                  ]
              }
    	    }
    	},
        uglify: {
            options: {
                banner: ''
            },
            build: {
            	files:{
            		'resource/fileMini/minStage1.<%=jsVersion.jsVersionNo%>.js': ["resource/fileMerge/baseStage1.<%=jsVersion.jsVersionNo%>.js"]
            		,'resource/fileMini/minStage2.<%=jsVersion.jsVersionNo%>.js':["resource/fileMerge/baseStage2.<%=jsVersion.jsVersionNo%>.js"]
            		,'resource/fileMini/minStage3.<%=jsVersion.jsVersionNo%>.js':["resource/fileMerge/baseStage3.<%=jsVersion.jsVersionNo%>.js"]
                    ,'resource/fileMini/minStage4.<%=jsVersion.jsVersionNo%>.js':["resource/fileMerge/baseStage4.<%=jsVersion.jsVersionNo%>.js"]
                    ,'resource/fileMini/minStage5.<%=jsVersion.jsVersionNo%>.js':["resource/fileMerge/baseStage5.<%=jsVersion.jsVersionNo%>.js"]
                    ,'resource/fileMini/minStage6.<%=jsVersion.jsVersionNo%>.js':["resource/fileMerge/baseStage6.<%=jsVersion.jsVersionNo%>.js"]
            		//,'resource/fileMini/minBaseCss.css':["resource/fileMerge/baseCss.css"]

            	  /*,'resource/fileMini/reportMini.js':["resource/fileMerge/report.base.js"]
                    ,'resource/fileMini/jstorageMini.js':["resource/fileMerge/jstorage.base.js"]
                    ,'resource/fileMini/incidentMini.js':["resource/fileMerge/incident.base.js"]
            		,'resource/fileMini/issueManageMini.js':["resource/fileMerge/issueManage.base.js"]*/
            	}
            }
        }
    });
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.registerTask('default', [ 'concat', 'uglify']);
    
};