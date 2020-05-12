function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    console.log(resultData);

    let backBtn = jQuery("#back-btn");
    backBtn.attr("href", "movies.html?" + resultData["prevParams"]);

    let starInfoElement = jQuery("#star_info");

    let data = resultData["data"];

    starInfoElement.append("<label>Name</label><p class=detail>" + data["star_name"] + "</p>" +
        "<label>Birth Year</label><p class=detail>" + data["star_birthyear"] + "</p>");

    let starTableBodyElement = jQuery("#star_table_body");

    let rowHTML = "";
    for (let movie of data["star_movies"]) {
        rowHTML += '<tr><td><a href="movie.html?id=' + movie["movie_id"] + '">' + movie["movie_title"] + "</a></td></tr>";
    }

    starTableBodyElement.append(rowHTML);
}

let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});