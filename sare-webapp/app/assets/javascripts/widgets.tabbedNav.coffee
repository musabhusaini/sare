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
Widgets = Page.Widgets

Math = window.Math

widget =
	_create: ->
		@_on $(@element),
			"click li": (e) ->
				li = $(e.target).closest "li"
				if $(li).is(".#{@options.activeClass}") then return
				
				@_$("ul.nav").children("li")
					.removeClass @options.activeClass
				$(li)
					.addClass @options.activeClass
				
				nav = $(li).data @options.navKey
				@_$(@options.tabsContainer).children(@options.tabContainer)
					.removeClass @options.activeClass
					
				navKey = @options.navKey
				ctr = @_$(@options.tabsContainer).children(@options.tabContainer).filter ->
					nav is $(@).data navKey
				
				$(ctr).addClass @options.activeClass
		
	_destroy: ->
		
	_setOption: (key, value) ->
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		navContainer: "ul.nav"
		tabsContainer: ".ctr-tabs"
		tabContainer: ".ctr-tab"
		activeClass: "active"
		navKey: "nav"

$.widget "widgets.tabbedNav", Sare.Widget, widget