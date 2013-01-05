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
    if key is "store"
      store = value
      @_$(@options.list).empty().change()
      if typeof store is "object" and store?.id?
        @options.listRoute(store.id).ajax
          success: (uuids) =>
            if @options.editable
              @_$(@options.addButton).removeAttr "disabled"
            @options.store = store
            uuids = [] if uuids not instanceof window.Array
            for uuid in uuids
              @options.getRoute(store.id, uuid).ajax
                success: (document) =>
                  @_add document
            @_trigger "listPopulate", store, uuids
      else
        @_$(@options.addButton).attr "disabled", true
      
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
        @_$(input).attr "disabled", true for input in @_form "inputs"
      when "enabled"
        if @options.editable
          @_$(input).removeAttr "disabled" for input in @_form "inputs"
      when "inputs"
        [ @options.contentInput, @options.polarityInput, @options.updateButton ]
      when "populate"
        @_$(@options.contentInput).val data?.content
        @_$(@options.polarityInput).val data?.polarity
  
  _create: ->
    @options.editable ?= not not @_$(@options.addButton).length
    if @options.editable then @_on @_$(@options.addButton),
      click: (e) =>
        @_$(@options.addButton).button "loading"
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
            @_$(@options.addButton).button "reset"
    
    if @options.editable then @_on @_$(@options.deleteButton),
      click: (e) =>
        selected = @selected()
        @_$(@options.deleteButton).button "loading"
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
            @_$(@options.deleteButton).button "reset"
            # button reset sets a timeout to enables the button, so this makes sure it's disabled, if necessary
            window.setTimeout(=>
              @_$(@options.list).change()
            , 0)
    
    @_on @_$(@options.list),
      change: (e) =>
        selected = @selected()
        @_form "populate", selected.data
        @_form if selected.data? then "enabled" else "disabled"
        if selected.data?
          @_$(@options.deleteButton).removeAttr "disabled"
        else if @options.editable
          @_$(@options.deleteButton).attr "disabled", true
        @_trigger "selectionChange", e, selected
        
    if @options.editable then @_on @_$(@options.updateButton),
      click: (e) =>
        selected = @selected()
        
        [ content, polarity ] = [
          @_$(@options.contentInput).val()
          Number(@_$(@options.polarityInput).val())
        ]
        
        updated = no
        updatedDoc = selected.data
        updatedDoc.content = (updated = yes; content) if content and content isnt selected.data.content
        updatedDoc.polarity = (updated = yes; polarity) if polarity? and polarity isnt selected.data.polarity
        
        if updated
          @_$(@options.updateButton).button "loading"
          @options.updateRoute(@options.store.id, updatedDoc.id).ajax
              contentType: Helpers.MimeTypes.json
              data: JSON.stringify updatedDoc
              success: (updatedDoc) =>
                @_updateListItem selected.item, updatedDoc
                @_trigger "itemUpdate", e,
                  data: selected.data
                  updatedData: updatedDoc
              complete: =>
                @_$(@options.updateButton).button "reset"
        
        e.preventDefault()

    
    @_form "disabled"
    for control in [@options.addButton, @options.deleteButton]
      @_$(control).attr "disabled", true
    
    # select the first store, if any
    firstItem = @_$(@options.list).children("option:first")
    if firstItem.length
      @_$(@options.list)
        .val($(firstItem).data(@options.dataKey)?.id)
        .change()
      
  _getCreateOptions: ->
      list: ".lst-documents"
      addButton: ".btn-add-doc"
      deleteButton: ".btn-delete-doc"
      contentInput: ".input-doc-content"
      polarityInput: ".input-doc-polarity"
      updateButton: ".btn-update-doc"
      listRoute: jsRoutes.controllers.DocumentsController.list
      getRoute: jsRoutes.controllers.DocumentsController.get
      addRoute: jsRoutes.controllers.DocumentsController.add
      updateRoute: jsRoutes.controllers.DocumentsController.update
      deleteRoute: jsRoutes.controllers.DocumentsController.delete
      dataKey: "document"

$.widget "widgets.documentList", Sare.Widget, widget
