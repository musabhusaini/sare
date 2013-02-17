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
    
    makeNode = (aspect) ->
      node =
        data: aspect.title
        children: if aspect.children? then (makeNode(child) for child in (aspect.children)) else null
        metadata:
          aspect: aspect
      node.state = "closed" if node.children?
      node

    @_$(@options.aspectsContainer).on "rename.jstree", (e, data) =>
      $.jstree.rollback data.rlbk
    
    addAspect = (parent, title) =>
      lexicon = parent ? $(@options.aspectsContainer).jstree("get_selected").data(@options.aspectKey) ? @options.lexicon
      @options.addAspectRoute(lexicon.id).ajax
        data: title
        success: (aspect) =>
          @_$(@options.aspectsContainer).jstree "create", null, "inside", makeNode aspect
    
    removeAspect = (node) =>
      node = node ? $(@options.aspectsContainer).jstree "get_selected"
      aspect = $(node).data @options.aspectKey
      @options.deleteAspectRoute("null", aspect.id).ajax
        success: =>
          @_$(@options.aspectsContainer).jstree "remove", null

    @_$(@options.aspectsContainer).jstree
      json_data:
        data: makeNode(@options.lexicon).children
        ajax:
          url: (node) =>
            @options.getLexiconRoute($(node).data(@options.aspectKey).id).url
          success: (data) =>
            makeNode(data).children
      plugins: [ "themes", "json_data", "ui", "crrm", "dnd" ]
    
    @_$(@options.addAspectButton).click => addAspect()
    
    @_$(@options.deleteAspectButton).click => removeAspect()
    
    @_$(@options.updateAspectButton).click =>
      @_$(@options.aspectsContainer).jstree "rename", null
    
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
      updateAspectButton: ".btn-update-aspect"
      deleteAspectButton: ".btn-delete-aspect"
      getLexiconRoute: jsRoutes.controllers.CollectionsController.get
      addAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.addAspect
      updateAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.updateAspect
      deleteAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.deleteAspect
      lexiconKey: "lexicon"
      aspectKey: "aspect"
      widgetKey: "widget"

$.widget "widgets.aspectLexicon", Sare.Widget, widget