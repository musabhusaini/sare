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
Selectors = Page.Selectors
Strings = Page.Strings
Widgets = Page.Widgets

Math = window.Math

widget =
	show: ->
		$(@element).show()
	
	hide: ->
		$(@element).hide()
	
	reset: ->
		$(@element).children(@options.bar)
			.width "0%"
		
	animate: ->
		animateProgress = (progress, duration, callback) =>
			duration ?= @options.animationTimeout
			progress ?= 0
			$(@element).children(@options.bar)
				.animate
					width: "#{progress}%"
				, duration, ->
					callback?()

		redeem = =>
			@_delay ->
				@options.redeemAjax
					success: (progressToken) =>
						if not progressToken.progress?
							animateProgress 100, null, =>
								@_delay ->
									@hide()
									@options.callback? progressToken
								, @options.pingTimeout
						else
							animateProgress Math.round(100 * progressToken.progress)
							redeem()
			, @options.pingTimeout
		
		@reset()
		@show()
		redeem()

	_create: ->
		
	_destroy: ->
		
	_setOption: (key, value) ->
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		bar: ".bar"
		animationTimeout: 500
		pingTimeout: 500
		callback: (data) -> return
		redeemAjax: -> return

$.widget "widgets.progress", Sare.Widget, widget