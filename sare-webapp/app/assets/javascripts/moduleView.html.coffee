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

#define reusables
location = window.location
$ = window.jQuery
jsRoutes = window.jsRoutes
Sare = window.Sare
Page = Sare.Page
Selectors = Page.Selectors
Strings = Page.Strings
Methods = Page.Methods

#define page constants
Strings.moduleOutputDataKey = "output"
Selectors.progressContainer = "#ctr-progress"
Selectors.nextModuleButton = "#btn-next-module"

# function to show the progress bar
Methods.showProgress = ->
  $(Selectors.progressContainer).show()

# function to hide the progress bar
Methods.hideProgress = ->
  $(Selectors.progressContainer).hide()

# function to set progress on the progress bar
Methods.setProgress = (percent) ->
  $(Selectors.progressContainer)
    .children(".bar").first()
      .css("width", percent + "%")

progressSimData =
  progress: 0
  handler: null

# function to simulate progress on the progress bar
Methods.simulateProgress = (timeStep, progressStep) ->
  timeStep = timeStep ? 100
  progressStep = progressStep ? 10
  Methods.showProgress()
  progressSimData.progress = 0
  simulator = ->
    Methods.setProgress progressSimData.progress
    if progressSimData.progress > 100
      progressSimData.progress = 100
    else if progressSimData.progress is 100
      progressSimData.progress = 0
    else progressSimData.progress += progressStep
    progressSimData.handler = window.setTimeout simulator, timeStep
  simulator()

# function to stop progress simulation
Methods.stopSimulatedProgress = ->
  window.clearTimeout(progressSimData.handler) if progressSimData.handler?
  Methods.setProgress 0
  Methods.hideProgress()

$ ->
  Methods.hideProgress()
  $(Selectors.nextModuleButton)
    .tooltip()
    .click =>
      output = $(Selectors.nextModuleButton).data(Strings.moduleOutputDataKey) ? "[]"
      location.href = jsRoutes.controllers.ModuleController.next(output).url