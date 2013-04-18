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
	remove: (nav) ->
		$(findNav nav).remove()
		$(findTab nav).remove()
	
	getNavs: ->
		@_$(@options.navContainer).children "li"
	
	findNav: (nav) ->
		navKey = @options.navKey
		$(@getNavs()).filter ->
			nav is $(@).data navKey
	
	getActiveNav: ->
		li = $(@getNavs()).filter(".#{@options.activeClass}")
		nav: $(li).data @options.navKey
		li: li
	
	getTabs: ->
		@_$(@options.tabsContainer).children @options.tabContainer
	
	findTab: (nav) ->
		navKey = @options.navKey
		$(@getTabs()).filter ->
			nav is $(@).data navKey
	
	activate: (li) ->
		if typeof li is "string"
			li = @findNav li
		if not $(li).length then return true
		
		$(@getNavs())
			.removeClass @options.activeClass
		$(li)
			.addClass @options.activeClass
		
		$(@getTabs())
			.removeClass @options.activeClass
		
		nav = $(li).data @options.navKey	
		ctr = @findTab nav
		
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
		@activate @getActiveNav()?.li
	
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