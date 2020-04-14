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

    console.log("handleResult: populating star info from resultData");

    let starInfoElement = jQuery("#movie_info");

    starInfoElement.append("<p>Movie Name: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Year: " + resultData[0]["movie_year"] + "</p>");

    // console.log("handleResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" + resultData[i]["movie_title"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_ratings"] + "</td>";

        rowHTML += "<td>";
        for (let genre of resultData[i]["movie_genres"]) {
            rowHTML += genre["genre"] + "<br>";
        }
        rowHTML += "</td>";

        rowHTML += "<td>";
        for (let star of resultData[i]["movie_stars"]) {
            rowHTML += '<a href="star.html?id=' + star["star_id"] + '">' + star["star_name"] + "</a><br>";
        }
        rowHTML += "</td>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});