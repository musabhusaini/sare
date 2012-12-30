###
main.html.js
###

Sare = window.Sare =
  Options:
    pingEnbabled: true
    pingInterval: 5

$ = window.jQuery
jsRoutes = window.jsRoutes

# function that pings the server repeatedly.
ping = ->
  jsRoutes.controllers.base.Application.keepAlive().ajax(
    success: ->
      delayedPing()
  )

# function to generate delayed pings.
delayedPing = ->
  window.setTimeout ping, 1000*60*Sare.Options.pingInterval

# start pinging
if Sare.Options.pingEnabled
  delayedPing()