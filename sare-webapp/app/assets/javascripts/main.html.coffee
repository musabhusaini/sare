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

Sare = window.Sare = $.extend window.Sare,
  Page:
    Configuration: {}
    Strings: {}
    Objects: {}
    Widgets: {}
    Methods: {}
    Selectors: {}
    Images:
    	wait: jsRoutes.controllers.Assets.at("images/throbber.gif").url
  Helpers:
    ContentTypes:
      json: "application/json; charset=utf-8"
    MimeTypes:
      json: "application/json"
    Guid: 
      random: ->
        "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace /[xy]/g, (c) ->
          r = window.Math.random() * 16|0 
          v = if c is "x" then r else (r & 0x3|0x8)
          v.toString 16

# function that pings the server repeatedly.
ping = ->
  jsRoutes.controllers.base.Application.keepAlive().ajax
    success: ->
      delayedPing()

# function to generate delayed pings.
delayedPing = ->
  window.setTimeout ping, 1000*60*Sare.Configuration.pingTimeout

# start pinging
if Sare.Configuration.pingTimeout isnt 0
  delayedPing()

fixWindowHeight = ->
  $("body")
    .css("height", "100%")
    .height($("body").height() - 111)

$(window).resize ->
  fixWindowHeight()

$ ->
  fixWindowHeight()