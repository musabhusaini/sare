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
	minifiableDep "main.html"
	minifiableDep "widgets.moduleManager"
], ->
	#define reusables
	location = window.location
	$ = window.jQuery
	
	Sare = window.Sare
	Page = Sare.Page
	Widgets = Page.Widgets
	Selectors = Page.Selectors
	Strings = Page.Strings
	
	#define page constants
	Strings.widgetKey = "moduleWidget"
	Strings.moduleOutputDataKey = "output"
	Selectors.moduleManagerContainer = "#ctr-module-manager"
	
	$ ->
		entryModule = $(Selectors.moduleManagerContainer).data "module"
		Widgets.moduleManager = $.proxy $(Selectors.moduleManagerContainer).moduleManager, $(Selectors.moduleManagerContainer)
		Widgets.moduleManager
			entryModule: entryModule
		if not entryModule? then Widgets.moduleManager "option", "output", []