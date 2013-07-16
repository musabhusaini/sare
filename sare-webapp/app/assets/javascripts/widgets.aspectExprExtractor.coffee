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
	"jquery-ui"
	"jsRoutes"
	"bootstrap-button"
	"bootstrap-tooltip"
	minifiableDep "main.html"
	minifiableDep "Sare.Widget"
	minifiableDep "widgets.progress"
	minifiableDep "moduleView.html"
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
		_sendModuleOutput: (lexicon, corpus) ->
			lexicon ?= @options.lexicon
			corpus ?= @options.corpus
			output = null
			if lexicon?
				output = [ @options.lexicon ]
				output.push(corpus) if corpus?
			Widgets.moduleManager "option", "output", output
		
		_create: ->
			@options.corpus ?= $(@element).data @options.corpusKey
			@options.lexicon ?= $(@element).data @options.lexiconKey
			
			@_$(@options.progressContainer).progress
				callback: (lexicon) =>
					@_changeInputState @options.applyButton, "reset"
					@options.lexicon = lexicon
					@_sendModuleOutput()
					@_$(@options.lexicaContainer).children(Selectors.moduleContainer)
						.storeList "selected", lexicon.id
			
			if @options.corpus?
				@_$(@options.corporaContainer).children(Selectors.moduleContainer)
					.storeList "disable"
			else
				@_on @_$(@options.corporaContainer).children(Selectors.moduleContainer),
					storeListSelectionChange: (e, selected) ->
						@options.corpus = selected.data
						@_sendModuleOutput()
					storeUpdate: (e, data) ->
						@options.corpus = data.updatedData
						@_sendModuleOutput()
						
				@_$(@options.corporaContainer).children(Selectors.moduleContainer)
					.storeList "option",
						suppressOutput: true
			
			if @options.lexicon?
				@_$(@options.lexicaContainer).children(Selectors.moduleContainer)
					.storeList "disable"
			else
				@_on @_$(@options.lexicaContainer).children(Selectors.moduleContainer),
					storeListSelectionChange: (e, selected) ->
						@options.lexicon = selected.data
						@_sendModuleOutput()
					storeUpdate: (e, data) ->
						@options.lexicon = data.updatedData
						@_sendModuleOutput()
						
				@_$(@options.lexicaContainer).children(Selectors.moduleContainer)
					.storeList "option",
						suppressOutput: true
						addRoute: =>
							@options.createLexiconRoute(@options.corpus?.id ? null)
			
			@_on @_$(@options.applyButton),
				click: (e) ->
					@_changeInputState @options.applyButton, "loading"
					@options.extractExpressionsRoute(@options.corpus.id, @options.lexicon?.id).ajax
						data: JSON.stringify
							autoLabelingMinimum: @_$(@options.minimumLabelTextbox).val()
							scoreAcceptanceThreshold: @_$(@options.scoreThresholdTextbox).val()
						contentType: Helpers.ContentTypes.json
						success: (token) =>
							@_$(@options.progressContainer).progress "option",
								redeemAjax: @options.redeemRoute(token.id).ajax
							@_$(@options.progressContainer).progress "animate"
					false
			
			@_$(@options.minimumLabelTextbox).tooltip()
			@_$(@options.scoreThresholdTextbox).tooltip()
			@_$(@options.applyButton).tooltip()
		
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
			corporaContainer: ".ctr-corpora"
			lexicaContainer: ".ctr-lexica"
			minimumLabelTextbox: ".txt-label-min"
			scoreThresholdTextbox: ".txt-score-threshold"
			applyButton: ".btn-apply"
			progressContainer: ".ctr-aee-progress"
			createLexiconRoute: jsRoutes.controllers.modules.AspectLexBuilder.create
			extractExpressionsRoute: jsRoutes.controllers.modules.AspectExprExtractor.extract
			redeemRoute: jsRoutes.controllers.modules.AspectExprExtractor.redeem
			corpusKey: "corpus"
			lexiconKey: "lexicon"
	
	$.widget "widgets.aspectExprExtractor", Sare.Widget, widget