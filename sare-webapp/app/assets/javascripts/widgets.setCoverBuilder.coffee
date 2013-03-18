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

# define reusables
$ = window.jQuery
jsRoutes = window.jsRoutes
JSON = window.JSON
Sare = window.Sare
Helpers = Sare.Helpers
Page = Sare.Page
Selectors = Page.Selectors
Strings = Page.Strings

widget =
	_create: ->
		@options.setcover ?= $(@element).data @options.setCoverKey
		@options.corpus ?= $(@element).data(@options.corpusKey) ? @options.setcover?.baseCorpus
		if not @options.corpus?
			return false
		
		if not @options.setcover?
			# TODO: refresh view.
			
			@_$(@options.setCoversContainer).children(Selectors.moduleContainer)
				.storeList "option",
					suppressOutput: true
					addRoute: =>
						@options.createSetCoverRoute @options.corpus.id
		
	refresh: ->
		$(@element).data Strings.widgetKey, @
		
	_init: ->
		@refresh()
		
	_destroy: ->
		
	_setOption: (key, value) ->
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		setCoversContainer: ".ctr-setcovers"
		createSetCoverRoute: jsRoutes.controllers.modules.SetCoverBuilder.create
		corpusKey: "corpus"
		setCoverKey: "setcover"

$.widget "widgets.setCoverBuilder", Sare.Widget, widget