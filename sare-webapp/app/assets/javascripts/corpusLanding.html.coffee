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
Page = Sare.Page
Helpers = Sare.Helpers
PageOptions = Page.Options
Strings = Page.Strings
Methods = Page.Methods
Selectors = Page.Selectors
PageObjects = Page.Objects

#define page constants
PageOptions.uploadFileCount = 1
Strings.corpusItemDataKey = "store"
Strings.corpusFilenameKey = "corpus"
Strings.dropFileMessage = "Drop a file here or click browse"
Strings.dragFileMessage = "Almost there, just let go now!"
Strings.uploadFailedMessage = "Something's amiss! Please try again"
Selectors.corpusList = "#lst-corpora"
Selectors.addCorpusButton = "#btn-add-corpus"
Selectors.deleteCorpusButton = "#btn-delete-corpus"
Selectors.corpusTitleInput = "#inp-corpus-title"
Selectors.corpusDescriptionInput = "#inp-corpus-desc"
Selectors.corpusLanguageList = "#lst-corpus-language"
Selectors.uploadCorpusContainer = "#ctr-corpus-upload"
Selectors.dropCorpusFileContainer = "#ctr-corpus-dropfile"
Selectors.uploadCorpusBrowseButton = "#btn-corpus-browse"
Selectors.updateCorpusButton = "#btn-corpus-update"

# function to disable the corpus update form
disableCorpusForm = ->
  $(Selectors.corpusTitleInput).attr "disabled", true
  $(Selectors.corpusDescriptionInput).attr "disabled", true
  $(Selectors.corpusLanguageList).attr "disabled", true
  $(Selectors.dropCorpusFileContainer).text("")
  $(Selectors.uploadCorpusBrowseButton).attr "disabled", true
  $(Selectors.updateCorpusButton).attr "disabled", true
  disableUploader()

# function to enable the corpus update form
enableCorpusForm = ->
  $(Selectors.corpusTitleInput).removeAttr "disabled"
  $(Selectors.corpusDescriptionInput).removeAttr "disabled"
  $(Selectors.corpusLanguageList).removeAttr "disabled"
  $(Selectors.dropCorpusFileContainer).text(Strings.dropFileMessage)
  $(Selectors.uploadCorpusBrowseButton).removeAttr "disabled"
  $(Selectors.updateCorpusButton).removeAttr "disabled"
  enableUploader()

# function to update corpus item to the most current value
updateCorpusListOption = (item, corpus) ->
  $(item)
    .val(corpus.id)
    .text(corpus.title ? corpus.id)
    .data Strings.corpusItemDataKey, corpus

# function to get the currently selected corpus information
getSelectedCorpus = ->
  selected = $(Selectors.corpusList).children("option:selected")
  [$(selected).data(Strings.corpusItemDataKey), selected]

# function to populate corpus form with values
populateCorpusForm = (corpus) ->
  $(Selectors.corpusTitleInput).val(corpus?.title)
  $(Selectors.corpusDescriptionInput).val(corpus?.description)
  if corpus?.language
    $(Selectors.corpusLanguageList).val(corpus.language)

# function to set up the uploader and start it
enableUploader = ->
  return null if PageObjects.uploader?
  
  # initialize the uploader
  uploader = PageObjects.uploader = new plupload.Uploader
    runtimes: "html5,html4"
    container: $(Selectors.uploadCorpusContainer).attr "id"
    browse_button: $(Selectors.uploadCorpusBrowseButton).attr "id"
    drop_element: $(Selectors.dropCorpusFileContainer).attr "id"
    file_data_name: Strings.corpusFilenameKey
    max_file_size: "10mb"
    max_file_count: PageOptions.uploadFileCount
    url: jsRoutes.controllers.CollectionsController.create().url

    ### not working because of browser/HTML limitations
    filters: [{
        title: "XML files"
        extensions: "xml"
      }, {
        title: "Zip files"
        extensions: "zip"
      }, {
        title: "Text files"
        extensions: "txt,csv"
      }
    ]
    ###

  # start the uploader
  uploader.init()
  
  # bind various events to the uploader
  uploader.bind "FilesAdded", (up, files) =>
    if files.length
      # enforce the file limit so that last files take precedence
      up.removeFile file for file in up.files[...(up.files.length - PageOptions.uploadFileCount)]
      $(Selectors.dropCorpusFileContainer).text files[0].name
      $(Selectors.updateCorpusButton).removeAttr "disabled"
  
  uploader.bind "UploadProgress", (up, file) =>
    if file.percent
      Methods.setProgress file.percent

  uploader.bind "Error", (up, error) =>
    $(Selectors.dropCorpusFileContainer).text Strings.uploadFailedMessage
    Methods.hideProgress()

  uploader.bind "FileUploaded", (up, file, response) =>
    Methods.hideProgress()
    $(Selectors.dropCorpusFileContainer).text Strings.dropFileMessage
    [corpus, selected] = getSelectedCorpus()
    corpus = JSON.parse response.response
    updateCorpusListOption selected, corpus
  
  if uploader.runtime is "html5"
    defaults =
      text: Strings.dropFileMessage
    dragenter = ->
      defaults.text = $(this).text()
      $(this).text(Strings.dragFileMessage)
      $(this).css "border", "1px solid #000000"
    dragleave = ->
      $(this).text(defaults.text)
      $(this).css "border", ""
    
    $(Selectors.dropCorpusFileContainer).on "dragenter", dragenter
    $(Selectors.dropCorpusFileContainer).on "dragleave", dragleave
    
    uploader.bind "Destroy", (up) =>
      $(Selectors.dropCorpusFileContainer).off "dragenter", dragenter
      $(Selectors.dropCorpusFileContainer).off "dragleave", dragleave

