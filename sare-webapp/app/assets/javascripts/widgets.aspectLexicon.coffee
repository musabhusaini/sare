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
  _makeAspectNode: (aspect) ->
    node =
      data: aspect.title
      children: if aspect.children? then (@_makeAspectNode(child) for child in (aspect.children)) else null
      metadata:
        aspect: aspect
    node.state = "closed" if node.children?
    node

  _makeKeywordNode: (keyword) ->
    data: keyword.content
    metadata:
      keyword: keyword

  getSelectedAspectNode: ->
    @_$(@options.aspectsContainer).jstree "get_selected"
    
  getSelectedAspect: ->
    @getSelectedAspectNode().data @options.aspectKey
  
  addAspect: (lexicon, title) ->
    lexicon ?= @getSelectedAspect() ? @options.lexicon
    aspect = title: (title ? null)
    @options.addAspectRoute(lexicon.id).ajax
      contentType: Helpers.ContentTypes.json
      data: JSON.stringify aspect
      success: (aspect) =>
        node = @_$(@options.aspectsContainer) if not @getSelectedAspectNode().length
        @_$(@options.aspectsContainer).jstree "create", node ? null, "inside", @_makeAspectNode aspect

  removeAspect: (node) ->
    node = node ? @getSelectedAspectNode()
    aspect = $(node).data @options.aspectKey
    @options.deleteAspectRoute("null", aspect.id).ajax
      success: =>
        @_$(@options.aspectsContainer).jstree "remove", null

  getSelectedKeywordNode: ->
    @_$(@options.keywordsContainer).jstree "get_selected"
  
  getSelectedKeyword: ->
    @getSelectedKeywordNode().data @options.keywordKey
  
  addKeyword: (aspect, content) ->
    aspect ?= @getSelectedAspect()
    keyword = content: (content ? null)
    @options.addKeywordRoute(aspect.id).ajax
      contentType: Helpers.ContentTypes.json
      data: JSON.stringify keyword
      success: (keyword) =>
        @_$(@options.keywordsContainer).jstree "create",
          @_$(@options.keywordsContainer), "inside", @_makeKeywordNode keyword
  
  removeKeyword: (node) ->
    node = node ? @getSelectedKeywordNode()
    keyword = $(node).data @options.keywordKey
    @options.deleteKeywordRoute("null", keyword.id).ajax
      success: =>
        @_$(@options.keywordsContainer).jstree "remove", null
  
  _create: ->
    @options.lexicon ?= $(@element).data @options.lexiconKey
    
    editAspectInputs = [ @options.updateAspectButton, @options.deleteAspectButton ]
    (aspectInputs = editAspectInputs.slice()).push @options.addAspectButton
    
    editKeywordInputs = [ @options.updateKeywordButton, @options.deleteKeywordButton ]
    (keywordInputs = editKeywordInputs.slice()).push @options.addKeywordButton
    
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
      @_changeInputState(input, "enabled") for input in keywordInputs
      @_changeInputState(input, "disabled") for input in editKeywordInputs
      
      aspect = $(data.rslt.obj).data @options.aspectKey
      @_$(@options.keywordsContainer).jstree "delete_node", @_$(@options.keywordsContainer).find("li")
      @options.getKeywordsRoute(aspect.id).ajax
        success: (keywords) =>
          for keyword in keywords
            @_$(@options.keywordsContainer).jstree "create",
              @_$(@options.keywordsContainer), "last", @_makeKeywordNode(keyword), null, true
    
    @_$(@options.aspectsContainer).on "deselect_node.jstree", (e, data) =>
      @_changeInputState(input, "disabled") for input in editAspectInputs
      @_changeInputState(input, "disabled") for input in keywordInputs
      @_$(@options.keywordsContainer).jstree "delete_node", @_$(@options.keywordsContainer).find("li")
      
    @_$(@options.aspectsContainer).jstree
      ui:
        select_limit: 1
      json_data:
        data: @_makeAspectNode(@options.lexicon).children ? []
        ajax:
          url: (node) =>
            @options.getLexiconRoute($(node).data(@options.aspectKey).id).url
          success: (data) =>
            @_makeAspectNode(data).children
      plugins: [ "themes", "json_data", "ui", "crrm", "dnd", "sort" ]
    
    @_$(@options.addAspectButton)
      .tooltip()
      .click => @addAspect()
    
    @_$(@options.updateAspectButton)
      .tooltip()
      .click =>
        @_$(@options.aspectsContainer).jstree "rename", null

    @_$(@options.deleteAspectButton)
      .tooltip()
      .click => @removeAspect()
    
    @_changeInputState(input, "disabled") for input in editAspectInputs

    @_$(@options.keywordsContainer).on "rename_node.jstree", (e, data) =>
      keyword = $(data.rslt.obj).data @options.keywordKey
      @options.updateKeywordRoute("null", keyword.id).ajax
        contentType: Helpers.ContentTypes.json
        data: JSON.stringify
          content: data.rslt.name
        success: (keyword) =>
          $(data.rslt.obj).data @options.keywordKey, keyword
        error: ->
          $.jstree.rollback data.rlbk
    
    @_$(@options.keywordsContainer).on "select_node.jstree", (e, data) =>
      @_changeInputState(input, "enabled") for input in keywordInputs
    
    @_$(@options.keywordsContainer).on "deselect_node.jstree", (e, data) =>
      @_changeInputState(input, "disabled") for input in keywordInputs
    
    @_$(@options.keywordsContainer).jstree
      crrm:
        move:
          check_move: -> false
      themes:
        dots: false
        icons: false
      ui:
        select_limit: 1
      json_data:
        data: []
      plugins: [ "themes", "json_data", "ui", "crrm", "dnd", "sort" ]
    
    @_$(@options.addKeywordButton)
      .tooltip()
      .click => @addKeyword()
    
    @_$(@options.deleteKeywordButton)
      .tooltip()
      .click => @removeKeyword()
      
    @_changeInputState(input, "disabled") for input in keywordInputs
    
  refresh: ->
    $(@element).data @options.widgetKey, @
    
  _init: ->
    @refresh()
    
  _destroy: ->
    
  _setOption: (key, value) ->
    $.Widget.prototype._setOption.apply @, arguments
  
  _getCreateOptions: ->
      aspectsContainer: ".ctr-aspects"
      addAspectButton: ".btn-add-aspect"
      updateAspectButton: ".btn-update-aspect"
      deleteAspectButton: ".btn-delete-aspect"
      keywordsContainer: ".ctr-keywords"
      addKeywordButton: ".btn-add-keyword"
      updateKeywordButton: ".btn-update-keyword"
      deleteKeywordButton: ".btn-delete-keyword"
      getLexiconRoute: jsRoutes.controllers.CollectionsController.get
      addAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.addAspect
      updateAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.updateAspect
      deleteAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.deleteAspect
      getKeywordsRoute: jsRoutes.controllers.modules.AspectLexBuilder.getExpressions
      addKeywordRoute: jsRoutes.controllers.modules.AspectLexBuilder.addExpression
      updateKeywordRoute: jsRoutes.controllers.modules.AspectLexBuilder.updateExpression
      deleteKeywordRoute: jsRoutes.controllers.modules.AspectLexBuilder.deleteExpression
      lexiconKey: "lexicon"
      aspectKey: "aspect"
      keywordKey: "keyword"
      widgetKey: "widget"

$.widget "widgets.aspectLexicon", Sare.Widget, widget