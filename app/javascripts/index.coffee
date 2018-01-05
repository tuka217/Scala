$ ->
  $.get "/users", (data) ->
    $.each data, (index, item) ->
      $("#users").append $("<li>").text item.username