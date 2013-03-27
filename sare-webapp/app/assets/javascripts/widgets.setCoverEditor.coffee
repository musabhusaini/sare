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

makeTolerance = (coverage) ->
	window.Math.round((1.0 - coverage) * 100);

makeCoverage = (tolerance) ->
	1 - (tolerance / 100.0)

widget =
	_fixButtons: ->
		updated = @_getUpdated()
		for input in [ @options.applyButton, @options.resetButton ]
			@_changeInputState input, "enabled", updated
		
	_getUpdated: (setcover) ->
		setcover ?= @options.setcover
		weightCoverage = @_$(@options.toleranceTextbox).val()
		weightCoverage = (if window.isNaN weightCoverage then null else makeCoverage window.Number weightCoverage)
		if weightCoverage is null or not (0 <= weightCoverage <= 1.0) then weightCoverage = setcover.weightCoverage 
		{ tokenizingOptions } = updatedSetCover =
			weightCoverage: weightCoverage
			tokenizingOptions:
				tags: ($(tag).val() for tag in @_$(@options.posTagCheckboxes).filter ":checked")
				isLemmatized: @_$(@options.lemmatizeCheckbox).is ":checked"
		
		updated = no
		if weightCoverage isnt setcover.weightCoverage then updated = yes
		else if tokenizingOptions.isLemmatized isnt setcover.tokenizingOptions.isLemmatized then updated = yes
		else if tokenizingOptions.tags.length isnt setcover.tokenizingOptions.tags.length then updated = yes
		else if weightCoverage isnt setcover.weightCoverage then updated = yes
		else
			for tag in tokenizingOptions.tags
				updated = yes if $.inArray(tag, setcover.tokenizingOptions.tags) < 0
		
		if not updated then return updated
		
		$.extend {}, setcover, updatedSetCover
	
	_updateView: (setcover) ->
		@options.setcover = setcover
		@_fixButtons()
		
		# send options to the module manager.
		if not @options.suppressOutput
			Widgets.moduleManager "option", "output", if @options.setcover?.size > 0 then @options.setcover else null
		
		$(@element).trigger "setCoverUpdated", setcover
	
	_create: ->
		@options.setcover ?= $(@element).data @options.setCoverKey
		
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
				
				pingTimeout = 500
				animateProgress = (progress, duration, callback) =>
					duration ?= pingTimeout
					progress ?= 0
					@_$(@options.progressContainer).children(".bar")
						.animate
							width: "#{progress}%"
						, duration, ->
							callback?()
				
				@options.updateRoute("null", @options.setcover.id).ajax
					data: JSON.stringify updatedSetCover
					contentType: Helpers.ContentTypes.json
					success: (setcover) =>
						redeem = =>
							@_delay ->
								@options.redeemRoute(setcover.id).ajax
									success: (progressToken) =>
										if not progressToken.progress?
											animateProgress 100, null, =>
												@_delay ->
													@_$(@options.progressContainer).hide()
													@_changeInputState @options.applyButton, "reset"
													@_delay ->
														@_updateView progressToken
													, 0
												, pingTimeout
										else
											redeem()
											animateProgress window.Math.round(100 * progressToken.progress)
							, pingTimeout
						
						@_$(@options.progressContainer)
							.show()
							.children(".bar")
								.css "width", "0%"
						redeem()

		@_on @_$(@options.resetButton),
			click: ->
				options = @options
				@_$(@options.posTagCheckboxes).each ->
					$(@).prop "checked", $.inArray($(@).val(), options.setcover.tokenizingOptions.tags) > -1
				@_$(@options.lemmatizeCheckbox).prop "checked", @options.setcover.tokenizingOptions.isLemmatized
				@_$(@options.toleranceTextbox).val makeTolerance @options.setcover.weightCoverage
				@_fixButtons()
		
		@_$(@options.posTagCheckboxes).parent().tooltip()
		@_$(@options.lemmatizeCheckbox).parent().tooltip()
		@_$(@options.toleranceTextbox).tooltip()
		@_$("button").tooltip()
		
	refresh: ->
		$(@element).data Strings.widgetKey, @

		@_updateView @options.setcover
		
	_init: ->
		@refresh()
		
	_destroy: ->
		
	_setOption: (key, value) ->
		switch key
			when "disabled"
				if value then @_$("button").hide() else @_$("button").show()
				@_changeInputState @_$("input"), if value then "disabled" else "enabled"
				if not value then @refresh()
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		suppressOutput: false
		posTagCheckboxes: ".chk-posTag"
		lemmatizeCheckbox: ".chk-lemmatize"
		toleranceContainer: ".ctr-tolerance"
		toleranceTextbox: ".txt-tolerance"
		coverageMatrixContainer: ".ctr-sc-cov-matrix"
		applyButton: ".btn-apply"
		resetButton: ".btn-reset"
		progressContainer: ".ctr-sc-progress"
		updateRoute: jsRoutes.controllers.modules.SetCoverBuilder.update
		redeemRoute: jsRoutes.controllers.modules.SetCoverBuilder.redeem
		setCoverKey: "setcover"

$.widget "widgets.setCoverEditor", Sare.Widget, widget