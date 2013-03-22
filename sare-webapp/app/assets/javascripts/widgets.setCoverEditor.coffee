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

widget =
	_fixButtons: ->
		
	_getUpdated: (setcover) ->
		setcover ?= @options.setcover
		{ weightCoverage, tokenizingOptions } = updatedSetCover =
			weightCoverage: 1.0	# TODO: get real weight coverage
			tokenizingOptions:
				tags: ($(tag).val() for tag in @_$(@options.posTagCheckboxes).filter ":checked")
				isLemmatized: @_$(@options.lemmatizeCheckbox).is ":checked"
		
		updated = no
		if weightCoverage isnt setcover.weightCoverage then updated = yes
		else if tokenizingOptions.isLemmatized isnt setcover.tokenizingOptions.isLemmatized then updated = yes
		else if tokenizingOptions.tags.length isnt setcover.tokenizingOptions.length then updated = yes
		else
			for tag in tokenizingOptions.tags
				updated = yes if not $.inArray tag, setcover.tokenizingOptions.tags
		
		if not updated then return updated
		
		$.extend {}, setcover, updatedSetCover
	
	_updateView: (setcover) ->
		
	
	_create: ->
		@options.setcover ?= $(@element).data @options.setCoverKey
		
		@_on @_$(@options.applyButton),
			click: ->
				updatedSetCover = @_getUpdated()
				if not updatedSetCover then return
				@options.updateRoute("null", @options.setcover.id).ajax
					data: JSON.stringify updatedSetCover
					contentType: Helpers.ContentTypes.json
					success: (setcover) =>
						redeem = =>
							@_delay ->
								@options.redeemRoute(setcover.id).ajax
									success: (progressToken) =>
										if not progressToken.progress?
											@_$(@options.progressContainer).hide()
											@_updateView progressToken
										else
											redeem()
											@_$(@options.progressContainer).children(".bar")
												.css "width", "#{window.Math.round(100 * progressToken.progress)}%"
							, 1000
						
						@_$(@options.progressContainer)
							.show()
							.children(".bar")
								.css "width", "0%"
						redeem()
		
		@_$(@options.posTagCheckboxes).parent().tooltip()
		@_$(@options.applyButton).tooltip()
		
		@_fixButtons()
		
	refresh: ->
		$(@element).data Strings.widgetKey, @
		
	_init: ->
		@refresh()
		
	_destroy: ->
		
	_setOption: (key, value) ->
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		posTagCheckboxes: ".chk-posTag"
		lemmatizeCheckbox: ".chk-lemmatize"
		coverageMatrixContainer: ".ctr-sc-cov-matrix"
		applyButton: ".btn-apply"
		progressContainer: ".ctr-sc-progress"
		updateRoute: jsRoutes.controllers.modules.SetCoverBuilder.update
		redeemRoute: jsRoutes.controllers.modules.SetCoverBuilder.redeem
		setCoverKey: "setcover"

$.widget "widgets.setCoverEditor", Sare.Widget, widget