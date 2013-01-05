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
plupload = window.plupload
Sare = window.Sare
Helpers = Sare.Helpers

widget =
  _setOption: (key, value) ->

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

  _form: (option, data) ->
    switch option
      when "disabled"
        @_$(input).attr("disabled", true) for input in @_form "inputs"
        @_$(@options.dropFileContainer).text @options.uploadFileMessage
        @_destroyUploader()
      when "enabled"
        if @options.editable
          @_$(input).removeAttr("disabled") for input in @_form "inputs"
          @_$(@options.dropFileContainer).text @options.dropFileMessage
          @_createUploader()
      when "inputs"
        [ @options.titleInput, @options.descriptionInput, @options.languageList, @options.browseButton, @options.updateButton ]
      when "populate"
        @_$(@options.titleInput).val data?.title
        @_$(@options.descriptionInput).val data?.description
        if data?.language
          @_$(@options.languageList).val data.language

  _uploader: null
  
  # function to set up the uploader and start it
  _createUploader: ->
    return null if @_uploader?
    
    Math = window.Math
    rand = Math.round(Math.random() * 1000)
    if not @_$(@options.uploadContainer).attr("id")?
      @_$(@options.uploadContainer).attr "id", "ctr-upload-" + rand
    if not @_$(@options.browseButton).attr("id")?
      @_$(@options.browseButton).attr "id", "btn-browse-" + rand
    if not @_$(@options.dropFileContainer).attr("id")?
      @_$(@options.dropFileContainer).attr "id", "ctr-dropfile-" + rand
    
    # initialize the uploader
    uploader = @_uploader = new plupload.Uploader
      runtimes: "html5,html4"
      container: @_$(@options.uploadContainer).attr "id"
      browse_button: @_$(@options.browseButton).attr "id"
      drop_element: @_$(@options.dropFileContainer).attr "id"
      file_data_name: @options.filenameKey
      max_file_size: "10mb"
      max_file_count: @options.uploadFileCount
      url: jsRoutes.controllers.CollectionsController.create().url
  
    defaults =
      text: @options.dropFileMessage
    dragenter = =>
      defaults.text = @_$(@options.dropFileContainer).text()
      @_$(@options.dropFileContainer).text @options.dragFileMessage
      @_$(@options.dropFileContainer).addClass @options.acceptingFilesClass
    dragleave = =>
      @_$(@options.dropFileContainer).text defaults.text
      @_$(@options.dropFileContainer).removeClass @options.acceptingFilesClass
  
    # start the uploader
    uploader.init()
    
    # bind various events to the uploader
    uploader.bind "BeforeUpload", (up, file) =>
      @_trigger "uploadStart", up, file
    
    uploader.bind "FilesAdded", (up, files) =>
      dragleave()
      if files.length
        # enforce the file limit so that last files take precedence
        up.removeFile file for file in up.files[...(up.files.length - @options.uploadFileCount)]
        @_$(@options.dropFileContainer).text files[0].name
        @_$(@options.updateButton).removeAttr "disabled"
    
    uploader.bind "UploadProgress", (up, file) =>
      if file.percent
        @_trigger "uploadProgress", up, file
  
    uploader.bind "Error", (up, error) =>
      @_$(@options.dropFileContainer).text @options.uploadFailedMessage
      @_trigger "uploadError", up, error
  
    uploader.bind "FileUploaded", (up, file, response) =>
      up.removeFile file
      @_$(@options.dropFileContainer).text @options.dropFileMessage
      selected = @selected()
      store = JSON.parse response.response
      @_updateListItem selected.item, store
      @_trigger "fileUpload", up,
        file: file
        data: store
    
    uploader.bind "UploadComplete", (up, files) =>
      @_trigger "uploadComplete", up, files
    
    if uploader.runtime is "html5"
      @_$(@options.dropFileContainer).on "dragenter", dragenter
      @_$(@options.dropFileContainer).on "dragleave", dragleave
      
      uploader.bind "Destroy", (up) =>
        @_$(@options.dropFileContainer).off "dragenter", dragenter
        @_$(@options.dropFileContainer).off "dragleave", dragleave

  _destroyUploader: ->
    @_uploader?.stop()
    @_uploader?.destroy()
    @_uploader = null

  _create: ->
    @options.editable ?= not not @_$(@options.addButton).length
    
    # handle add button click
    if @options.editable then @_on @_$(@options.addButton),
      click: (e) =>
        @_$(@options.addButton).button "loading"
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
            @_$(@options.titleInput).focus() if @options.editable
            @_trigger "itemAdd", e,
              item: option
              data: store
          complete: =>
            @_$(@options.addButton).button "reset"
    
    # handle delete store button click
    if @options.editable then @_on @_$(@options.deleteButton),
      click: (e) =>
        selected = @selected()
        @_$(@options.deleteButton).button "loading"
        if selected.data? then @options.deleteRoute(selected.data.id).ajax
          success: (store) =>
            next = $(selected.item).next()
            next = $(selected.item).prev() if not next.length
            @_$(@options.list)
              .val $(next).data(@options.dataKey)?.id
            @_$(@options.list)
              .change()
            $(selected.item).remove()
            @_trigger "itemRemove", e,
              item: selected.item
              data: store
          complete: =>
            @_$(@options.deleteButton).button "reset"
            # button reset sets a timeout to enables the button, so this makes sure it's disabled, if necessary
            window.setTimeout(=>
              @_$(@options.list).change()
            , 0)
      
    # handle store list selection change
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
    
    # handle update button click
    if @options.editable then @_on @_$(@options.updateButton),
      click: (e) =>
        selected = @selected()
        updateStore = (store, updatedStore) =>
          triggerEvent = =>
            @_trigger "itemUpdate", e,
              store: store
              updatedStore: updatedStore

          [title, description, language] = [
            @_$(@options.titleInput).val()
            @_$(@options.descriptionInput).val()
            @_$(@options.languageList).val()
          ]
          
          updatedStore = updatedStore ? store
          updated = no
          updateOptions = updatedStore
          updateOptions.title = (updated = yes; title) if title and title isnt store.title
          updateOptions.description = (updated = yes; description) if description and description isnt store.description
          updateOptions.language = (updated = yes; language) if language and language isnt store.language
          updateOptions =
            details: updateOptions
          
          if updated
            @options.updateRoute(updatedStore.id).ajax
              contentType: Helpers.MimeTypes.json
              data: JSON.stringify updateOptions
              success: (updatedStore) =>
                @_updateListItem selected.item, updatedStore
                triggerEvent()
              complete: =>
                @_$(@options.updateButton).button "reset"
          else
            @_updateListItem selected.item, updatedStore
            @_form "populate", updatedStore
            @_$(@options.updateButton).button "reset"
            triggerEvent()
        
        @_$(@options.updateButton).button "loading"
        if @_uploader?.files.length
          @_uploader.settings.url = jsRoutes.controllers.CollectionsController.update(selected.data?.id).url
          uploadComplete = (up, file, response) =>
            updatedStore = JSON.parse response.response
            @_uploader.unbind "FileUploaded", uploadComplete
            updateStore selected.data, updatedStore
          @_uploader.bind "FileUploaded", uploadComplete
          @_uploader.start()
        else updateStore selected.data
        
        e.preventDefault()

    @_$(@options.deleteButton).attr "disabled", true
    if not @options.editable then @_$(@options.addButton).attr "disabled", true
    @_form "disabled"
    
    # select the first store, if any
    firstItem = @_$(@options.list).children("option:first")
    if firstItem.length
      @_$(@options.list)
        .val($(firstItem).data(@options.dataKey)?.id)
        .change()
    
  _getCreateOptions: ->
      list: ".lst-store"
      addButton: ".btn-add-store"
      deleteButton: ".btn-delete-store"
      titleInput: ".input-store-title"
      descriptionInput: ".input-store-desc"
      languageList: ".lst-store-language"
      uploadContainer: ".ctr-store-upload"
      dropFileContainer: ".ctr-store-dropfile"
      browseButton: ".btn-store-browse"
      updateButton: ".btn-store-update"
      addRoute: jsRoutes.controllers.CollectionsController.create
      deleteRoute: jsRoutes.controllers.CollectionsController.delete
      updateRoute: jsRoutes.controllers.CollectionsController.update
      uploadFileCount: 1
      dataKey: "store"
      filenameKey: "corpus"
      uploadFileMessage: "Upload file"
      dropFileMessage: "Drop file or browse"
      dragFileMessage: "Almost there, just let go now!"
      uploadFailedMessage: "Something's amiss! Please try again"
      acceptingFilesClass: "accepting-files"

$.widget "widgets.storeList", Sare.Widget, widget