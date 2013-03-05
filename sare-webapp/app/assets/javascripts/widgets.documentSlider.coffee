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
Images = Page.Images

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
		oldRank = @options.rank
		rank ?= @options.rank
		if @options.corpus.size < 1 or rank > @options.corpus.size then return
		
		if not tags?
			tags = ""
			(tags += (if tags isnt "" then "|" else "") + $(checkbox).val()) for checkbox in @_$(@options.postagCheckbox).filter(":checked")
		
		getDocument = =>
			enableControls = =>
				if not silent
					for input in [ @options.prevButton, @options.rankText, @options.nextButton ]
						@_changeInputState @_$(input), "enabled"
			
			@options.getDocumentRoute(@options.corpus.id, @options.lexicon.id, tags, rank).ajax
				success: (document) =>
					options = @options
					@_$(@options.documentContainer)
						.empty()
						.html(document.enhancedContent ? document.content)
					@_$(@options.documentContainer)
						.tooltip
							selector: @options.emphasizedTokenButton
							title: ->
								aspect = $(@).data options.aspectKey
								isNew = $(@).hasClass options.newTokenClass
								if aspect?
									return "Already a keyword under '#{aspect.title}'"
								else if isNew
									lemma = $(@).data options.lemmaKey
									return "Click to add '#{lemma.toLowerCase()}' to the current aspect"
								else
									return "Already seen and ignored"
					if not silent
						@options.rank = (document.rank ? rank)
						if document.corpus? then @options.corpus = document.corpus
						@_fixRank()
					@_fixButtons()
					enableControls()
					callback?(document)
				error: =>
					if rank < 0 and @options.corpus?.size?
						# we have no next document, so we go to the last one instead.
						@_navigate @options.corpus.size - 1
					else
						# otherwise navigate to the old rank or 0 if the old rank was also negative
						@_navigate if oldRank >= 0 then oldRank else 0
		
		if not silent
			@_$(@options.documentContainer)
				.empty()
				.html $("<img>").attr "src", Images.wait
			for input in [ @options.prevButton, @options.rankText, @options.nextButton ]
				@_changeInputState @_$(input), "disabled"
		
		if @options.rank >= 0 and rank isnt @options.rank and not silent
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
				window.setTimeout ->
					$(e.target).select()
				, 0
			blur: (e) ->
				@_fixRank()
			keydown: (e) ->
				exit = ->
					$(e.target).blur()
				cancel = =>
					@_fixRank()
					exit()
				
				# handle enter key.
				if e.which is 13
					rank = window.parseInt $(e.target).val()
					if window.isNaN(rank) or rank.toString() isnt $(e.target).val() or rank is @options.rank + 1 or rank < 1 or rank > @options.corpus.size
						cancel()
						return false
					@_navigate rank - 1, null, false, => exit()
					return true
				else if e.which is 27
					# handle escape key.
					cancel()
				else if event.ctrlKey or 112 <= event.which <= 123 or event.which is 9 or event.which is 46 or event.which is 8 or 35 <= event.which <= 39
					# allow ctrl+[x], f[x], tab, backspace, delete, home, end, left, right.
        	return true
        else if (event.shiftKey or (event.which < 48 or event.which > 57) and (event.which < 96 or event.which > 105))
        	# if it is not a number, stop the keypress. 
        	return false
        return true
		
		lexiconParentContainer = =>
			$(@element).parent().siblings(@options.lexiconContainer).first()
		lexiconWidget = =>
			$(lexiconParentContainer().children(".ctr-module").first()).data Strings.widgetKey
		
		findKeywordButton = (keyword) =>
			lemmaKey = @options.lemmaKey
			@_$(@options.emphasizedTokenButton).filter ->
				($(@).data(lemmaKey) ? $(@).text()).toLowerCase() is (keyword.content ? keyword).toLowerCase()
		
		makeKeywordButton = (keyword) =>
			$(findKeywordButton keyword)
				.removeClass(@options.newTokenClass)
				.addClass @options.keywordTokenClass
		
		unmakeKeywordButton = (keyword) =>
			$(findKeywordButton keyword).removeClass @options.keywordTokenClass
			
		@_on $(lexiconParentContainer()),
			aspectLexiconKeywordAdded: (e, keyword) ->
				makeKeywordButton keyword
				true
			aspectLexiconKeywordRemoved: (e, keyword) ->
				widget = lexiconWidget()
				if not widget.hasKeyword widget.getLexicon(), keyword, true
					unmakeKeywordButton keyword
				true
			aspectLexiconKeywordRenamed: (e, data) ->
				widget = lexiconWidget()
				{ keyword, result } = data
				if not widget.hasKeyword widget.getLexicon(), keyword, true
					unmakeKeywordButton keyword
				makeKeywordButton result
				true
		
		(events = {})["click #{@options.emphasizedTokenButton}"] = (e) ->
			widget = lexiconWidget()
			aspect = widget?.getSelectedAspect?().data
			if aspect?
				widget.addKeyword null, $(e.target).data(@options.lemmaKey), false, true
				$(e.target)
					.data(@options.aspectKey, aspect)
					.removeClass(@options.newTokenClass)
					.addClass @options.keywordTokenClass
		
		@_on @_$(@options.documentContainer), events
		
		(events = {})["click #{@options.postagCheckbox}"] = => @_navigate()
		@_on @element, events
		
		@_$(@options.rankText).tooltip
			title: =>
				return "Document #{@options.rank + 1} of #{@options.corpus.size}"
		
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
		documentContainer: ".ctr-document"
		prevButton: ".btn-prev-doc"
		nextButton: ".btn-next-doc"
		rankText: ".txt-rank"
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