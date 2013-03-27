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

$ = window.jQuery
Sare = window.Sare

class Sare.Widget extends $.Widget
	constructor: ->
		super()
	
	_$: (s) ->
		$(@element).find s
	
	_tooltipCache: {}
	
	_removeTooltip: (input) ->
		id = @_$(input).attr "id"
		if id?
			@_tooltipCache[id] = @_$(input).data("tooltip")?.options
		@_$(input).tooltip "destroy"
	
	_restoreTooltip: (input) ->
		options = {}
		id = @_$(input).attr "id"
		if id?
			options = @_tooltipCache[id]
			delete @_tooltipCache[id]
		@_$(input).tooltip options
	
	_changeInputState: (input, state, value) ->
		value ?= true
		switch state
			when "enabled"
				@_restoreTooltip input
				@_$(input).prop "disabled", not value
			when "disabled"
				@_removeTooltip input
				@_$(input).prop "disabled", value
			when "loading"
				@_removeTooltip input
				@_$(input).button state
			when "reset"
				@_restoreTooltip input
				@_$(input).button state
