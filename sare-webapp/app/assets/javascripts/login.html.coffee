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

minifiableDep = window.RjsHelpers.minifiableDep
define = window.define

define [
	"jquery"
	"jsRoutes"
	"bootstrap-tooltip"
	minifiableDep "main.html"
], ->
	# define reusables
	$ = window.jQuery
	jsRoutes = window.jsRoutes
	Sare = window.Sare
	Page = Sare.Page
	Helpers = Sare.Helpers
	Selectors = Page.Selectors
	Selectors.guestSignonButton = "#btn-guest"
	
	document = window.document
	location = window.location
	
	initJanrain = ->
		if typeof window.janrain isnt "object"
			window.janrain = {}
		if typeof window.janrain.settings isnt "object"
			window.janrain.settings = {}
		
		janrain.settings.tokenUrl = jsRoutes.controllers.base.Application.login(location.href)
			.absoluteURL location.protocol is "https:"
		
		isReady = ->
			janrain.ready = true
		
		if document.addEventListener
		  document.addEventListener "DOMContentLoaded", isReady, false
		else
		  window.attachEvent "onload", isReady
		
		e = document.createElement "script"
		e.type = "text/javascript"
		e.id = "janrainAuthWidget"
		
		if location.protocol is "https:"
		  e.src = "https://rpxnow.com/js/lib/sarenv/engage.js"
		else
		  e.src = "http://widget-cdn.rpxnow.com/js/lib/sarenv/engage.js"
		
		s = document.getElementsByTagName("script")[0];
		s.parentNode.insertBefore e, s
	
	initJanrain()
	
	$ ->
		$(Selectors.guestSignonButton).tooltip()