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

function formatUrl() {
    let title = getParameterByName("title") != null ? getParameterByName("title") : "";
    let year = getParameterByName("year") != null ? getParameterByName("year") : "";
    let director = getParameterByName("director") != null ? getParameterByName("director") : "";
    let star = getParameterByName("star") != null ? getParameterByName("star") : "";
    return "api/movies?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star;
}

function handleMoviesResult(resultData) {
    let moviesTableBodyElement = jQuery("#movies_table_body");
    console.log(resultData);

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML +=
            "<td>" +
            '<a href="movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +
            '</a>' +

            "</td>";
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

        rowHTML += "<td><button class=\"colored-btn\">Add to Cart</button></td>";

        rowHTML += "</tr>";

        moviesTableBodyElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: formatUrl(),
    success: (resultData) => handleMoviesResult(resultData)
});