###
moduleView.html.js
###

$ = window.jQuery
jsRoutes = window.jsRoutes
JSON = window.JSON

$ ->
  $("#lst-corpora").change (e) =>
    $("#module-output").val(JSON.stringify $(e.target)
        .closest("select")
        .children("option:selected")
        .first()
        .data("store"))
