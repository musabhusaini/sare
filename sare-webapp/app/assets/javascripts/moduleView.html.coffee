###
moduleView.html.js
###

$ = window.jQuery
jsRoutes = window.jsRoutes

$ ->
  $("#btn-next").click(=>
    output = $("#module-output").val()
    window.location.href = jsRoutes.controllers.ModuleController.next(output).url
  )
