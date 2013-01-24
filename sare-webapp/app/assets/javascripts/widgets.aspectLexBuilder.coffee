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
  _create: ->
    @options.lexicon ?= $(@element).data @options.lexiconKey
    @options.corpus ?= $(@element).data @options.corpusKey
    
    if not @options.lexicon?
      @_$(@options.lexicaContainer).children(Selectors.moduleContainer).storeList "option",
        addRoute: jsRoutes.controllers.modules.AspectLexBuilder.create
    
    if @options.corpus?
      # TODO: create the documents widget.
      @_$(@options.documentsContainer)
      
    # TODO: create the aspects tree.
    
    # TODO: create the keywords tree.

  refresh: ->
    $(@element).data @options.widgetKey, @
    
  _init: ->
    @refresh()
    
  _destroy: ->
    
  _setOption: (key, value) ->
    $.Widget.prototype._setOption.apply @, arguments
  
  _getCreateOptions: ->
      lexicaContainer: ".ctr-lexica"
      documentsContainer: ".ctr-documents"
      aspectsContainer: ".ctr-aspects"
      keywordsContainer: ".ctr-keywords"
      lexiconKey: "lexicon"
      corpusKey: "corpus"
      widgetKey: "widget"

$.widget "widgets.aspectLexBuilder", Sare.Widget, widget