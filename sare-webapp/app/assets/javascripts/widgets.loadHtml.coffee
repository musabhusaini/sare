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
Sare = window.Sare
Page = Sare.Page
Helpers = Sare.Helpers
PageObjects = Page.Objects

replaceView = (e, state) ->
  if state? and state.target? and state.content?
    #$(state.target).html state.content
    inner = $(state.target).wrapInner("<div>").children()
    effect = state.effect ? "slide"
    $(inner).hide effect,
      direction: "right",
      500,
      ->
        newInner = $("<div>").append($(state.content)).hide()
        $(state.target)
          .empty()
          .append newInner
        $(newInner).show effect,
          direction: "left",
          500

widget =
  _init: ->
    return null if not @options.route?
    
    generateState = (target, content) =>
      PageObjects.pageNumber ?= 0
      target = "#" + $(target).attr "id"
      if target is "#" then return null
      uid: ++PageObjects.pageNumber
      target: target
      content: content
      effect: @options.effect

    request =
      data:
        partial: true
      success: (response) =>
        history = window.history
        if not history.state? or not history.state.uid?
          currentState = generateState @element, $(@element).html()
          history.replaceState currentState, window.location.href, window.location.href
        state = generateState @element, response
        history.pushState state, url, url
        replaceView null, state

    if typeof @options.route is "object" and @options.route.ajax?
      ajax = @options.route.ajax
      url = @options.route.url
    else if typeof @options.route is "string"
      ajax = $.ajax
      url = @options.route
      request = $.extend request,
        url: url
    
    if ajax? then ajax request

  _getCreateOptions: ->
      route: null
      effect: "slide"

$.widget "widgets.loadHtml", Sare.Widget, widget

$(window).on "popstate", (e) ->
  replaceView e, window.history.state