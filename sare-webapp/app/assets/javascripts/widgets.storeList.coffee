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
Strings = Page.Strings
Widgets = Page.Widgets

widget =
	select: (value) ->
		return @selected value
	
	selected: (value) ->
		if typeof value is "string"
			@_$(@options.list).val(value).change()
		else if value instanceof $ and $(value).is("option") and $(value).is @_$(@options.list).children()
			@_$(@options.list).val($(value).val()).change()
		else if typeof(value) is "object" and value?.id?
			@_$(@options.list).val(value.id).change()

		selected = @_$(@options.list).children "option:selected"
		return {
			item: selected
			data: $(selected).data @options.storeKey
		}
		
	findItem: (id) ->
		key = @options.storeKey
		@_$(@options.list).children("option").filter ->
			$(@).data(key)?.id is id
	
	updateItem: (id, store) ->
		return if not id? or id isnt store?.id
		$(@findItem(id)).data @options.storeKey, store
		if @options.detailsShown then @_toggleDetails 0, false
	
	_updateListItem: (item, data) ->
		$(item)
			.val(data.id)
			.text(data.title ? data.id)
			.data @options.storeKey, data

	_toggleDetails: (duration, initial, callback) ->
		initial ?= @options.detailsShown
		duration ?= 200
		@_changeInputState @options.detailsButton, "disabled"
		if initial
			@_$(@options.detailsOuterContainer).hide duration, =>
				(@_$(@options.detailsOuterContainer).children().first().data Strings.widgetKey)?.destroy()
				@_$(@options.detailsOuterContainer).empty()
				@options.detailsShown = false
				@_$(@options.innerContainer).removeClass @options.withDetailsClass, duration/2
				@_$(@options.detailsButton)
					.empty()
					.append "<i class=\"icon-chevron-down\"></i>"
				@_changeInputState @options.detailsButton, "enabled"
				callback?()
		else
			@_$(@options.detailsOuterContainer).load @options.detailsFormRoute(@selected().data.id).url, =>
				@_$(@options.detailsOuterContainer).show duration
				@options.detailsShown = true
				@_$(@options.innerContainer).addClass @options.withDetailsClass, duration/2
				@_$(@options.detailsButton)
					.empty()
					.append "<i class=\"icon-chevron-up\"></i>"
				@_changeInputState @options.detailsButton, "enabled"
				callback?()

	_create: ->
		@options.editable ?= not not @_$(@options.addButton).length
		
		# handle add button click
		if @options.editable then @_on @_$(@options.addButton),
			click: (e) ->
				e.preventDefault()
				@_changeInputState @_$(@options.addButton), "loading"
				@options.addRoute().ajax
					success: (store) =>
						@_updateListItem(option = $("<option>"), store)
						@_$(@options.list)
							.append(option)
							.val(store.id)
							.change()
						@_toggleDetails null, false
						@_trigger "itemAdd", e,
							item: option
							data: store
					complete: =>
						@_changeInputState @_$(@options.addButton), "reset"
		
		@_on @_$(@options.detailsButton),
			click: (e) ->
				@_toggleDetails()
				e.preventDefault()
		
		@_on @_$(@options.detailsOuterContainer),
			"storeUpdate": (e, data) ->
				{ updatedData } = data
				if updatedData?
					@_updateListItem @selected().item, updatedData
		
		# handle delete store button click
		if @options.editable then @_on @_$(@options.deleteButton),
			click: (e) ->
				e.preventDefault()
				selected = @selected()
				@_changeInputState @_$(@options.deleteButton), "loading"
				if selected.data? then @options.deleteRoute(selected.data.id).ajax
					success: (store) =>
						next = $(selected.item).next()
						next = $(selected.item).prev() if not next.length
						if next.length
							@_$(@options.list)
								.val $(next).data(@options.storeKey)?.id
						$(selected.item).remove()
						@_trigger "itemRemove", e,
							item: selected.item
							data: store
					complete: =>
						@_changeInputState @_$(@options.deleteButton), "reset"
						# button reset sets a timeout to enables the button, so this makes sure it's disabled, if necessary
						window.setTimeout =>
							@_$(@options.list).change()
						, 0
		
		@_$(@options.detailsOuterContainer).hide()
		if @options.detailsShown
			@_toggleDetails 0, false
			
		# handle store list selection change
		@_on @_$(@options.list),
			change: (e) ->
				selected = @selected()
				@_changeInputState @options.deleteButton, "enabled", selected.data? and @options.editable
				@_changeInputState @options.detailsButton, "enabled", selected.data?
					
				if not @options.suppressOutput
					Widgets.moduleManager "option", "output", (selected.data ? null)
				if @options.detailsShown
					if selected.data?
						@_toggleDetails 0, false
					else
						@_toggleDetails null, true
				# for some reason, the second trigger doesn't work in all cases.
				$(@element).trigger "storeListSelectionChange", selected
				@_trigger "selectionchange", e, selected
		
		for input in [ @options.deleteButton, @options.detailsButton ]
			@_changeInputState input, "disabled"
		@_changeInputState @options.addButton, "enabled", @options.editable
		
		@_$(@options.list).tooltip()
		
	refresh: ->
		$(@element).data Strings.widgetKey, @
		
		if not @selected().item?.length
			# select the first store, if any
			firstItem = @_$(@options.list).children("option:first")
			if firstItem.length
				@_$(@options.list)
					.val($(firstItem).data @options.storeKey)?.id
		
		window.setTimeout =>
			@_$(@options.list).change()
		, 0
		
	_init: ->
		@refresh()
		
	_destroy: ->
		for input in [ @options.list, @options.addButton, @options.detailsButton, @options.deleteButton ]
			$(@_$ input).tooltip "destroy"

	_setOption: (key, value) ->
		switch key
			when "disabled"
				if value and @options.detailsShown
					@_toggleDetails 0
				for input in [ @options.list, @options.addButton, @options.detailsButton, @options.deleteButton ]
					@_changeInputState input, if value then "disabled" else "enabled"
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
			innerContainer: ".ctr-store-list-inner"
			list: ".lst-store"
			addButton: ".btn-add-store"
			detailsButton: ".btn-store-details"
			detailsOuterContainer: ".ctr-store-details-outer"
			deleteButton: ".btn-delete-store"
			withDetailsClass: "with-details"
			addRoute: jsRoutes.controllers.modules.CorpusModule.create
			detailsFormRoute: jsRoutes.controllers.CollectionsController.detailsForm
			deleteRoute: jsRoutes.controllers.CollectionsController.delete
			detailsShown: false
			suppressOutput: false
			storeKey: "store"

$.widget "widgets.storeList", Sare.Widget, widget