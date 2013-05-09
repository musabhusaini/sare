###
Sentilab SARE: a Sentiment Analysis Research Environment
Copyright (C) 2013 Sabanci University Sentilab
http://sentilab.sabanciuniv.edu

This file is part of SARE.

SARE is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SARE is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SARE. If not, see <http://www.gnu.org/licenses/>.
###

minifiableDep = window.RjsHelpers.minifiableDep
define = window.define
define [
	"jquery"
	"jsRoutes"
	minifiableDep "main.html"
	minifiableDep "Sare.Widget"
	minifiableDep "widgets.storeList"
	minifiableDep "widgets.progress"
	minifiableDep "widgets.tabbedNav"
	"jsplugins/jqplot/jquery.jqplot.min"
	"jsplugins/jqplot/plugins/jqplot.pieRenderer.min"
	"jsplugins/jqplot/plugins/jqplot.barRenderer.min"
	"jsplugins/jqplot/plugins/jqplot.categoryAxisRenderer.min"
	"jsplugins/jqplot/plugins/jqplot.canvasTextRenderer.min"
	"jsplugins/jqplot/plugins/jqplot.canvasAxisLabelRenderer.min"
	"jsplugins/jqplot/plugins/jqplot.canvasAxisTickRenderer.min"
	"jsplugins/jstree/jquery.jstree"
], ->
	# define reusables
	$ = window.jQuery
	jsRoutes = window.jsRoutes
	JSON = window.JSON
	Sare = window.Sare
	Helpers = Sare.Helpers
	Page = Sare.Page
	Selectors = Page.Selectors
	Strings = Page.Strings
	Widgets = Page.Widgets
	
	Math = window.Math
	
	widget =
		_summaryPlot: null
		
		_create: ->
			@options.corpus ?= $(@element).data @options.corpusKey
			@options.lexicon ?= $(@element).data @options.lexiconKey
			
			@_on $(window.document),
				"click": (e) ->
					if not $(e.target).closest(@_$ @options.documentsTreeContainer).length
						$(@_$ @options.documentsTreeContainer).jstree "unset_focus"
			
			@_on $(window),
				resize: (e) ->
					@_summaryPlot?.replot()
	
			@_on @_$(@options.documentsTreeContainer),
				"click a": (e) ->
					$(e.target).focus()
			
			@_$(@options.documentsTreeContainer).on
				"loaded.jstree": (e, data) =>
					data.inst.select_node $(data.inst.get_container_ul()).children "li:first"
				
				"select_node.jstree": (e, data) =>
					roundPolarity = (polarity) ->
						Math.round(polarity*1000)/1000
					
					@_summaryPlot?.destroy()
					@_summaryPlot = null
					@_$(@options.navContainer).find("ul.nav li").hide()
					
					type = $(data.rslt.obj).data @options.typeKey
					summary = $(data.rslt.obj).data @options.summaryKey
					document = $(data.rslt.obj).data @options.documentKey
					visualTitle = if type is "orientation" then "<strong>Top 4 Emerging Aspects<strong>"
					
					tableMap = summary or document?.aspectPolarities
					scoreMax = 0
					if tableMap?
						tableArray = (for title, value of tableMap
							scoreMax = Math.max scoreMax, Math.abs value
							[ title, roundPolarity value ])
					
					if tableArray?.length
						tableArray.sort (a, b) -> b[1] - a[1]
						if summary?
							others = tableArray.splice 4, tableArray.length-4
							othersTotal = 0
							for [ _, value ] in others
								othersTotal += value
							if othersTotal and others.length
								tableArray.push [ "Others", othersTotal ]
					
					graphInnerContainer = @_$(@options.graphContainer).children("div").first()
					graphId = $(graphInnerContainer).empty().removeClass().attr "id"
					@_$(@options.tableContainer).find("table caption")
						.html visualTitle
					thead = @_$(@options.tableContainer).find("table thead").empty()
					tbody = @_$(@options.tableContainer).find("table tbody").empty()
					
					@_$(@options.visualsNav).show()
					if tableArray?.length
						for [title, value] in tableArray
							$(tbody).append "<tr><td>#{title}</td><td>#{roundPolarity value}</td></tr>"
					else
						$(graphInnerContainer).text "Not enough data for a graph"
					
					if summary?
						categoryHeader = switch type
							when "corpus" then "Orientation"
							when "orientation" then "Aspect"
							else ""
						
						$(thead).append "<tr><th>#{categoryHeader}</th><th>Count</th></tr>"
						$(tbody).append "<tr><td>Total</td><td>#{$(data.rslt.obj).data @options.sizeKey}</td></tr>"
						
						if tableArray?.length
							$(graphInnerContainer).addClass "pie"
							@_summaryPlot = $.jqplot graphId, [ tableArray ],
								title: visualTitle
								seriesDefaults:
									renderer: jQuery.jqplot.PieRenderer
									rendererOptions:
										showDataLabels: true
								grid:
									borderWidth: 0
									shadow: false
									background: "transparent"
								legend:
									show: true
									location: 'e'
							
							# this makes sure the proportions are correct.
							@_delay ->
								@_summaryPlot.replot()
							, 0
									
						activeNav = @_$(@options.detailsContainer).tabbedNav("getActiveNav")?.li
						if not activeNav.length or not $(activeNav).is(@options.visualsNav)
							@_$(@options.detailsContainer).tabbedNav "activate", @options.graphicalNavKey
							
					else if document?
						fontFamily = $(graphInnerContainer).css "font-family"
						if tableArray?.length
							graphData = [(
								for [ title, value ] in tableArray
									[ Helpers.String.truncate(title, 100/tableArray.length, true), value ]
							)]
							@_summaryPlot = $.jqplot graphId, graphData,
								axesDefaults:
									labelRenderer: $.jqplot.CanvasAxisLabelRenderer
									labelOptions:
										# this should get picked up automatically, but it's not.
										fontFamily: fontFamily
									tickRenderer: $.jqplot.CanvasAxisTickRenderer
									tickOptions:
										# this should get picked up automatically, but it's not.
										fontFamily: fontFamily
								seriesDefaults:
									renderer: $.jqplot.BarRenderer
									rendererOptions:
										fillToZero: true
								axes:
									xaxis:
										label: "Aspect name"
										renderer: $.jqplot.CategoryAxisRenderer
									yaxis:
										label: "Polarity score [-1, +1]"
										min: -scoreMax
										max: scoreMax
										tickOptions:
											formatString: "%1.3f"
						
						@_$(@options.documentNav).show()
						@_$(@options.documentContainer).text document.content
						$(thead).append "<tr><th>Aspect</th><th>Polarity</th></tr>"
						$(tbody).append "<tr><td>Overall</td><td>#{roundPolarity document.polarity}</td></tr>"
					
			@_on @options.detailsContainer,
				tabbedNavTabChanged: (e, data) ->
					if data?.key is @options.graphicalNavKey
						@_summaryPlot?.replot()
			
			@_$(@options.documentsTreeContainer).jstree
				ui:
					select_limit: 1
				types:
					valid_children: [ "documentGroup" ]
					types:
						documentGroup:
							valid_children: [ "documentGroup", "document" ]
						document:
							max_children: 0
							icon:
								image: jsRoutes.controllers.Assets.at("plugins/jstree/themes/misc/file.png").url
				plugins: [ "themes", "html_data", "ui", "types", "hotkeys" ]
			
			@_$(@options.detailsContainer).tabbedNav()
			
		refresh: ->
			$(@element).data Strings.widgetKey, @
			
		_init: ->
			@refresh()
			
		_destroy: ->
			
		_setOption: (key, value) ->
			switch key
				when "disabled"
					null
			$.Widget.prototype._setOption.apply @, arguments
		
		_getCreateOptions: ->
			engine: null
			documentsTreeContainer: ".ctr-documents-tree"
			detailsContainer: ".ctr-details"
			navContainer: ".ctr-nav"
			documentNav: ".nav-document"
			visualsNav: ".nav-visual"
			documentContainer: ".ctr-document"
			tableContainer: ".ctr-visual-table"
			graphContainer: ".ctr-visual-graph"
			graphicalNavKey: "graphical"
			lexiconKey: "lexicon"
			corpusKey: "corpus"
			typeKey: "type"
			sizeKey: "size"
			summaryKey: "summary"
			documentKey: "document"
	
	$.widget "widgets.aspectOpinionMinerResults", Sare.Widget, widget