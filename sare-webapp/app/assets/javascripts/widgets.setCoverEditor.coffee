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

makeTolerance = (coverage) ->
	Math.round((1.0 - coverage) * 100);

makeCoverage = (tolerance) ->
	1 - (tolerance / 100.0)

widget =
	_fixButtons: ->
		updated = @_getHardUpdated()
		
		@_$(@options.plotToleranceButton).popover "destroy"
		@_$(@options.plotToleranceButton).tooltip "destroy"
		
		if updated
			@_$(@options.plotToleranceButton).popover()
			@_togglePlot null, true
		else
			@_$(@options.plotToleranceButton).tooltip
				title: "Plot the effect of tolerance on optimization"
				trigger: "hover"
		
		updated = updated or @_getUpdated()
		for input in [ @options.applyButton, @options.resetButton ]
			@_changeInputState input, "enabled", updated
	
	_getHardUpdated: (setcover) ->
		setcover ?= @options.setcover
		{ tokenizingOptions } = updatedSetCover =
			tokenizingOptions:
				tags: ($(tag).val() for tag in @_$(@options.posTagCheckboxes).filter ":checked")
				isLemmatized: @_$(@options.lemmatizeCheckbox).is ":checked"
		
		updated = no
		if tokenizingOptions.isLemmatized isnt setcover.tokenizingOptions.isLemmatized then updated = yes
		else if tokenizingOptions.tags.length isnt setcover.tokenizingOptions.tags.length then updated = yes
		else
			for tag in tokenizingOptions.tags
				updated = yes if $.inArray(tag, setcover.tokenizingOptions.tags) < 0
				
		if not updated then return updated
		
		$.extend {}, setcover, updatedSetCover
		
	_getUpdated: (setcover) ->
		setcover ?= @options.setcover
		weightCoverage = @_$(@options.toleranceTextbox).val()
		weightCoverage = (if window.isNaN weightCoverage then null else makeCoverage window.Number weightCoverage)
		if weightCoverage is null or not (0 <= weightCoverage <= 1.0) then weightCoverage = setcover.weightCoverage 
		{ tokenizingOptions } = updatedSetCover =
			weightCoverage: weightCoverage
		
		hardUpdated = @_getHardUpdated setcover
		updated = no
		if weightCoverage isnt setcover.weightCoverage then updated = yes
		else if hardUpdated then updated = yes
		
		if not updated then return updated
		
		$.extend {}, setcover, hardUpdated or {}, updatedSetCover
	
	_tolerancePlot: null
	
	_togglePlot: (duration, initial, callback) ->
		initial ?= @options.plotShown
		duration ?= 200
		
		@options.plotShown = not initial
		if @options.plotShown
			@_$(@options.plotToleranceButton).addClass "active"
		else
			@_$(@options.plotToleranceButton).removeClass "active"
			@_$(@options.coverageMatrixContainer)
				.hide duration, =>
					@_$(@options.coverageMatrixContainer).empty()
					callback?()
			return @options.plotShown
		
		@_$(@options.coverageMatrixContainer).empty()
		
		matrix = []
		initialPoint = null
		for coverage, covered of @options.setcover.coverageMatrix
			point = [100-coverage, Math.round(covered * @options.setcover.baseCorpus.size)]
			if coverage/100 is @options.setcover.weightCoverage or not initialPoint? then initialPoint = point
			matrix.push point
		
		id = @_$(@options.coverageMatrixContainer)
			.addClass("invisible")
			.show()
			.attr "id"
		
		@_tolerancePlot = $.jqplot id, [ matrix ],
			axesDefaults:
				labelRenderer: $.jqplot.CanvasAxisLabelRenderer
			seriesDefaults:
				rendererOptions:
					smooth: true
			axes:
				xaxis:
					label: "Loss tolerance (% of useful words lost)"
					pad: 0
					tickOptions:
						formatString: "%2.0f"
				yaxis:
					label: "Optimized corpus size (# of documents)"
					tickOptions:
						formatString: "%2.0f"
			highlighter:
				formatString: "<table><tr><td>Loss tolerance:</td><td>%s%</td></tr><tr><td>Optimized corpus size:</td><td>%s</td></tr></table>"
				show: true
				tooltipLocation: "ne"
				fadeTooltip: true
				sizeAdjust: 7.5
			permHighlighter:
				show: true
				initial:
					data: initialPoint
					seriesIndex: 0
			cursor:
				show: false
		
		@_$(@options.coverageMatrixContainer)
			.hide()
			.removeClass("invisible")
			.show duration, =>
				callback?()
		return @options.plotShown
	
	_updateView: (setcover) ->
		@options.setcover = setcover
		@_fixButtons()
		
		# send options to the module manager.
		if not @options.suppressOutput
			Widgets.moduleManager "option", "output", if @options.setcover?.size > 0 then @options.setcover else null
		
		$(@element).trigger "setCoverUpdated", setcover
	
	_create: ->
		@options.setcover ?= $(@element).data @options.setCoverKey
		
		@_$(@options.progressContainer).progress
			callback: (setcover) =>
				@_changeInputState @options.applyButton, "reset"
				@_delay ->
					@_updateView setcover
				, 0
		
		@_on @_$("input"),
			change: -> @_fixButtons()
			keyup: -> @_fixButtons()
			input: -> @_fixButtons()
		
		@_on @_$(@options.applyButton),
			click: ->
				updatedSetCover = @_getUpdated()
				if not updatedSetCover then return
				
				@_changeInputState @options.applyButton, "loading"
				@_changeInputState @options.resetButton, "disabled"
				
				@options.updateRoute(null, @options.setcover.id).ajax
					data: JSON.stringify updatedSetCover
					contentType: Helpers.ContentTypes.json
					success: (setcover) =>
						@_$(@options.progressContainer).progress "option",
							redeemAjax: @options.redeemRoute(setcover.id).ajax
						@_$(@options.progressContainer).progress "animate"
		
		@_on @_$(@options.resetButton),
			click: ->
				options = @options
				@_$(@options.posTagCheckboxes).each ->
					$(@).prop "checked", $.inArray($(@).val(), options.setcover.tokenizingOptions.tags) > -1
				@_$(@options.lemmatizeCheckbox).prop "checked", @options.setcover.tokenizingOptions.isLemmatized
				@_$(@options.toleranceTextbox).val makeTolerance @options.setcover.weightCoverage
				@_fixButtons()
		
		@_on @_$(@options.coverageMatrixContainer),
			jqplotDataClick: (event, seriesIndex, pointIndex, data) ->
				@_$(@options.toleranceTextbox).val Math.round data[0]
				@_fixButtons()
		
		@_on @_$(@options.plotToleranceButton),
			click: (e) ->
				if @_$(@options.plotToleranceButton).is ".active"
					return not @_togglePlot null, true

				if @_getHardUpdated()
					return false
				
				if not @options.setcover.coverageMatrix?
					response = @options.getSetCoverRoute(@options.setcover.id, true).ajax
						success: (setcover) =>
							@_updateView setcover
							@_togglePlot null, false
				else
					@_togglePlot null, false
				
				false
		
		@_$(@options.posTagCheckboxes).parent().tooltip()
		@_$(@options.lemmatizeCheckbox).parent().tooltip()
		@_$(@options.toleranceTextbox).tooltip()
		@_$("button").tooltip()
		
	refresh: ->
		$(@element).data Strings.widgetKey, @
		
		@_togglePlot null, not @options.plotShown
		@_updateView @options.setcover
		
	_init: ->
		@refresh()
		
	_destroy: ->
		
	_setOption: (key, value) ->
		switch key
			when "disabled"
				btns = "#{@options.setCoverControls} button"
				if value then @_$(btns).hide() else @_$(btns).show()
				@_changeInputState @_$("input,button"), "disabled", value
				if not value then @refresh()
				if value and @_$(@options.plotToleranceButton).is ".active"
					@_$(@options.plotToleranceButton).button "toggle"
					@_$(@options.coverageMatrixContainer).empty().hide()
					@options.plotShown = false
					
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		suppressOutput: false
		plotShown: false
		posTagCheckboxes: ".chk-posTag"
		lemmatizeCheckbox: ".chk-lemmatize"
		toleranceContainer: ".ctr-tolerance"
		toleranceTextbox: ".txt-tolerance"
		plotToleranceButton: ".btn-plot-tolerance"
		coverageMatrixContainer: ".ctr-sc-cov-matrix"
		setCoverControls: ".ctr-sc-controls"
		applyButton: ".btn-apply"
		resetButton: ".btn-reset"
		progressContainer: ".ctr-sc-progress"
		getSetCoverRoute: jsRoutes.controllers.modules.SetCoverBuilder.getSetCover
		updateRoute: jsRoutes.controllers.modules.SetCoverBuilder.update
		redeemRoute: jsRoutes.controllers.modules.SetCoverBuilder.redeem
		setCoverKey: "setcover"

$.widget "widgets.setCoverEditor", Sare.Widget, widget