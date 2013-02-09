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
    
    @_$(@options.aspectsContainer).jstree
      json_data:
        progressive_render: true
        data: @options.lexicon.children ? []
        ajax:
          url: (node) =>
            @options.getLexiconRoute(node.aspect.id)
          success: (data) =>
            data: data.title
            children: data.children
            aspect: data
      plugins: [ "themes", "json_data", "ui", "crrm", "dnd" ]
    
    @_$(@options.aspectsContainer).on "before.jstree", (e, data) =>
      switch data.func
        when "create"
          debugger
    
    @_$(@options.addAspectButton).click =>
      @_$(@options.aspectsContainer).jstree "create", null, "inside"
    
    @_$(@options.deleteAspectButton).click =>
      @_$(@options.aspectsContainer).jstree "remove", null
    
    @_$(@options.keywordsContainer).jstree
      json_data:
        data: []
      plugins: [ "themes", "json_data", "ui", "crrm", "dnd" ]

  refresh: ->
    $(@element).data @options.widgetKey, @
    
  _init: ->
    @refresh()
    
  _destroy: ->
    
  _setOption: (key, value) ->
    $.Widget.prototype._setOption.apply @, arguments
  
  _getCreateOptions: ->
      aspectsContainer: ".ctr-aspects"
      keywordsContainer: ".ctr-keywords"
      addAspectButton: ".btn-add-aspect"
      deleteAspectButton: ".btn-delete-aspect"
      getLexiconRoute: jsRoutes.controllers.CollectionsController.get
      lexiconKey: "lexicon"
      widgetKey: "widget"

$.widget "widgets.aspectLexicon", Sare.Widget, widget