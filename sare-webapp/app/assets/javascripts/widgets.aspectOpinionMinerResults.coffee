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
				@_summaryPlot?.replot
					resetAxes: true

		@_on @_$(@options.documentsTreeContainer),
			"click a": (e) ->
				$(e.target).focus()
		
		@_$(@options.documentsTreeContainer).on
			"loaded.jstree": (e, data) =>
				data.inst.select_node $(data.inst.get_container_ul()).children "li:first"
			
			"select_node.jstree": (e, data) =>
				@_summaryPlot?.destroy()
				@_summaryPlot = null
				@_$(@options.navContainer).find("ul.nav li").hide()
				
				type = $(data.rslt.obj).data @options.typeKey
				summary = $(data.rslt.obj).data @options.summaryKey
				document = $(data.rslt.obj).data @options.documentKey
				
				tableMap = summary or document?.aspectPolarities
				graphData = null
				scoreMax = 0
				graphInnerContainer = @_$(@options.graphContainer).children("div").first()
				graphId = $(graphInnerContainer).empty().removeClass().attr "id"
				thead = @_$(@options.tableContainer).find("table thead").empty()
				tbody = @_$(@options.tableContainer).find("table tbody").empty()
				
				if tableMap?
					for title, value of tableMap
						$(tbody).append "<tr><td>#{title}</td><td>#{Math.round(value*1000)/1000}</td></tr>"
						scoreMax = Math.max scoreMax, Math.abs value
					@_$(@options.visualsNav).show()
					graphData = [ (for title, value of tableMap
						[title, value]) ]
				
				if not graphData?[0].length
					$(graphInnerContainer).text "Not enough data for a graph"
				
				if summary?
					$(thead).append "<tr><th>Type</th><th>Count</th></tr>"
					$(tbody).append "<tr><td>Total</td><td>#{$(data.rslt.obj).data @options.sizeKey}</td></tr>"
					
					if graphData?[0].length
						$(graphInnerContainer).addClass "pie"
						@_summaryPlot = $.jqplot graphId, graphData,
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
								
					activeNav = @_$(@options.detailsContainer).tabbedNav("getActiveNav")?.li
					if not activeNav.length or not $(activeNav).is(@options.visualsNav)
						@_$(@options.detailsContainer).tabbedNav "activate", @options.graphicalNavKey
						
				else if document?
					if graphData?[0].length
						@_summaryPlot = $.jqplot graphId, graphData,
							seriesDefaults:
								renderer: $.jqplot.BarRenderer
								rendererOptions:
									fillToZero: true
							axes:
								xaxis:
									renderer: $.jqplot.CategoryAxisRenderer
									tickRenderer: $.jqplot.CanvasAxisTickRenderer
									tickOptions:
										fontFamily: "Courier New"
										fontSize: "9pt"
								yaxis:
									min: -scoreMax
									max: scoreMax
					
					@_$(@options.documentNav).show()
					@_$(@options.documentContainer).text document.content
					$(thead).append "<tr><th>Aspect</th><th>Polarity</th></tr>"
					$(tbody).append "<tr><td>Overall</td><td>#{value = Math.round(document.polarity*1000)/1000}</td></tr>"
				
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
							image: jsRoutes.controllers.Assets.at("/plugins/jstree/themes/misc/file.png").url
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