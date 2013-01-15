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
  _form: (option, data) ->
    switch option
      when "disabled"
        @_changeInputState @_$(input), "disabled" for input in @_form "inputs"
        @_$(@options.dropFileContainer)
          .text(@options.uploadFileMessage)
          .tooltip "destroy"
        @_destroyUploader()
      when "enabled"
        @_changeInputState @_$(input), "enabled" for input in @_form "inputs"
        if not @options.isDerived
          @_$(@options.dropFileContainer)
            .text(@options.dropFileMessage)
            .tooltip
              title: @options.dropFileTip
          @_createUploader()
      when "inputs"
        inputs = [ @options.titleInput, @options.descriptionInput, @options.languageList ]
        inputs.push(@options.browseButton) if not @options.isDerived
        inputs
      when "populate"
        @_$(@options.titleInput).val data?.title
        @_$(@options.descriptionInput).val data?.description
        if data?.language
          @_$(@options.languageList).val data.language

  _uploader: null
  
  _getUpdated: (store) ->
    store ?= @options.store 
    { title, description, language } = updatedStore = {
      title: @_$(@options.titleInput).val()
      description: @_$(@options.descriptionInput).val()
      language: @_$(@options.languageList).val()
    }
    
    updated = no
    updated = yes if title and title isnt store?.title
    updated = yes if description and description isnt store?.description
    updated = yes if language and language isnt store?.language
    updated = yes if not @options.isDerived and @_uploader?.files?.length

    return if updated then $.extend(store, updatedStore) else updated
    
  # function to set up the uploader and start it
  _createUploader: ->
    return null if @_uploader? or @options.isDerived
    
    # initialize the uploader
    uploader = @_uploader = new plupload.Uploader
      runtimes: "html5,html4"
      container: @_$(@options.uploadContainer).attr "id"
      browse_button: @_$(@options.browseButton).attr "id"
      drop_element: @_$(@options.dropFileContainer).attr "id"
      file_data_name: @options.filenameKey
      max_file_size: "10mb"
      max_file_count: @options.uploadFileCount
      url: @options.createRoute().url
  
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
        @_$(@options.dropFileContainer)
          .text(files[0].name)
          .tooltip("destroy")
          .tooltip
            title: @options.filenameTip
        #@_changeInputState @_$(@options.updateButton), "enabled"
    
    uploader.bind "UploadProgress", (up, file) =>
      if file.percent
        @_trigger "uploadProgress", up, file
  
    uploader.bind "Error", (up, error) =>
      @_$(@options.dropFileContainer)
        .text(@options.uploadFailedMessage)
        .tooltip("destroy")
        .tooltip
          title: @options.dropFileTip
      @_trigger "uploadError", up, error
  
    uploader.bind "FileUploaded", (up, file, response) =>
      up.removeFile file
      @_$(@options.dropFileContainer)
        .text(@options.dropFileMessage)
        .tooltip("destroy")
        .tooltip
          title: @options.dropFileTip
      store = JSON.parse response.response
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
    @options.store ?= @_$(@options.innerContainer).data @options.dataKey
    @options.isDerived ?= not @_$(@options.browseButton).length
        
    # handle update button click
    @_on @_$(@options.updateButton),
      click: (e) =>
        updateStore = (store, updatedStore) =>
          triggerEvent = =>
            @_$(@options.innerContainer).data @options.dataKey, @options.store = updatedStore
            @_trigger "update", e,
              data: store
              updatedData: updatedStore
          
          updated = @_getUpdated store
          if not not updated
            updatedStore = updatedStore ? store
            updateOptions = updatedStore
            updateOptions.title = updated.title
            updateOptions.description = updated.description
            updateOptions.language = updated.language
            updateOptions =
              details: updateOptions

            @options.updateRoute(updatedStore.id).ajax
              contentType: Helpers.MimeTypes.json
              data: JSON.stringify updateOptions
              success: (updatedStore) =>
                triggerEvent()
              complete: =>
                @_changeInputState @_$(@options.updateButton), "reset"
          else
            @_changeInputState @_$(@options.updateButton), "reset"
            if updatedStore?
              @_form "populate", updatedStore
              triggerEvent()
        
        @_changeInputState @_$(@options.updateButton), "loading"
        if not @options.isDerived and @_uploader?.files.length
          @_uploader.settings.url = @options.updateRoute(@options.store?.id).url
          uploadComplete = (up, file, response) =>
            updatedStore = JSON.parse response.response
            @_uploader.unbind "FileUploaded", uploadComplete
            updateStore @options.store, updatedStore
          @_uploader.bind "FileUploaded", uploadComplete
          @_uploader.start()
        else updateStore @options.store
        
        e.preventDefault()
        
    @_on @_$(".modal"),
      hidden: =>
        @destroy()
    
    if @options.store?
      @_form "enabled"
      @_changeInputState @_$(@options.updateButton), "enabled"
    else
      @_form "disabled"
      @_changeInputState @_$(@options.updateButton), "disabled"
    
    if @options.store?
      @_form "populate", @options.store
    
    @_$(@options.titleInput).tooltip()
    @_$(@options.descriptionInput).tooltip()
    @_$(@options.languageList).tooltip()
    @_$(@options.browseButton).tooltip()
    
  _destroy: ->
    @_$(@options.titleInput).tooltip "destroy"
    @_$(@options.descriptionInput).tooltip "destroy"
    @_$(@options.languageList).tooltip "destroy"
    @_$(@options.dropFileContainer).tooltip "destroy"
    @_$(@options.browseButton).tooltip "destroy"
    @_$(@options.updateButton).tooltip "destroy"
    
  _getCreateOptions: ->
      innerContainer: ".ctr-store-details-inner"
      detailsForm: ".frm-details"
      titleInput: ".input-store-title"
      descriptionInput: ".input-store-desc"
      languageList: ".lst-store-language"
      uploadContainer: ".ctr-store-upload"
      dropFileContainer: ".ctr-store-dropfile"
      browseButton: ".btn-store-browse"
      updateButton: ".btn-apply"
      createRoute: jsRoutes.controllers.CollectionsController.create
      updateRoute: jsRoutes.controllers.CollectionsController.update
      uploadFileCount: 1
      dataKey: "store"
      filenameKey: "corpus"
      uploadFileMessage: "Upload file"
      dropFileMessage: "Drop file or browse"
      dropFileTip: "A file can be drag and dropped here"
      filenameTip: "Name of the file to be uploaded"
      dragFileMessage: "Almost there, just let go now!"
      uploadFailedMessage: "Something's amiss! Let's try again"
      acceptingFilesClass: "accepting-files"

$.widget "widgets.storeDetails", Sare.Widget, widget