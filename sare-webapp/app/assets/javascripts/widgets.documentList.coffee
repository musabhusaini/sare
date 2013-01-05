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
      if typeof store is "object" and store.id?
        @options.listRoute(store.id).ajax
          success: (uuids) =>
            if @options.editable
              @_$(@options.addButton).removeAttr "disabled"
            @options.store = store
            uuids = [] if uuids not instanceof window.Array
            @_add(uuid) for uuid in uuids
            @_trigger "listPopulate", store, uuids
      else
        @_$(@options.addButton).attr "disabled", true
      
  _add: (uuid, e) ->
    if @options.store? then @options.getRoute(@options.store.id, uuid).ajax
      success: (document) =>
        @_updateListItem(option = $("<option>"), document)
        @_$(@options.list)
          .append option
        @_trigger "itemAdd", e,
          item: option
          data: document
        if @_$(@options.list).children("option").length == 1
          @_$(@options.list)
            .val(data.id)
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
    $(item)
      .val(document.id)
      .text(document.summarizedContent ? document.id)
      .data @options.dataKey, document

  _form: (option, data) ->
      
  _create: ->
    @options.editable ?= not not @_$(@options.addButton).length
    if @options.editable then @_on @_$(@options.addButton),
      click: (e) =>
    
    if @options.editable then @_on @_$(@options.deleteButton),
      click: (e) =>
    
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
    
    for control in [@options.addButton, @options.deleteButton]
      @_$(control).attr "disabled", true
      
  _getCreateOptions: ->
      list: ".lst-documents"
      addButton: ".btn-add-doc"
      deleteButton: ".btn-delete-doc"
      listRoute: jsRoutes.controllers.DocumentsController.list
      getRoute: jsRoutes.controllers.DocumentsController.get
      addRoute: jsRoutes.controllers.DocumentsController.add
      updateRoute: jsRoutes.controllers.DocumentsController.update
      deleteRoute: jsRoutes.controllers.DocumentsController.delete
      dataKey: "document"

$.widget "widgets.documentList", Sare.Widget, widget
