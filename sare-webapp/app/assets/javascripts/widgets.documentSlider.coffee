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
	goPrev: ->
		if @options.index > 0
			@_navigate @options.index - 1
	
	goNext: ->
		if @options.index < @options.corpus.size - 1
			@_navigate @options.index + 1
	
	_fixButtons: ->
		@_changeInputState @options.prevButton, if @options.index > 0 then "enabled" else "disabled"
		@_changeInputState @options.nextButton, if @options.index < @options.corpus.size - 1 then "enabled" else "disabled" 
	
	_navigate: (index, tags, silent) ->
		index ?= @options.index
		if not tags?
			tags = ""
			(tags += (if tags isnt "" then "|" else "") + $(checkbox).val()) for checkbox in @_$(@options.postagCheckbox).filter(":checked")
		
		getDocument = =>
			@options.getDocumentRoute(@options.corpus.id, @options.lexicon.id, tags, index).ajax
				success: (document) =>
					@_$(@options.documentContainer)
						.empty()
						.html(document.enhancedContent ? document.content)
					if not silent then @options.index = (document.rank ? index)
					@_fixButtons()
		
		if index isnt @options.index and not silent
			@options.seeDocumentRoute(@options.corpus.id, @options.lexicon.id, tags, @options.index).ajax
				success: (document) => getDocument()
		else
			getDocument()
	
	_create: ->
		@options.corpus ?= $(@element).data @options.corpusKey
		@options.lexicon ?= $(@element).data @options.lexiconKey
		
		@_on @_$(@options.nextButton),
			click: -> @goNext()
		
		@_on @_$(@options.prevButton),
			click: -> @goPrev()
		
		(events = {})["click #{@options.postagCheckbox}"] = => @_navigate()
		@_on @element, events
		
		@_navigate()
		
	refresh: ->
		$(@element).data Strings.widgetKey, @
		
	_init: ->
		@refresh()
		
	_destroy: ->
		
	_setOption: (key, value) ->
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		documentContainer: ".ctr-document"
		prevButton: ".btn-prev-doc"
		nextButton: ".btn-next-doc"
		postagCheckbox: ".chk-postag"
		index: -1
		getDocumentRoute: jsRoutes.controllers.modules.AspectLexBuilder.getDocument
		seeDocumentRoute: jsRoutes.controllers.modules.AspectLexBuilder.seeDocument
		corpusKey: "corpus"
		lexiconKey: "lexicon"

$.widget "widgets.documentSlider", Sare.Widget, widget