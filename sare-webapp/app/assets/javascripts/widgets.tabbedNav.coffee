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
Sare = window.Sare
Math = window.Math

widget =
	_findNav: (nav) ->
		navKey = @options.navKey
		@_$(@options.navContainer).children("li").filter ->
			nav is $(@).data navKey
	
	_findTab: (nav) ->
		navKey = @options.navKey
		@_$(@options.tabsContainer).children(@options.tabContainer).filter ->
			nav is $(@).data navKey
	
	getActiveNav: ->
		li = @_$(@options.navContainer).children("li").filter(".#{@options.activeClass}")
		nav: $(li).data @options.navKey
		li: li
	
	activate: (li) ->
		if typeof li is "string"
			li = @_findNav li
		if not $(li).length then return true
		
		@_$(@options.navContainer).children("li")
			.removeClass @options.activeClass
		$(li)
			.addClass @options.activeClass
		
		@_$(@options.tabsContainer).children(@options.tabContainer)
			.removeClass @options.activeClass
		
		nav = $(li).data @options.navKey	
		ctr = @_findTab nav
		
		$(ctr).addClass @options.activeClass
		
		$(@element).trigger "tabbedNavTabChanged",
			key: nav
			container: ctr
		
		false
		
	_create: ->
		@_on $(@element),
			"click li": (e) ->
				@activate $(e.target).closest "#{@options.navContainer} li"
	
	refresh: ->
		@activate @_$(@options.navContainer).find "li.#{@options.activeClass}"
	
	_init: ->
		@refresh()
	
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