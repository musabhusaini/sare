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
	
	widget =
		_create: ->
			@options.corpus ?= $(@element).data @options.corpusKey
			@options.lexicon ?= $(@element).data @options.lexiconKey
			
			@_$(@options.progressContainer).progress
				callback: =>
					@_changeInputState @options.analyzeButton, "reset"
					@_delay ->
						@_$(@options.resultsContainer).load @options.resultsRoute(@options.corpus.id, @options.lexicon.id, @options.engine).url
					, 0
			
			@_on @_$(@options.analyzeButton),
				click: ->
					@_changeInputState @options.analyzeButton, "loading"
					@_$(@options.resultsContainer).empty()
					@options.mineRoute(@options.corpus.id, @options.lexicon.id, @options.engine).ajax
						success: (progressToken) =>
							@_$(@options.progressContainer).progress "option",
								redeemAjax: @options.redeemRoute(progressToken.id).ajax
							@_$(@options.progressContainer).progress "animate"
			
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
			analyzeButton: ".btn-analyze"
			progressContainer: ".ctr-aom-progress"
			resultsContainer: ".ctr-aom-results-outer"
			mineRoute: jsRoutes.controllers.modules.opinionMiners.base.AspectOpinionMiner.mine
			resultsRoute: jsRoutes.controllers.modules.opinionMiners.base.AspectOpinionMiner.resultsView
			redeemRoute: jsRoutes.controllers.modules.opinionMiners.base.AspectOpinionMiner.redeem
			lexiconKey: "lexicon"
			corpusKey: "corpus"
	
	$.widget "widgets.aspectOpinionMinerEditor", Sare.Widget, widget