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

widget =
  _setOption: (key, value) ->
    changeState = (state) =>
      @_changeInputState @_$(input), state for input in [ @options.list, @options.addButton ]
      
    if key is "store"
      store = value
      @_$(@options.list).empty().change()
      if typeof store is "object" and store?.id?
        @options.listRoute(store.id).ajax
          success: (uuids) =>
            if @options.editable
              changeState "enabled"
            @options.store = store
            uuids = [] if uuids not instanceof window.Array
            for uuid in uuids
              @options.getRoute(store.id, uuid).ajax
                success: (document) =>
                  @_add document
            @_trigger "listPopulate", store, uuids
          error: ->
            changeState "disabled"
      else
        changeState "disabled"
      
  _add: (document, e) ->
    @_updateListItem(option = $("<option>"), document)
    @_$(@options.list)
      .append option
    @_trigger "itemAdd", e,
      item: option
      data: document
    if @_$(@options.list).children("option").length == 1
      @_$(@options.list)
        .val(document.id)
        .change()
  
  _remove: ->
    if @options.store? then @options.deleteRoute(@options.store.id, $(item).data(@options.dataKey)).ajax
      success: (document) =>
        selected = @selected() if not item?
        next = $(selected.item).next()
        next = $(selected.item).prev() if not next.length
        @_$(@options.list)
          .val($(next).data(@options.dataKey)?.id)
          .change()
        $(selected.item).remove()
        @_trigger "itemRemove", e, selected
  
  selected: ->
    selected = @_$(@options.list).children "option:selected"
    return {
      item: selected
      data: $(selected).data @options.dataKey
    }

  _updateListItem: (item, document) ->
    content = document.summarizedContent ? document.content
    text = if content? and content isnt "" then content else document.id
    $(item)
      .val(document.id)
      .text(text)
      .data @options.dataKey, document

  _form: (option, data) ->
    switch option
      when "disabled"
        @_changeInputState @_$(input), "disabled" for input in @_form "inputs"
      when "enabled"
        if @options.editable
          @_changeInputState @_$(input), "enabled" for input in @_form "inputs"
      when "inputs"
        [ @options.contentInput, @options.updateButton ]
      when "populate"
        @_$(@options.contentInput).val data?.content
  
  _create: ->
    @options.editable ?= not not @_$(@options.addButton).length
    if @options.editable then @_on @_$(@options.addButton),
      click: (e) =>
        @_changeInputState @_$(@options.addButton), "loading"
        @options.addRoute(@options.store.id).ajax
          contentType: Helpers.MimeTypes.json
          data: JSON.stringify
            content: ""
          success: (document) =>
            @_add document, e
            @_$(@options.list)
              .val(document.id)
              .change()
            @_$(@options.contentInput).focus() if @options.editable
          complete: =>
            @_changeInputState @_$(@options.addButton), "reset"
    
    if @options.editable then @_on @_$(@options.deleteButton),
      click: (e) =>
        selected = @selected()
        @_changeInputState @_$(@options.deleteButton), "loading"
        if selected.data? then @options.deleteRoute(@options.store.id, selected.data.id).ajax
          success: (document) =>
            next = $(selected.item).next()
            next = $(selected.item).prev() if not next.length
            @_$(@options.list)
              .val $(next).data(@options.dataKey)?.id
            @_$(@options.list)
              .change()
            $(selected.item).remove()
            @_trigger "itemRemove", e,
              item: selected.item
              data: document
          complete: =>
            @_changeInputState @_$(@options.deleteButton), "reset"
            # button reset sets a timeout to enables the button, so this makes sure it's disabled, if necessary
            window.setTimeout(=>
              @_$(@options.list).change()
            , 0)
    
    @_on @_$(@options.list),
      change: (e) =>
        selected = @selected()
        @_form "populate", selected.data
        @_form if selected.data? and selected.data.isEditable then "enabled" else "disabled"
        if selected.data? and @options.editable
          @_changeInputState @_$(@options.deleteButton), "enabled"
        else
          @_changeInputState @_$(@options.deleteButton), "disabled"
        @_trigger "selectionChange", e, selected
        
    if @options.editable then @_on @_$(@options.updateButton),
      click: (e) =>
        selected = @selected()
        
        content = @_$(@options.contentInput).val()
        
        updated = no
        updatedDoc = selected.data
        updatedDoc.content = (updated = yes; content) if content and content isnt selected.data.content
        
        if updated
          @_changeInputState @_$(@options.updateButton), "loading"
          @options.updateRoute(@options.store.id, updatedDoc.id).ajax
              contentType: Helpers.MimeTypes.json
              data: JSON.stringify updatedDoc
              success: (updatedDoc) =>
                @_updateListItem selected.item, updatedDoc
                @_trigger "itemUpdate", e,
                  data: selected.data
                  updatedData: updatedDoc
              complete: =>
                @_changeInputState @_$(@options.updateButton), "reset"
        
        e.preventDefault()

    @option("store", @options.store)
    #@_form "disabled"
    #@_changeInputState @_$(control), "disabled" for control in [@options.addButton, @options.deleteButton]
    
    @_$(@options.list).tooltip()
    @_$(@options.contentInput).tooltip()
    @_$(@options.updateButton).tooltip()
    
    # select the first store, if any
    firstItem = @_$(@options.list).children("option:first")
    if firstItem.length
      @_$(@options.list)
        .val($(firstItem).data(@options.dataKey)?.id)
        .change()
      
  _destroy: ->
    @_$(@options.list).tooltip "destroy"
    @_$(@options.addButton).tooltip "destroy"
    @_$(@options.deleteButton).tooltip "destroy"
    @_$(@options.contentInput).tooltip "destroy"
    @_$(@options.updateButton).tooltip "destroy"
  
  _getCreateOptions: ->
      store: null
      list: ".lst-documents"
      addButton: ".btn-add-doc"
      deleteButton: ".btn-delete-doc"
      contentInput: ".input-doc-content"
      updateButton: ".btn-update-doc"
      listRoute: jsRoutes.controllers.DocumentsController.list
      getRoute: jsRoutes.controllers.DocumentsController.get
      addRoute: jsRoutes.controllers.modules.CorpusModule.addDocument
      updateRoute: jsRoutes.controllers.modules.CorpusModule.updateDocument
      deleteRoute: jsRoutes.controllers.modules.CorpusModule.deleteDocument
      dataKey: "document"

$.widget "widgets.documentList", Sare.Widget, widget
