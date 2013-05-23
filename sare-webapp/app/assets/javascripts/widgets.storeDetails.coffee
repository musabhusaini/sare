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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SARE. If not, see <http://www.gnu.org/licenses/>.
###

minifiableDep = window.RjsHelpers.minifiableDep
define = window.define
define [
	"jquery"
	"jsplugins/plupload/js/plupload.full"
	minifiableDep "main.html"
	minifiableDep "Sare.Widget"
	"bootstrap-button"
	"bootstrap-tooltip"
], ->
	# define reusables
	$ = window.jQuery
	jsRoutes = window.jsRoutes
	JSON = window.JSON
	plupload = window.plupload

	Sare = window.Sare
	Helpers = Sare.Helpers
	Page = Sare.Page
	Strings = Page.Strings
	
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
					@_$(@options.sizeText).text data?.size
	
		_uploader: null
		
		_getUpdated: (store) ->
			store ?= @options.store 
			{ title, description, language } = updatedStore = {
				title: @_$(@options.titleInput).val()
				description: @_$(@options.descriptionInput).val()
				language: @_$(@options.languageList).val()
			}
			
			grabbers = null
			for grabber in @_$(@options.alternateGrabberContainer).children()
				grabber = ($(grabber).data Strings.widgetKey)?.getData?()
				if grabber then grabbers = $.extend {}, grabbers, grabber
			
			updated = no
			updated = yes if title and title isnt store?.title
			updated = yes if description and description isnt store?.description
			updated = yes if language and language isnt store?.language
			updated = yes if not @options.isDerived and @_uploader?.files?.length
			updated = yes if grabbers?
	
			return if updated then $.extend(grabbers: grabbers, store, updatedStore) else updated
			
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
					@_fixButtons()
			
			uploader.bind "UploadProgress", (up, file) =>
				if file.percent
					@_trigger "uploadProgress", up, file
		
			uploader.bind "Error", (up, error) =>
				up.removeFile file for file in up.files
				@_$(@options.dropFileContainer)
					.text(@options.uploadFailedMessage)
					.tooltip("destroy")
					.tooltip
						title: @options.dropFileTip
				@_changeInputState @_$(@options.storeUpdateButton), "reset"
				@_fixButtons()
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
				@_fixButtons()
			
			if uploader.runtime is "html5"
				@_$(@options.dropFileContainer).on "dragenter", dragenter
				@_$(@options.dropFileContainer).on "dragleave", dragleave
				
				uploader.bind "Destroy", (up) =>
					@_$(@options.dropFileContainer).off "dragenter", dragenter
					@_$(@options.dropFileContainer).off "dragleave", dragleave
	
		_destroyUploader: ->
			@_uploader?.stop()
			@_uploader?.destroy?()
			@_uploader = null
			
		_destroyGrabbers: ->
			@_$(@options.alternateGrabberContainer).children()
				.each ->
					($(@).data Strings.widgetKey)?.destroy?()
			@_$(@options.alternateGrabberContainer).empty()
			if @_$(@options.twitterButton).is ".active"
				@_$(@options.twitterButton).button "toggle"
					
		_fixButtons: ->
			# delay execution so that this happens at the end.
			window.setTimeout =>
				for input in [ @options.storeUpdateButton, @options.storeResetButton ]
					@_changeInputState input, "enabled", not not @_getUpdated()
			, 0
			false
		
		_populateDocument: (id, elem, callback) ->
			elem ?= @_$(@options.documentsList)
				.children("option")
					.filter -> $(@).val() is id
			if $(elem).length > 1
				id = $(elem).map(-> $(@).val()).get()
			else
				id ?= $(elem).val()
			
			if not elem? or not $(elem).length or not id? then return
			
			@options.getDocumentRoute(@options.store.id, JSON.stringify id).ajax
				success: (documents) =>
					$(elem).each (index, elem) =>
						document = $.grep(documents, (document) => document.id is $(elem).val())?[0]
						$(elem)
							.text(document.summarizedContent or document.content)
							.data @options.documentKey, document
					callback? documents
		
		_populateDocuments: (refreshList) ->
			if not @options.populateDocuments then return
			
			if refreshList
				@options.listDocumentsRoute(@options.store.id).ajax
					success: (ids) =>
						documents = @_$(@options.documentsList).children "option"
						newIds = $.grep ids, (id) =>
							$(documents).is -> $(@).val() is id
						, true
						$.each newIds, (index, id) =>
							@_$(@options.documentsList).append "<option value='#{id}'>#{id}</option>"
						if not @_$(@options.documentsList).children("option:selected").length
							@_$(@options.documentsList)
								.val(@_$(@options.documentsList).children("option").first().val())
								.change()
						@_populateDocuments()
				return
			
			that = @
			next = @_$(@options.documentsList)
				.children("option")
					.filter(-> not $(@).data(that.options.documentKey)?)
						.slice 0, 20
			if not $(next).length then return
			@_populateDocument null, $(next), => if @options.populateDocuments then @_populateDocuments()
		
		_create: ->
			@options.store ?= $(@element).data @options.dataKey
			@options.isDerived ?= not @_$(@options.browseButton).length
			
			# applies changes to the store.
			applyStoreChanges = (e, callback) =>
				updateStore = (store, updatedStore) =>
					finalizeUpdate = (updatedStore) =>
						$(@element).data @options.dataKey, @options.store = updatedStore
						@_form "populate", updatedStore
						@_populateDocuments true
						callback?()
						$(@element).trigger "storeUpdate",
							data: store
							updatedData: updatedStore
					
					updated = @_getUpdated store
					if not not updated
						@options.updateRoute(updated.id).ajax
							contentType: Helpers.MimeTypes.json
							data: JSON.stringify updated
							success: (updatedStore) =>
								finalizeUpdate updatedStore
							complete: =>
								@_changeInputState @_$(@options.storeUpdateButton), "reset"
								@_fixButtons()
					else
						@_changeInputState @_$(@options.storeUpdateButton), "reset"
						if updatedStore?
							finalizeUpdate updatedStore
							@_fixButtons()
				
				@_changeInputState @_$(@options.storeUpdateButton), "loading"
				if not @options.isDerived and @_uploader?.files.length
					@_uploader.settings.url = @options.updateRoute(@options.store?.id).url
					uploadComplete = (up, file, response) =>
						updatedStore = JSON.parse response.response
						@_uploader.unbind "FileUploaded", uploadComplete
						updateStore @options.store, updatedStore
					@_uploader.bind "FileUploaded", uploadComplete
					@_uploader.start()
				else updateStore @options.store
			
			# handle update button click
			@_on @_$(@options.storeUpdateButton),
				click: (e) ->
					applyStoreChanges e, =>
						@_destroyGrabbers()
					false
			
			# handle reset
			@_on @_$(@options.storeResetButton),
				click: (e) ->
					if not @options.isDerived and @_uploader?
						@_uploader.removeFile file for file in @_uploader.files
						@_$(@options.dropFileContainer)
							.text(@options.dropFileMessage)
							.tooltip("destroy")
							.tooltip
								title: @options.dropFileTip
	
					@_form "populate", @options.store
					@_destroyGrabbers()
					@_fixButtons()
					false
			
			# handle twitter grabber
			@_on @_$(@options.twitterButton),
				click: (e) ->
					active = $(e.target).is ".active"
					@_destroyGrabbers()
					if active
						# the last call will toggle in this case, so toggle again.
						$(e.target).button "toggle"
					else
						@_$(@options.alternateGrabberContainer)
							.load @options.twitterGrabberViewRoute(@options.store.id).url, =>
								@_$(@options.alternateGrabberContainer).parent().show()
					e.preventDefault()
			
			@_on @_$(@options.alternateGrabberContainer),
				"closed .alert": ->
					@_destroyGrabbers()
			
			@_on @_$(@options.documentsList),
				change: (e) ->
					show = (document) =>
						if $.isArray document then document = document[0]
						@_$(@options.documentEditorContainer)
							.empty()
							.load @options.documentEditorRoute(@options.store.id, document.id).url
					
					target = $(e.target).children ":selected"					
					document = $(target).data @options.documentKey
					if not document?
						@_populateDocument null, target, show
					else
						show document
			
			# we want to make sure the right buttons are enabled.
			@_on @element,
				"keyup input": ->
					@_fixButtons()
				"change select": ->
					@_fixButtons()
			
			# do the inits.
			@_populateDocuments()
			@_form (if @options.store? then "enabled" else "disabled")
			@_changeInputState @_$(@options.storeUpdateButton), (if @options.store? then "enabled" else "disabled")
			@_$(@options.documentsList)
				.val(@_$(@options.documentsList).children("option").first().val())
				.change()
			
			inputs = [
				@options.storeUpdateButton
				@options.storeResetButton
				@options.titleInput
				@options.descriptionInput
				@options.languageList
				@options.sizeText
				@options.browseButton
			]
			
			@_$(input).tooltip() for input in inputs
		
		refresh: ->
			$(@element).data Strings.widgetKey, @
			
			if @options.store?
				@_form "populate", @options.store
			
			# enable/disable all the right buttons.
			@_fixButtons()
			
			@_destroyGrabbers()
			
		_init: ->
			@refresh()
			
		_destroy: ->
			inputs = [
				@options.storeUpdateButton
				@options.storeResetButton
				@options.titleInput
				@options.descriptionInput
				@options.languageList
				@options.sizeText
				@options.browseButton
			]
			
			@_$(input).tooltip("destroy") for input in inputs
			@_destroyGrabbers()
			@_destroyUploader()
			@options.populateDocuments = off
			
		_setOption: (key, value) ->
			switch key
				when "store"
					@refresh()
				when "disabled"
					@_form if value then "disabled" else "enabled"
					@_destroyGrabbers()
			$.Widget.prototype._setOption.apply @, arguments
	
		_getCreateOptions: ->
				detailsForm: ".frm-details"
				titleInput: ".input-store-title"
				descriptionInput: ".input-store-desc"
				languageList: ".lst-store-language"
				sizeText: ".txt-store-size"
				uploadContainer: ".ctr-store-upload"
				alternateGrabberContainer: ".ctr-alt-grab"
				dropFileContainer: ".ctr-store-dropfile"
				browseButton: ".btn-store-browse"
				twitterButton: ".btn-twitter-grab"
				storeUpdateButton: ".btn-store-apply"
				storeResetButton: ".btn-store-reset"
				documentsContainer: ".ctr-documents"
				documentsList: ".lst-documents"
				documentEditorContainer: ".ctr-document"
				acceptingFilesClass: "accepting-files"
				updateRoute: jsRoutes?.controllers?.modules?.CorpusModule?.update
				listDocumentsRoute: jsRoutes?.controllers?.DocumentsController?.list
				getDocumentRoute: jsRoutes?.controllers?.DocumentsController?.get
				documentEditorRoute: jsRoutes?.controllers?.DocumentsController?.editorView
				twitterGrabberViewRoute: jsRoutes?.controllers?.modules?.CorpusModule?.twitterGrabberView
				populateDocuments: on
				uploadFileCount: 1
				dataKey: "store"
				filenameKey: "file"
				documentKey: "document"
				uploadFileMessage: "Upload file"
				dropFileMessage: "Drop file or browse"
				dropFileTip: "A file can be dragged and dropped here"
				filenameTip: "Name of the file to be uploaded"
				dragFileMessage: "Almost there, just let go now!"
				uploadFailedMessage: "Something's amiss! Let's try again"
	
	$.widget "widgets.storeDetails", Sare.Widget, widget