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
Widgets = Page.Widgets

widget =
  selected: (value) ->
    if typeof value is "string"
      @_$(@options.list).val(value).change()
    else if value instanceof $ and $(value).is("option") and $(value).is @_$(@options.list).children()
      @_$(@options.list).val($(value).val()).change()
    else if typeof(value) is "object" and value?.id?
      @_$(@options.list).val(value.id).change()

    selected = @_$(@options.list).children "option:selected"
    return {
      item: selected
      data: $(selected).data @options.dataKey
    }
    
  _updateListItem: (item, data) ->
    $(item)
      .val(data.id)
      .text(data.title ? data.id)
      .data @options.dataKey, data

  _create: ->
    @options.editable ?= not not @_$(@options.addButton).length
    
    # handle add button click
    if @options.editable then @_on @_$(@options.addButton),
      click: (e) =>
        e.preventDefault()
        @_changeInputState @_$(@options.addButton), "loading"
        @options.addRoute().ajax
          contentType: Helpers.MimeTypes.json
          data: JSON.stringify
            content: ""
            format: "text"
          success: (store) =>
            @_updateListItem(option = $("<option>"), store)
            @_$(@options.list)
              .append(option)
              .val(store.id)
              .change()
            @_$(@options.detailsButton).click()
            @_trigger "itemAdd", e,
              item: option
              data: store
          complete: =>
            @_changeInputState @_$(@options.addButton), "reset"
    
    @_on @_$(@options.detailsButton),
      click: (e) =>
        @_$(@options.detailsModalOuterContainer).load @options.detailsFormRoute(@selected().data.id).url
        e.preventDefault()

    # TODO: not working for some reason. fix it!
    @_on @_$(@options.detailsModalOuterContainer).children(".modal"),
      storeDetailsUpdate: (e, data) =>
        { updatedStore } = data
        if updatedStore? then @_updateListItem @selected().item, updatedStore
       
    @_on @_$(@options.detailsModalOuterContainer),
      "hidden .modal": =>
        data = @_$(@options.detailsModalOuterContainer).children(".modal").data @options.dataKey
        if data? then @_updateListItem @selected().item, data
        @_$(@options.detailsModalOuterContainer).empty()
    
    # handle delete store button click
    if @options.editable then @_on @_$(@options.deleteButton),
      click: (e) =>
        e.preventDefault()
        selected = @selected()
        @_changeInputState @_$(@options.deleteButton), "loading"
        if selected.data? then @options.deleteRoute(selected.data.id).ajax
          success: (store) =>
            next = $(selected.item).next()
            next = $(selected.item).prev() if not next.length
            if next.length
              @_$(@options.list)
                .val $(next).data(@options.dataKey)?.id
            $(selected.item).remove()
            @_trigger "itemRemove", e,
              item: selected.item
              data: store
          complete: =>
            @_changeInputState @_$(@options.deleteButton), "reset"
            # button reset sets a timeout to enables the button, so this makes sure it's disabled, if necessary
            window.setTimeout(=>
              @_$(@options.list).change()
            , 0)
      
    # handle store list selection change
    @_on @_$(@options.list),
      change: (e) =>
        selected = @selected()
        for input in [ @options.deleteButton, @options.detailsButton ]
          @_changeInputState @_$(input), if selected.data? and @options.editable then "enabled" else "disabled"
        Widgets.moduleManager "option", "output", (selected.data ? null)
        @_trigger "selectionChange", e, selected
    
    for input in [ @options.deleteButton, @options.detailsButton ]
      @_changeInputState @_$(input), "disabled"
    @_changeInputState @_$(@options.addButton), if @options.editable then "enabled" else "disabled"
    
    @_$(@options.list).tooltip()
    
  refresh: ->
    $(@element).data @options.widgetKey, @
    
    if not @selected().item?.length
      # select the first store, if any
      firstItem = @_$(@options.list).children("option:first")
      if firstItem.length
        @_$(@options.list)
          .val($(firstItem).data @options.dataKey)?.id
    
    change = =>
      @_$(@options.list).change()
    window.setTimeout(change, 0)
    
  _init: ->
    @refresh()
    
  _destroy: ->
    for input in [ @options.list, @options.addButton, @options.detailsButton, @options.deleteButton ]
      $(@_$ input).tooltip "destroy"
    
  _setOption: (key, value) ->
    switch key
      when "disabled"
        for input in [ @options.list, @options.addButton, @options.detailsButton, @options.deleteButton ]
          @_changeInputState input, if value then "disabled" else "enabled"
  
  _getCreateOptions: ->
      list: ".lst-store"
      addButton: ".btn-add-store"
      detailsButton: ".btn-store-details"
      detailsModalOuterContainer: ".ctr-store-details-outer"
      deleteButton: ".btn-delete-store"
      addRoute: jsRoutes.controllers.modules.CorpusModule.create
      detailsFormRoute: jsRoutes.controllers.CollectionsController.detailsForm
      deleteRoute: jsRoutes.controllers.CollectionsController.delete
      dataKey: "store"
      widgetKey: "widget"

$.widget "widgets.storeList", Sare.Widget, widget