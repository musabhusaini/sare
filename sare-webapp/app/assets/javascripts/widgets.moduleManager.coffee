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

JSON = window.JSON
$ = window.jQuery
Sare = window.Sare
Page = Sare.Page
Selectors = Page.Selectors

Selectors.moduleContainer = ".ctr-module"

Math = window.Math
history = window.history
location = window.location
localStorage = window.localStorage

widget =
  _prevState: ->
    JSON.parse localStorage["previous"]
    
  _updatePrevState: (state) ->
    state = $.extend {
      url: window.location.href
    }, state
    localStorage["previous"] = JSON.stringify state
    state
    
  _pageCounter: ->
    counter = localStorage["counter"]
    if counter?
      counter = window.Number counter
    counter ? 0
    
  _updatePageCounter: ->
    counter = @_pageCounter()
    localStorage["counter"] = counter + 1
    counter
    
  _generateBrowserState: ->
    uid: @_updatePageCounter()
    
  _pushBrowserState: (module) ->
    history.pushState state = @_generateBrowserState(), module.name, module.url
    @_updatePrevState state
    
  _replaceBrowserState: (module) ->
    history.replaceState state = @_generateBrowserState(), module.name, module.url
    @_updatePrevState state
    
  _forwardModules: null
  _backwardModules: null
  _currentModule: null
  
  modules: ->
    back = @_backwardModules ? []
    current = if @_currentModule? then [ @_currentModules ] else []
    next = @_forwardModules ? []
    back.concat current, next
  
  replace: (module, noDisplay) ->
    return null if not module? or typeof module isnt "object"
    
    @_replaceBrowserState module
    @_currentModule = module
    if not noDisplay then @display module
  
  push: (module) ->
    return null if not module? or typeof module isnt "object"
    
    @_pushBrowserState module
    @_forwardModules = [ module ]
    @next()
    
  peek: ->
    @_currentModule
  
  next: ->
    if @_currentModule? then @_backwardModules.push oldModule = @_currentModule
    @_currentModule = @_forwardModules.pop()
    if @_currentModule?
      @_callModule oldModule, "disable"
      @_display @_currentModule
    else
      location.reload()
    @_currentModule
  
  previous: ->
    if @_currentModule? then @_forwardModules.push oldModule = @_currentModule
    @_currentModule = @_backwardModules.pop()
    if @_currentModule?
      @_remove oldModule
      @_display @_currentModule
    else
      location.reload()
    @_currentModule
  
  _display: (module) ->
    if not module.url?
      @option "output", []
    else
      @option "output", null
      
    if not module.canPartiallyRender and module.url?
      location.href = module.url
      return
      
    if module.target? and @_$(@options.contentContainer).has($(module.target)).length
      @_callModule module, "refresh"
      return @_callModule module, "enable"
    module.target ?= $("<div>")
    $(module.target).empty().hide().appendTo @_$(@options.contentContainer)
    if module.url?
      $(module.target)
        .load(module.url, "partial=true")
        .show()
      # TODO: add animations or whatever here.
  
  _remove: (module) ->
    $(module.target).remove()
  
  _callModule: (module, call, data) ->
    return null if not module? or not module.target?
    target = $(module.target).filter(Selectors.moduleContainer)
    target = $(module.target).find(Selectors.moduleContainer) if not target.length
    widget = $(target).first().data @options.widgetKey
    if typeof widget is "object"
      widget[call] data
    
  _create: ->
    @_forwardModules = []
    @_backwardModules = []
    
    window.addEventListener "popstate", (e) =>
      state = e.state
      uid = state?.uid
      if uid?
        @next() if uid > @_prevState()?.uid
        @previous() if uid < @_prevState()?.uid
        @_updatePrevState state
    
    module =
      url: null
      name: "Entry module"
      target: @_$(@options.contentContainer).children()
    
    @replace module, true
    @_$(@options.nextModulesButtons).on "click", "button", (e) =>
      @push $(e.target).data @options.moduleKey
  
  _setOption: (key, value) ->
    switch key
      when "output"
        @_$(@options.moduleControlsContainer).hide()
        if not value? then return
        if typeof value is "object" then value = JSON.stringify(value) 
        @options.moduleOptionsRoute(value).ajax
          success: (modules) =>
            @_$(@options.moduleControlsContainer).show()
            @_$(@options.nextModulesButtons).empty()
            for module in modules
              $("<button>")
                .text(module.name)
                .addClass("btn")
                .data(@options.moduleKey, module)
                .appendTo @_$ @options.nextModulesButtons
    
    $.Widget.prototype._setOption.apply @, arguments
    
  _getCreateOptions: ->
    output: null
    contentContainer: ".ctr-module-content"
    nextModulesButtons: ".btg-next-modules"
    progressBarContainer: ".ctr-progress"
    moduleControlsContainer: ".ctr-module-controls"
    moduleKey: "module"
    widgetKey: "widget"
    moduleOptionsRoute: jsRoutes.controllers.ModuleController.options

$.widget "widgets.moduleManager", Sare.Widget, widget