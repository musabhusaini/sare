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
		if @options.rank > 0
			@_navigate @options.rank - 1
	
	goNext: ->
		if @options.rank < @options.corpus.size - 1
			@_navigate @options.rank + 1
	
	_fixButtons: ->
		@_changeInputState @options.prevButton, if @options.rank > 0 then "enabled" else "disabled"
		@_changeInputState @options.nextButton, if @options.rank < @options.corpus.size - 1 then "enabled" else "disabled" 
	
	_fixRank: ->
		@_$(@options.rankText).val "#{@options.rank + 1} / #{@options.corpus.size}"
	
	_navigate: (rank, tags, silent, callback) ->
		rank ?= @options.rank
		if not tags?
			tags = ""
			(tags += (if tags isnt "" then "|" else "") + $(checkbox).val()) for checkbox in @_$(@options.postagCheckbox).filter(":checked")
		
		getDocument = =>
			@options.getDocumentRoute(@options.corpus.id, @options.lexicon.id, tags, rank).ajax
				success: (document) =>
					@_$(@options.documentContainer)
						.empty()
						.html(document.enhancedContent ? document.content)
					if not silent
						@options.rank = (document.rank ? rank)
						if document.corpus? then @options.corpus = document.corpus
						@_fixRank()
					@_fixButtons()
					callback?(document)
		
		if rank isnt @options.rank and not silent
			@options.seeDocumentRoute(@options.corpus.id, @options.lexicon.id, tags, @options.rank).ajax
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
		
		@_on @_$(@options.rankText),
			focus: (e) ->
				$(e.target).val @options.rank + 1
			blur: (e) ->
				@_fixRank()
			keydown: (e) ->
				exit = ->
					$(e.target).blur()
				cancel = =>
					@_fixRank()
					exit()
				
				# handle enter key
				if e.which is 13
					rank = window.parseInt $(e.target).val()
					if window.isNaN(rank) or rank.toString() isnt $(e.target).val() or rank is @options.rank + 1 or rank < 1 or rank > @options.corpus.size
						cancel()
						return false
					@_navigate rank - 1
					exit()
				else if e.which is 27
					# handle escape key
					cancel()
				else if event.ctrlKey or 112 <= event.which <= 123 or event.which is 46 or event.which is 8 or 35 <= event.which <= 39
					# allow ctrl+[x], f[x], backspace, delete, home, end, left, right
        	return true
        else if (event.shiftKey or (event.which < 48 or event.which > 57) and (event.which < 96 or event.which > 105))
        	# if it is not a number, stop the keypress. 
        	return false
        return true
		
		(events = {})["click #{@options.emphasizedTokenButton}"] = (e) =>
			lexiconContainer = $(@element).parent().siblings(@options.lexiconContainer).first().children(".ctr-module").first()
			lexiconWidget = $(lexiconContainer).data Strings.widgetKey
			aspect = lexiconWidget?.getSelectedAspect?().data
			if aspect?
				lexiconWidget.addKeyword null, $(e.target).data(@options.lemmaKey), false, true
				$(e.target)
					.data(@options.aspectKey, aspect)
					.removeClass(@options.newTokenClass)
					.addClass @options.keywordTokenClass
		
		@_on @_$(@options.documentContainer), events
		
		(events = {})["click #{@options.postagCheckbox}"] = => @_navigate()
		@_on @element, events
		
		@_$(@options.rankText).tooltip()
		@_$(@options.postagCheckbox).parent().tooltip()
		
		@_navigate()
		
	refresh: ->
		$(@element).data Strings.widgetKey, @
		
	_init: ->
		@refresh()
		
	_destroy: ->
		
	_setOption: (key, value) ->
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		rankText: ".txt-rank"
		documentContainer: ".ctr-document"
		prevButton: ".btn-prev-doc"
		nextButton: ".btn-next-doc"
		emphasizedTokenButton: ".emphasized-token"
		postagCheckbox: ".chk-posTag"
		lexiconContainer: ".ctr-alex"
		newTokenClass: "btn-info"
		keywordTokenClass: "btn-success"
		rank: -1
		getDocumentRoute: jsRoutes.controllers.modules.AspectLexBuilder.getDocument
		seeDocumentRoute: jsRoutes.controllers.modules.AspectLexBuilder.seeDocument
		corpusKey: "corpus"
		lexiconKey: "lexicon"
		aspectKey: "aspect"
		lemmaKey: "lemma"

$.widget "widgets.documentSlider", Sare.Widget, widget