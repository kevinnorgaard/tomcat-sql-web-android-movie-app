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

    let movieInfoElement = jQuery("#movie_info");

    movieInfoElement.append(
        "<label>Movie Title</label><p class=detail>" + resultData[0]["movie_title"] + "</p>" +
        "<label>Release Year</label><p class=detail>" + resultData[0]["movie_year"] + "</p>" +
        "<label>Director</label><p class=detail>" + resultData[0]["movie_director"] + "</p>" +
        "<label>Ratings</label><p class=detail>" + resultData[0]["movie_ratings"] + "</p>");

    // console.log("handleResult: populating movie table from resultData");

    let genresTableBodyElement = jQuery("#genres_table_body");
    let starsTableBodyElement = jQuery("#stars_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let genresHTML = "";
        let starsHTML = "";

        for (let genre of resultData[i]["movie_genres"]) {
            genresHTML += "<tr><td>" + genre["genre"] + "</td></tr>";
        }

        for (let star of resultData[i]["movie_stars"]) {
            starsHTML += '<tr><td><a href="star.html?id=' + star["star_id"] + '">' + star["star_name"] + "</a></td></tr>";
        }

        genresTableBodyElement.append(genresHTML);
        starsTableBodyElement.append(starsHTML);
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