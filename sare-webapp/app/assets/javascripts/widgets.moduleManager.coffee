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

JSON = window.JSON
$ = window.jQuery
Sare = window.Sare
Page = Sare.Page
Selectors = Page.Selectors
Strings = Page.Strings

Selectors.moduleContainer = ".ctr-module"

Math = window.Math
history = window.history
location = window.location
localStorage = window.localStorage

homeModule =
	id: "20e7c8130d0c44dea90f5cc6427add0a"
	name: "Start"

widget =
	_generateBrowserState: ->
		uid: @_$(@options.breadcrumbsContainer).children("li").length
		
	_pushBrowserState: (module) ->
		history.pushState (state = @_generateBrowserState()), module.name, module.url
	
	_replaceBrowserState: (module) ->
		state = @_generateBrowserState()
		state.uid--
		history.replaceState state, module.name, module.url
	
	_getModule: (breadcrumb) ->
		$(breadcrumb).data @options.moduleKey
	
	_getActiveBreadcrumb: ->
		@_$(@options.breadcrumbsContainer).children "li.active:first"
	
	_getActiveModule: ->
		@_getModule @_getActiveBreadcrumb()
	
	_createBreadcrumb: (module) ->
		if not @_isModule(module) then return null
		
		name = if module.baseName? then "#{module.baseName} (#{module.name})" else module.name
		(breadcrumb = $ "<li><a href='#'>#{name}</a></li>")
			.data @options.moduleKey, module
		module.breadcrumb = breadcrumb
	
	_isModule: (module) ->
		module? and typeof module is "object" and module.id?
	
	_callModule: (module, call, data) ->
		if not @_isModule(module) then return null
		
		target = $(module.target).filter Selectors.moduleContainer
		target = $(module.target).find(Selectors.moduleContainer) if not target.length
		widget = $(target).first().data Strings.widgetKey
		if widget? and typeof widget is "object"
			widget[call]? data
	
	_deactivateModule: (module) ->
		if not @_isModule(module) then return null
		
		@_callModule module, "disable"
		$(module.breadcrumb)
			.removeClass("active")
			.children("a").prop "href", module.url
		$(module.target).hide @options.animationDuration
	
	_activateModule: (module) ->
		if not @_isModule(module) then return null
		
		@_callModule module, "enable"
		@_deactivateModule @_getActiveModule()
		$(module.breadcrumb)
			.addClass("active")
			.children("a").prop "href", null
		$(module.target).show @options.animationDuration
		@_callModule module, "refresh"
		
	_removeModule: (module) ->
		if not @_isModule(module) then return null
		
		@_callModule module, "destroy"
		$(module.breadcrumb).remove()
		$(module.target).remove()
	
	_replaceActive: (module, skipDisplay) ->
		if not @_isModule(module) then return null
		
		newBreadcrumb = @_createBreadcrumb module
		active = @_getActiveBreadcrumb()
		@_replaceBrowserState module
		$(active).replaceWith newBreadcrumb
		if not skipDisplay then @display module
	
	go: (index) ->
		if index is 0 then return
		
		active = @_getActiveModule()
		if active?
			gotoModule = @_getModule(if index > 0 then $(active.breadcrumb).nextAll().eq(index-1) else $(active.breadcrumb).prevAll().eq(-index-1))
		if gotoModule?
			@_deactivateModule active
			@display gotoModule
		else
			location.reload()
		gotoModule
	
	push: (module) ->
		if not @_isModule(module) then return null
		
		# clear previous modules.
		that = @
		$(@_getActiveBreadcrumb()).nextAll().each ->
			that._removeModule that._getModule @
		
		@_pushBrowserState module
		
		activeBreadcrumb = @_getActiveBreadcrumb()
		if not $(activeBreadcrumb).children("span.divider").length
			$(activeBreadcrumb).append " <span class='divider'>&gt;</span>"
		(@_createBreadcrumb module).appendTo @_$ @options.breadcrumbsContainer
		@go 1
	
	display: (module) ->
		if not @_isModule(module) then return null
		
		if module.id is homeModule.id
			@option "output", []
		else
			@option "output", null
		
		if module.target?
			return @_activateModule module
		
		if not module.canPartiallyRender and module.url?
			location.href = module.url
			return
				
		module.target ?= $("<div>")
			.hide()
			.appendTo @_$ @options.contentContainer
		if module.url?
			$(module.target)
				.load module.url, "partial=true", => @_activateModule module
		
	_create: ->
		# hide the controls as they get shown by something else.
		@_$(@options.moduleControlsContainer).hide()
		
		window.addEventListener "popstate", (e) =>
			uid = e.state?.uid
			if uid?
				@go uid - $(@_getActiveBreadcrumb()).index()

		module = $.extend {}, homeModule, (@options.entryModule ? {}),
			target: @_$(@options.contentContainer).children()
			url: location.href
		
		@_replaceActive module, true
		@_activateModule module
		
		@_on @_$(@options.breadcrumbsContainer),
			"click a": (e) ->
				distance = $(e.target).closest("li").index() - $(@_getActiveBreadcrumb()).index()
				history.go distance
				false
		
		@_on @_$(@options.nextModulesButtons),
			"click button": (e) ->
				module = $(e.target).data @options.moduleKey
				return true if module.subModules? and module.subModules.length > 0
				@push module
	
	_setOption: (key, value) ->
		switch key
			when "output"
				@_$(@options.moduleControlsContainer).hide()
				if not value? then return
				if not $.isArray(value) then value = [ value ]
				value = ((val.id ? val) for val in value)
				@options.moduleOptionsRoute(JSON.stringify value).ajax
					success: (modules) =>
						makeButton = (module) =>
							$("<button>")
									.text(module.name)
									.addClass("btn")
									.data @options.moduleKey, module
						@_$(@options.moduleControlsContainer).hide()
						@_$(@options.nextModulesButtons).empty()
						activeModule = @_getActiveModule()
						for module in modules
							if module.id isnt activeModule?.id or activeModule?.allowSelfOutput
								@_$(@options.moduleControlsContainer).show()
								btn = makeButton module
								if not module.subModules? or module.subModules.length < 1
									@_$(@options.nextModulesButtons).append btn
								else
									btn
										.attr("data-toggle", "dropdown")
										.append(" ")
										.append $ "<span class=\"caret\">"
									subBtns = $ "<ul class=\"dropdown-menu\">"
									for mod in module.subModules
										subBtns.append $("<li>").append makeButton mod
									$("<div>")
										.addClass("btn-group")
										.append(btn)
										.append(subBtns)
										.appendTo @_$(@options.nextModulesButtons)
		
		$.Widget.prototype._setOption.apply @, arguments
		
	_getCreateOptions: ->
		output: null
		headerContainer: ".ctr-module-header"
		breadcrumbsContainer: ".ctr-breadcrumbs > ul.breadcrumb"
		contentContainer: ".ctr-module-content"
		nextModulesButtons: ".btg-next-modules"
		progressBarContainer: ".ctr-progress"
		moduleControlsContainer: ".ctr-module-controls"
		moduleKey: "module"
		moduleOptionsRoute: jsRoutes.controllers.ModuleController.options
		animationDuration: 200

$.widget "widgets.moduleManager", Sare.Widget, widget