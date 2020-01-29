const reloadTimeout = 5000; // reload timeout in milliseconds
var currentData; // currently saved data

$(document).ready(() => {
  // continuously fetches data from fetch API node
  setInterval(reloadData, reloadTimeout);
});

/**
 * Loads data from the "fetch" RESTful API node and then calls `updateTable()`
 */
function reloadData() {
  $.ajax({
    url: "/api/fetch",
    type: "get",
    success: function(data) {
      updateTable(data);
    },
    error: function(err) {
      console.log(err);
    }
  });
}

/**
 * Updates the leaderboard table accordingly to newly received data
 *
 * @param data newly received data
 */
function updateTable(data) {
  if (!currentData) {
    currentData = data; // save data
    return;
  }

  if (JSON.stringify(data) === JSON.stringify(currentData)) {
    return; // no change
  }
  currentData = data; // data changed, save new data

  // fade out table body
  $("#leaderboard_body").animate({ opacity: 0 }, function() {
    $(this).remove(); // remove table body
    // construct new table body
    var html = `<tbody style="display: none;" class="table table-striped leaderboard_row" id="leaderboard_body">`;
    for (var i = 0; i < currentData.length; i++) {
      var dataRow = currentData[i];
      html += `<tr`;
      switch (i) {
        case 0:
          html += ` class="first_place"`;
          break;
        case 1:
          html += ` class="second_place"`;
          break;
        case 2:
          html += ` class="third_place"`;
          break;
      }
      html += `><th scope="row">${i + 1}
      <td>${dataRow["name"]}</td>
      <td>${dataRow["score"]}</td>
      <td>${dataRow["message"]}</td>
      </tr>`;
    }
    html += `</tbody>`;
    // add new table body to table
    $(html).insertAfter("#leaderboard_head");
    // fade in new table
    $("#leaderboard_body").fadeIn();
  });
}