# function to disable the uploader
disableUploader = ->
  PageObjects.uploader?.stop()
  PageObjects.uploader?.destroy()
  PageObjects.uploader = null

$ ->
  # handle add corpus button click
  $(Selectors.addCorpusButton).click (e) =>
    jsRoutes.controllers.CollectionsController.create().ajax
      contentType: Helpers.MimeTypes.json
      data: JSON.stringify
        content: ""
        format: "text"
      success: (response) =>
        updateCorpusListOption(option = $("<option>"), response)
        $(Selectors.corpusList)
          .append(option)
          .val(response.id)
          .change()
        $(Selectors.corpusTitleInput).focus()
  
  # handle delete corpus button click
  $(Selectors.deleteCorpusButton).click (e) =>
    [corpus, selected] = getSelectedCorpus()
    jsRoutes.controllers.CollectionsController.delete(corpus.id).ajax(
      success: (response) =>
        next = $(selected).next()
        next = $(selected).prev() if not next.length
        $(Selectors.corpusList)
          .val $(next).data(Strings.corpusItemDataKey)?.id
        $(Selectors.corpusList)
          .change()
        $(selected).remove()
    ) if selected?
  
  # set up the corpus list
  $(Selectors.corpusList).change (e) =>
    [corpus, selected] = getSelectedCorpus()
    if selected?.length
      $(Selectors.moduleOutputField).val JSON.stringify corpus
      $(Selectors.nextModuleButton).removeAttr "disabled"
      $(Selectors.deleteCorpusButton).removeAttr "disabled"
      populateCorpusForm(corpus)
      enableCorpusForm()
    else
      $(Selectors.moduleOutputField).val Strings.defaultModuleInput
      $(Selectors.nextModuleButton).attr "disabled", true
      $(Selectors.deleteCorpusButton).attr "disabled", true
      populateCorpusForm(null)
      disableCorpusForm()

  # set up the update button
  $(Selectors.updateCorpusButton).click (e) =>
    [corpus, selected] = getSelectedCorpus()
    
    updateCorpus = (corpus, updatedCorpus) ->
      [title, description, language] = [
        $(Selectors.corpusTitleInput).val()
        $(Selectors.corpusDescriptionInput).val()
        $(Selectors.corpusLanguageList).val()
      ]
      
      updatedCorpus = updatedCorpus ? corpus
      updated = no
      updateOptions = updatedCorpus
      updateOptions.title = (updated = yes; title) if title and title isnt corpus.title
      updateOptions.description = (updated = yes; description) if description and description isnt corpus.description
      updateOptions.language = (updated = yes; language) if language and language isnt corpus.language
      updateOptions =
        details: updateOptions
      
      if updated
        jsRoutes.controllers.CollectionsController.update(updatedCorpus.id).ajax
          contentType: Helpers.MimeTypes.json
          data: JSON.stringify updateOptions
          success: (updatedCorpus) =>
            updateCorpusListOption selected, updatedCorpus
      else
        updateCorpusListOption selected, updatedCorpus
        populateCorpusForm updatedCorpus
    
    if PageObjects.uploader?.files.length
      PageObjects.uploader.settings.url = jsRoutes.controllers.CollectionsController.update(corpus.id).url
      uploadComplete = (up, file, response) =>
        updatedCorpus = JSON.parse response.response
        PageObjects.uploader.unbind "FileUploaded", uploadComplete
        updateCorpus corpus, updatedCorpus
      PageObjects.uploader.bind "FileUploaded", uploadComplete
      PageObjects.uploader.start()
      Methods.showProgress()
    else updateCorpus corpus
    
    e.preventDefault()

  # do the one-offs
  $(Selectors.nextModuleButton).attr "disabled", true
  disableCorpusForm()
  
  # select the first item, if any
  firstCorpus = $(Selectors.corpusList).children("option:first")
  if firstCorpus.length
    $(Selectors.corpusList)
      .val($(firstCorpus).data(Strings.corpusItemDataKey)?.id)
      .change()
  