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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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
Widgets = Page.Widgets

widget =
	goPrev: ->
		if @options.index > 0 then @_navigate --@options.index
	
	goNext: ->
		if @options.index < @options.corpus.size - 1 then @_navigate ++@options.index
	
	_fixButtons: ->
		@_changeInputState @options.prevButton, if @options.index > 0 then "enabled" else "disabled"
		@_changeInputState @options.nextButton, if @options.index < @options.corpus.size - 1 then "enabled" else "disabled" 
	
	_navigate: (index) ->
		index ?= @options.index
		
		@options.getDocumentRoute(@options.corpus.id, @options.lexicon.id, index).ajax
			success: (document) =>
				# TODO: add color to this.
				@_$(@options.documentContainer)
					.empty()
					.html document.content
				@_fixButtons()
	
	_create: ->
		@options.corpus ?= $(@element).data @options.corpusKey
		@options.lexicon ?= $(@element).data @options.lexiconKey
		
		@_on @_$(@options.nextButton),
			click: -> @goNext()
		
		@_on @_$(@options.prevButton),
			click: -> @goPrev()
		
		@_navigate()
    
	refresh: ->
		$(@element).data @options.widgetKey, @
    
	_init: ->
		@refresh()
    
	_destroy: ->
    
	_setOption: (key, value) ->
		$.Widget.prototype._setOption.apply @, arguments
  
	_getCreateOptions: ->
		documentContainer: ".ctr-document"
		prevButton: ".btn-prev-doc"
		nextButton: ".btn-next-doc"
		index: 0
		getDocumentRoute: jsRoutes.controllers.modules.AspectLexBuilder.getDocument
		corpusKey: "corpus"
		lexiconKey: "lexicon"

$.widget "widgets.documentSlider", Sare.Widget, widget