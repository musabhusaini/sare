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
	minifiableDep "main.html"
	minifiableDep "Sare.Widget"
	minifiableDep "widgets.storeList"
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
			@options.lexicon ?= $(@element).data @options.lexiconKey
			@options.corpus ?= $(@element).data @options.corpusKey
			
			refreshView = (corpus, lexicon) =>
				@options.lexicon = lexicon
				@options.corpus = corpus
				corpus ?= @options.lexicon?.baseCorpus
				@_sendModuleOutput @options.lexicon, corpus
				
				@_$(@options.documentsContainer).empty()
				@_$(@options.lexiconContainer).empty()
				
				if @options.lexicon?
					if corpus?
						@_$(@options.documentsContainer)
							.removeClass("hide")
							.load @options.documentsViewRoute(corpus.id, @options.lexicon.id).url
					else
						@_$(@options.documentsContainer).addClass "hide"
					@_$(@options.lexiconContainer)
						.load @options.lexiconViewRoute(@options.lexicon.id).url
			
			if @options.corpus?
				@_$(@options.corporaContainer).children(Selectors.moduleContainer)
					.storeList "disable"
			else
				@_on @_$(@options.corporaContainer).children(Selectors.moduleContainer),
					storeListSelectionChange: (e, selected) ->
						refreshView selected.data, @options.lexicon
					storeUpdate: (e, data) ->
						refreshView data.updatedData, @options.lexicon
						
				@_$(@options.corporaContainer).children(Selectors.moduleContainer)
					.storeList "option",
						suppressOutput: true
				
			if @options.lexicon?
				@_$(@options.lexicaContainer).children(Selectors.moduleContainer)
					.storeList "disable"
			else
				@_on @_$(@options.lexicaContainer).children(Selectors.moduleContainer),
					storeListSelectionChange: (e, selected) ->
						refreshView @options.corpus, selected.data
					storeUpdate: (e, data) ->
						refreshView @options.corpus, data.updatedData
				
				@_$(@options.lexicaContainer).children(Selectors.moduleContainer)
					.storeList "option",
						suppressOutput: true
						addRoute: =>
							@options.createLexiconRoute(@options.corpus?.id ? null)
			
			refreshView @_$(@options.corporaContainer)
				.children(Selectors.moduleContainer)
					.storeList("selected").data
			, @_$(@options.lexicaContainer)
					.children(Selectors.moduleContainer)
						.storeList("selected").data
		
		refresh: ->
			@_sendModuleOutput()
			$(@element).data Strings.widgetKey, @
			
		_init: ->
			@refresh()
			
		_destroy: ->
			
		_setOption: (key, value) ->
			switch key
				when "disabled"
					@_$(@options.lexicaContainer).children(Selectors.moduleContainer)
						.storeList if value then "disable" else "enable"
					for input in [ @options.documentsContainer, @options.lexiconContainer ]
						if value then @_$(input).addClass("hide") else @_$(input).removeClass "hide"
			$.Widget.prototype._setOption.apply @, arguments
		
		_getCreateOptions: ->
			corporaContainer: ".ctr-corpora"
			lexicaContainer: ".ctr-lexica"
			documentsContainer: ".ctr-documents"
			lexiconContainer: ".ctr-alex"
			createLexiconRoute: jsRoutes.controllers.modules.AspectLexBuilder.create
			documentsViewRoute: jsRoutes.controllers.modules.AspectLexBuilder.documentsView
			lexiconViewRoute: jsRoutes.controllers.modules.AspectLexBuilder.lexiconView
			lexiconKey: "lexicon"
			corpusKey: "corpus"
	
	$.widget "widgets.aspectLexBuilder", Sare.Widget, widget