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

widget =
	_create: ->
		@options.lexicon ?= $(@element).data @options.lexiconKey
		@options.corpus ?= $(@element).data @options.corpusKey
		
		storeLists = @_$(@options.lexicaContainer).children(Selectors.moduleContainer)
			.add(@_$(@options.corporaContainer).children Selectors.moduleContainer)
		
		if $(storeLists).length
			refreshView = (e, data) =>
				if $(e.target).closest(@options.lexicaContainer).length
					lexicon = data
				if $(e.target).closest(@options.corporaContainer).length
					corpus = data
				[ lexicon, corpus ]
				
			@_on storeLists,
				storeListSelectionChange: (e, selected) ->
					refreshView e, selected.data
				storeUpdate: (e, data) ->
					refreshView e, data.updatedData
			
			storeLists
					.storeList "option",
						suppressOutput: true
		
	refresh: ->
		$(@element).data Strings.widgetKey, @
		
	_init: ->
		@refresh()
		
	_destroy: ->
		
	_setOption: (key, value) ->
		switch key
			when "disabled"
				# TODO: do something
				a = 0
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		engine: null
		lexicaContainer: ".ctr-lexica"
		corporaContainer: ".ctr-corpora"
		createLexiconRoute: jsRoutes.controllers.modules.AspectLexBuilder.create
		lexiconKey: "lexicon"
		corpusKey: "corpus"

$.widget "widgets.aspectBasedOpinionMiner", Sare.Widget, widget