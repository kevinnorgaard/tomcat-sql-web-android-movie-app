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
    let genre = getParameterByName("genre") != null ? getParameterByName("genre") : "";
    let titlestart = getParameterByName("titlestart") != null ? getParameterByName("titlestart") : "";
    return "api/movies?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&genre=" + genre + "&titlestart=" + titlestart;
}

function handleCartResult(resultData) {
    console.log("handling cart response");
    console.log(resultData);
}

function submitLoginForm(id, formSubmitEvent) {
    console.log("submitted");
    console.log(id);
    $.ajax(
        "api/cart", {
            method: "POST",
            data: {"id": id},
            success: handleCartResult
        }
    );
}

function sortGenres(a, b) {
    return a["genre"].localeCompare(b["genre"]);
}

function sortStars(a, b) {
    let diff = b["star_feature_count"] - a["star_feature_count"];
    if (diff === 0) {
        return a["star_name"].localeCompare(b["star_name"])
    }
    return diff;
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
        let genres = resultData[i]["movie_genres"].sort(sortGenres);
        for (let genre of genres) {
            rowHTML += "<a href=\"movies.html?genre=" + genre["genre"] + "\">" + genre["genre"] + "</a><br>";
        }
        rowHTML += "</td>";

        rowHTML += "<td>";
        let stars = resultData[i]["movie_stars"].sort(sortStars);
        for (let star of stars) {
            rowHTML += '<a class=\"star-item\" href="star.html?id=' + star["star_id"] + '">' + star["star_name"] + "</a><br>";
        }
        rowHTML += "</td>";

        rowHTML += "<td><button class=\"colored-btn\" onclick=\"submitLoginForm('" + resultData[i]['movie_id'] + "');\">Add to Cart</button></td>";

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