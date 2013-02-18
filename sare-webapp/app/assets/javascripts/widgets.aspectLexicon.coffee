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
    
    editAspectInputs = [ @options.deleteAspectButton, @options.updateAspectButton ]
    
    makeAspectNode = (aspect) ->
      node =
        data: aspect.title
        children: if aspect.children? then (makeAspectNode(child) for child in (aspect.children)) else null
        metadata:
          aspect: aspect
      node.state = "closed" if node.children?
      node

    @_$(@options.aspectsContainer).on "rename_node.jstree", (e, data) =>
      aspect = $(data.rslt.obj).data @options.aspectKey
      @options.updateAspectRoute("null", aspect.id).ajax
        contentType: Helpers.ContentTypes.json
        data: JSON.stringify
          title: data.rslt.name
        success: (aspect) =>
          $(data.rslt.obj).data @options.aspectKey, aspect
        error: ->
          $.jstree.rollback data.rlbk
    
    @_$(@options.aspectsContainer).on "move_node.jstree", (e, data) =>
      aspect = $(data.rslt.o).data @options.aspectKey
      lexicon = $(data.rslt.np).data(@options.aspectKey) ? @options.lexicon
      @options.updateAspectRoute(lexicon.id, aspect.id).ajax
        success: (aspect) =>
          $(data.rslt.o).data @options.aspectKey, aspect
        error: ->
          $.jstree.rollback data.rlbk
    
    @_$(@options.aspectsContainer).on "select_node.jstree", (e, data) =>
      @_changeInputState(input, "enabled") for input in editAspectInputs
    
    @_$(@options.aspectsContainer).on "deselect_node.jstree", (e, data) =>
      @_changeInputState(input, "disabled") for input in editAspectInputs
      
    addAspect = (parent, title) =>
      lexicon = parent ? $(@options.aspectsContainer).jstree("get_selected").data(@options.aspectKey) ? @options.lexicon
      aspect = if title? then { title: title } else {}
      @options.addAspectRoute(lexicon.id).ajax
        contentType: Helpers.ContentTypes.json
        data: JSON.stringify aspect
        success: (aspect) =>
          node = @_$ @options.aspectsContainer if not $(@options.aspectsContainer).jstree("get_selected").length
          @_$(@options.aspectsContainer).jstree "create", node ? null, "inside", makeAspectNode aspect
    
    removeAspect = (node) =>
      node = node ? $(@options.aspectsContainer).jstree "get_selected"
      aspect = $(node).data @options.aspectKey
      @options.deleteAspectRoute("null", aspect.id).ajax
        success: =>
          @_$(@options.aspectsContainer).jstree "remove", null

    @_$(@options.aspectsContainer).jstree
      ui:
        select_limit: 1
      json_data:
        data: makeAspectNode(@options.lexicon).children ? []
        ajax:
          url: (node) =>
            @options.getLexiconRoute($(node).data(@options.aspectKey).id).url
          success: (data) =>
            makeAspectNode(data).children
      plugins: [ "themes", "json_data", "ui", "crrm", "dnd", "sort" ]
    
    @_$(@options.addAspectButton)
      .tooltip()
      .click => addAspect()
    
    @_$(@options.deleteAspectButton)
      .tooltip()
      .click => removeAspect()
    
    @_$(@options.updateAspectButton)
      .tooltip()
      .click =>
        @_$(@options.aspectsContainer).jstree "rename", null
    
    @_changeInputState(input, "disabled") for input in editAspectInputs
    
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