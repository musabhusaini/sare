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

widget =
	_makeAspectNode: (aspect, metadata) ->
		metadata = $.extend {}, metadata ? {},
			id: aspect.id
			aspect: aspect
		node =
			data:
				title: aspect.title
				attr:
					id: "aspect-node-#{aspect.id}"
			children: if aspect.children? then (@_makeAspectNode(child) for child in (aspect.children)) else null
			metadata: metadata
		node.state = "closed" if node.children?
		node

	_makeKeywordNode: (keyword, metadata) ->
		metadata = $.extend {}, metadata ? {},
			id: keyword.id
			keyword: keyword
		data:
			title: keyword.content
			icon: jsRoutes.controllers.Assets.at("/plugins/jstree/themes/misc/file.png").url
			attr:
				id: "keyword-node-#{keyword.id}"
		metadata: metadata

	getSelectedAspect: ->
		node = @_$(@options.aspectsContainer).jstree "get_selected"
		node: node
		data: node.data @options.aspectKey
	
	addAspect: (lexicon, title, superficial, skipRename) ->
		display = (aspect) =>
			node = @_makeAspectNode aspect,
				superficial: superficial
			@_$(@options.aspectsContainer).jstree "create",
				@_$(@options.aspectsContainer), "last", node, null , skipRename ? false
		
		lexicon ?= @options.lexicon
		if superficial
			display title
		else
			aspect = title: (title ? null)
			@options.addAspectRoute(lexicon.id).ajax
				contentType: Helpers.ContentTypes.json
				data: JSON.stringify aspect
				success: display

	removeAspect: (node, superficial) ->
		display = (node) => @_$(@options.aspectsContainer).jstree "remove", node
		node = node ? @getSelectedAspect().node
		if superficial
			display node
		else
			aspect = $(node).data @options.aspectKey
			@options.deleteAspectRoute(null, aspect.id).ajax
				success: => display node

	getSelectedKeyword: ->
		node = @_$(@options.keywordsContainer).jstree "get_selected"
		node: node
		data: node.data @options.keywordKey

	getLexicon: ->
		@options.lexicon
	
	hasAspect: (lexicon, aspect, recursive) ->
		lexicon ?= @options.lexicon
		return false if not lexicon or not aspect?
		response = @options.getAspectRoute(lexicon.id, aspect.title ? aspect, not not recursive).ajax
			async: false
		response.status is 200
	
	hasKeyword: (aspect, keyword, recursive) ->
		aspect ?= @getSelectedAspect().data
		return false if not aspect? or not keyword?
		response = @options.getKeywordRoute(aspect.id, keyword.content ? keyword, not not recursive).ajax
			async: false
		response.status is 200
	
	addKeyword: (aspect, content, superficial, skipRename) ->
		display = (keyword) =>
			if aspect.id isnt @getSelectedAspect().data?.id then return
			node = @_makeKeywordNode keyword,
				superficial: superficial
			@_$(@options.keywordsContainer).jstree "create",
				@_$(@options.keywordsContainer), "inside", node, null, skipRename ? false
		
		aspect ?= @getSelectedAspect().data
		return if not aspect?
		
		if superficial
			display content
		else
			keyword = content: (content ? null)
			@options.addKeywordRoute(aspect.id).ajax
				contentType: Helpers.ContentTypes.json
				data: JSON.stringify keyword
				success: display
	
	removeKeyword: (node, superficial) ->
		display = (node) => @_$(@options.keywordsContainer).jstree "remove", node
		node = node ? @getSelectedKeyword().node
		if superficial
			display node
		else
			keyword = $(node).data @options.keywordKey
			@options.deleteKeywordRoute(null, keyword.id).ajax
				success: => display node
	
	_create: ->
		superficialKey = "superficial"
		@options.lexicon ?= $(@element).data @options.lexiconKey
		
		editAspectInputs = [ @options.updateAspectButton, @options.deleteAspectButton ]
		(aspectInputs = editAspectInputs.slice()).push @options.addAspectButton
		
		editKeywordInputs = [ @options.updateKeywordButton, @options.deleteKeywordButton ]
		(keywordInputs = editKeywordInputs.slice()).push @options.addKeywordButton

		state = {}

		selectNode = (tree, node) ->
			prev = $(tree).jstree "get_selected"
			node ?= $(tree).find "li:eq(0)"
			$(tree)
				.jstree("select_node", $ node)
				.jstree "deselect_node", $ prev

		populateKeywords = (aspect, keywords) =>
			nodes = @_$(@options.keywordsContainer).find "li"
			if nodes.length then @_$(@options.keywordsContainer).jstree "delete_node", nodes, true
			@addKeyword(aspect, keyword, true, true) for keyword in (keywords ? [])
			window.setTimeout =>
				selectNode @_$ @options.keywordsContainer
			, 0
		
		@_$(@options.aspectsContainer).on
			"rename_node.jstree": (e, data) =>
				aspect = $(data.rslt.obj).data @options.aspectKey
				@options.updateAspectRoute(null, aspect.id).ajax
					contentType: Helpers.ContentTypes.json
					data: JSON.stringify
						title: data.rslt.name
					success: (aspect) =>
						$(data.rslt.obj).data @options.aspectKey, aspect
					error: ->
						$.jstree.rollback data.rlbk
		
			"prepare_move.jstree": (e, data) =>
				state.aspectsRlbk = @_$(@options.aspectsContainer).jstree "get_rollback"
				state.keywordsRlbk = @_$(@options.keywordsContainer).jstree "get_rollback"
			
			"move_node.jstree": (e, data) =>
				aspect = $(data.rslt.o).data @options.aspectKey
				lexicon = $(data.rslt.np).data @options.aspectKey
				
				if not aspect?
					# handle the case of moving a keyword into an aspect.
					keyword = $(data.rslt.o).data @options.keywordKey
					if keyword?
						revertKeywords = =>
							$.jstree.rollback state.keywordsRlbk
							state.keywordsRlbk = null
							
						$.jstree.rollback state.aspectsRlbk
						state.aspectsRlbk = null
						if not lexicon?
							revertKeywords()
						else
							@options.updateKeywordRoute(lexicon.id, keyword.id).ajax
								error: =>
									revertKeywords()
						return true
				
				lexicon ?= @options.lexicon
				@options.updateAspectRoute(lexicon.id, aspect.id).ajax
					success: (aspect) =>
						$(data.rslt.o).data @options.aspectKey, aspect
					error: ->
						$.jstree.rollback data.rlbk
		
			"select_node.jstree": (e, data) =>
				@_changeInputState(input, "enabled") for input in editAspectInputs
				@_changeInputState(input, "enabled") for input in keywordInputs
				@_changeInputState(input, "disabled") for input in editKeywordInputs
				
				aspect = $(data.rslt.obj).data @options.aspectKey
				@options.getKeywordsRoute(aspect.id).ajax
					success: (keywords) =>
						populateKeywords aspect, keywords
						# want this to execute in the end so as to override other focus operations.
						window.setTimeout ->
							data.inst.set_focus()
						, 0
					error: =>
						populateKeywords null, []
		
			"deselect_node.jstree": (e, data) =>
				if not data.inst.get_selected().length
					@_changeInputState(input, "disabled") for input in editAspectInputs
					@_changeInputState(input, "disabled") for input in keywordInputs
					populateKeywords null, []
		
			"create_node.jstree": (e, data) =>
				if not $(data.rslt.obj).data superficialKey
					selectNode @_$(@options.aspectsContainer), data.rslt.obj
		
			"delete_node.jstree": (e, data) =>
				prev = data.rslt.prev
				prev = @_$(@options.aspectsContainer).find("li:eq(0)") if not prev?[0]
				if not prev?[0]
					@_$(@options.keywordsContainer).jstree "delete_node", @_$(@options.keywordsContainer).find("li")
				else
					selectNode @_$(@options.aspectsContainer), prev

		@_$(@options.aspectsContainer).jstree
			ui:
				select_limit: 1
			json_data:
				data: []
				ajax:
					url: (node) =>
						@options.getLexiconRoute($(node).data(@options.aspectKey).id).url
					success: (data) =>
						@_makeAspectNode(data).children
			crrm:
				move:
					check_move: (m) =>
						aspect = $(m.o).data @options.aspectKey
						lexicon = $(m.np).data @options.aspectKey
						
						if not aspect?
							# handle the case of moving a keyword into an aspect.
							keyword = $(m.o).data @options.keywordKey
							if keyword?
								if not lexicon?
									return false;
								else
									return not @hasKeyword lexicon, keyword
						true
			dnd:
				drag_target: @options.draggableObject
				drop_target: false
				drag_check: (data) =>
					keyword = $(data.o).data(@options.lemmaKey) ? $(data.o).text()
					aspect = $(data.r).data @options.aspectKey
					return false if not aspect? or not keyword
					after: false
					before: false
					inside: not @hasKeyword aspect, keyword
				drag_finish: (data) =>
					keyword = $(data.o).data(@options.lemmaKey) ? $(data.o).text()
					aspect = $(data.r).data @options.aspectKey
					if aspect? and keyword
						@addKeyword aspect, keyword, false, true
			hotkeys:
				insert: => @addAspect()
				del: => @removeAspect()
			plugins: [ "themes", "json_data", "ui", "crrm", "dnd", "sort", "hotkeys" ]
		
		# do this later so we don't end up getting overridden.
		window.setTimeout =>
			@_$(@options.aspectsContainer).jstree "set_focus"
			@addAspect(@options.lexicon, aspect, true, true) for aspect in (@options.lexicon.children ? [])
			window.setTimeout =>
				selectNode @_$ @options.aspectsContainer
			, 0
		, 0
		
		@_$(@options.addAspectButton)
			.tooltip()
			.click => @addAspect()
		
		@_$(@options.updateAspectButton)
			.tooltip()
			.click =>
				@_$(@options.aspectsContainer).jstree "rename", null

		@_$(@options.deleteAspectButton)
			.tooltip()
			.click => @removeAspect()
		
		@_changeInputState(input, "disabled") for input in editAspectInputs
		
		trees = @_$(@options.aspectsContainer).add @_$ @options.keywordsContainer
		trees.on "click", "a", (e) =>
			$(e.target).focus()

		@_on $(window.document),
			"click": (e) ->
				if not $(e.target).closest(trees).length
					$(trees).jstree "unset_focus"

		@_$(@options.keywordsContainer).on
			"rename_node.jstree": (e, data) =>
				keyword = $(data.rslt.obj).data @options.keywordKey
				@options.updateKeywordRoute(null, keyword.id).ajax
					contentType: Helpers.ContentTypes.json
					data: JSON.stringify
						content: data.rslt.name
					success: (keyword) =>
						$(data.rslt.obj).data @options.keywordKey, keyword
						$(@element).trigger "aspectLexiconKeywordRenamed",
							keyword: state.prevKeyword
							result: data.rslt.name
					error: ->
						$.jstree.rollback data.rlbk
			
			"select_node.jstree": (e, data) =>
				@_changeInputState(input, "enabled") for input in keywordInputs
				data.inst.set_focus()
			
			"deselect_node.jstree": (e, data) =>
				if not data.inst.get_selected().length
					@_changeInputState(input, "disabled") for input in editKeywordInputs
			
			"create_node.jstree": (e, data) =>
				if not $(data.rslt.obj).data superficialKey
					selectNode @_$(@options.keywordsContainer), data.rslt.obj
				$(@element).trigger "aspectLexiconKeywordAdded", $(data.rslt.obj).data @options.keywordKey
			
			"delete_node.jstree": (e, data) =>
				prev = data.rslt.prev
				prev = @_$(@options.keywordsContainer).find("li:eq(0)") if not prev?[0]
				if not not prev?[0] then selectNode @_$(@options.keywordsContainer), prev
				if not data.args[1]
					$(@element).trigger "aspectLexiconKeywordRemoved", $(data.rslt.obj).data @options.keywordKey
			
		@_$(@options.keywordsContainer).jstree
			crrm:
				move:
					check_move: -> false
			ui:
				select_limit: 1
			json_data:
				data: []
			dnd:
				drag_target: @options.draggableObject
				drop_target: false
				drag_check: (data) =>
					keyword = $(data.o).data(@options.lemmaKey) ? $(data.o).text()
					return false if not @getSelectedAspect().data? or not keyword
					possible = not @hasKeyword null, keyword
					after: possible
					before: possible
					inside: not not data.is_root
				drag_finish: (data) =>
					keyword = $(data.o).data(@options.lemmaKey) ? $(data.o).text()
					if @getSelectedAspect().data? and keyword
						@addKeyword null, keyword, false, true
			hotkeys:
				insert: => @addKeyword()
				del: => @removeKeyword()
			plugins: [ "themes", "json_data", "ui", "crrm", "dnd", "sort", "hotkeys" ]
		
		@_$(@options.addKeywordButton)
			.tooltip()
			.click => @addKeyword()

		@_$(@options.updateKeywordButton)
			.tooltip()
			.click =>
				state.prevKeyword = @getSelectedKeyword().data
				@_$(@options.keywordsContainer).jstree "rename", null
		
		@_$(@options.deleteKeywordButton)
			.tooltip()
			.click => @removeKeyword()
		
		@_changeInputState(input, "disabled") for input in keywordInputs
		
	refresh: ->
		$(@element).data Strings.widgetKey, @
		
	_init: ->
		@refresh()
		
	_destroy: ->
		
	_setOption: (key, value) ->
		$.Widget.prototype._setOption.apply @, arguments
	
	_getCreateOptions: ->
		draggableObject: ".lexicon-draggable"
		aspectsContainer: ".ctr-aspects"
		addAspectButton: ".btn-add-aspect"
		updateAspectButton: ".btn-update-aspect"
		deleteAspectButton: ".btn-delete-aspect"
		keywordsContainer: ".ctr-keywords"
		addKeywordButton: ".btn-add-keyword"
		updateKeywordButton: ".btn-update-keyword"
		deleteKeywordButton: ".btn-delete-keyword"
		getLexiconRoute: jsRoutes.controllers.CollectionsController.get
		getAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.getAspect
		addAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.addAspect
		updateAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.updateAspect
		deleteAspectRoute: jsRoutes.controllers.modules.AspectLexBuilder.deleteAspect
		getKeywordRoute: jsRoutes.controllers.modules.AspectLexBuilder.getExpression
		getKeywordsRoute: jsRoutes.controllers.modules.AspectLexBuilder.getExpressions
		addKeywordRoute: jsRoutes.controllers.modules.AspectLexBuilder.addExpression
		updateKeywordRoute: jsRoutes.controllers.modules.AspectLexBuilder.updateExpression
		deleteKeywordRoute: jsRoutes.controllers.modules.AspectLexBuilder.deleteExpression
		lexiconKey: "lexicon"
		aspectKey: "aspect"
		keywordKey: "keyword"
		lemmaKey: "lemma"

$.widget "widgets.aspectLexicon", Sare.Widget, widget