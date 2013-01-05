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
Selectors.corpusListContainer = "#ctr-corpus-list"
Selectors.documentListContainer = "#ctr-document-list"
Selectors.documentList = "#lst-documents"
Selectors.addDocumentButton = "#btn-add-doc"
Selectors.deleteDocumentButton = "#btn-delete-doc"

Document =
  # document list, add, delete controls
  Controls:
    display: (corpus) ->
      $(Selectors.documentList).empty()
      if not corpus? or not corpus.id?
        $(Selectors.addDocumentButton).attr "disabled", true
        return $(Selectors.deleteDocumentButton).attr "disabled", true
      
      $(Selectors.addDocumentButton).removeAttr "disabled"
      jsRoutes.controllers.DocumentsController.list(corpus.id).ajax
        success: (uuids) =>
          uuids ?= []
          for uuid in uuids
            $(Selectors.documentList).append $("<option>")
              .val(uuid)
              .text uuid
            $(Selectors.documentList).val(uuid).change() if $(Selectors.documentList).children("option").length == 1

$ ->
  $(Selectors.documentListContainer).documentList()
  
  $(Selectors.corpusListContainer).storeList
    uploadStart: ->
      Methods.showProgress()
    uploadProgress: (up, file) ->
      Methods.setProgress file.percent
    uploadError: ->
      Methods.hideProgress()
    fileUpload: (up, e) ->
      $(Selectors.documentListContainer).documentList "option", "store", e?.data
    uploadComplete: ->
      Methods.setProgress 0
      Methods.hideProgress()
    selectionChange: (e, selected) ->
      if selected?.data? then $(Selectors.nextModuleButton).removeAttr "disabled"
      else $(Selectors.nextModuleButton).attr "disabled", true
      $(Selectors.documentListContainer).documentList "option", "store", (selected?.data ? null)
      $(Selectors.nextModuleButton).data Strings.moduleOutputDataKey,
        if selected?.data? then JSON.stringify(selected.data) else null  