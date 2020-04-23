let limitSelectElement = jQuery("#limit-select");

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

function setParameterByName(target, value) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results || !results[2]) {
        if (url.indexOf("?") === -1) {
            window.location.href = url + "?" + target + "=" + value;
        }
        else {
            window.location.href = newUrl = url + "&" + target + "=" + value;
        }
    }

    if (results[2] !== value) {
        let paramIndex = url.indexOf(results[0]);
        let valueStartIndex = paramIndex + results[0].indexOf(results[2]);
        let valueEndIndex = valueStartIndex + results[2].length;
        window.location.href = url.substr(0, valueStartIndex) + value + url.substr(valueEndIndex, url.length);
    }
    return true;
}

function formatUrl() {
    let title = getParameterByName("title") != null ? getParameterByName("title") : "";
    let year = getParameterByName("year") != null ? getParameterByName("year") : "";
    let director = getParameterByName("director") != null ? getParameterByName("director") : "";
    let star = getParameterByName("star") != null ? getParameterByName("star") : "";
    let genre = getParameterByName("genre") != null ? getParameterByName("genre") : "";
    let titleStart = getParameterByName("titlestart") != null ? getParameterByName("titlestart") : "";
    let limit = getParameterByName("limit") != null ? getParameterByName("limit") : "";
    let offset = getParameterByName("offset") != null ? getParameterByName("offset") : "";
    return "api/movies?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&genre=" + genre +
        "&titlestart=" + titleStart + "&limit=" + limit + "&offset=" + offset;
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

    let data = resultData["data"];
    for (let i = 0; i < data.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<td><a href=\"movie.html?id=" + data[i]['movie_id'] + "\">" + data[i]["movie_title"] + "</a></td>";
        rowHTML += "<td>" + data[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + data[i]["movie_director"] + "</td>";
        rowHTML += "<td>" + data[i]["movie_ratings"] + "</td>";

        rowHTML += "<td>";
        let genres = data[i]["movie_genres"].sort(sortGenres);
        for (let genre of genres) {
            rowHTML += "<a href=\"movies.html?genre=" + genre["genre"] + "\">" + genre["genre"] + "</a><br>";
        }
        rowHTML += "</td>";

        rowHTML += "<td>";
        let stars = data[i]["movie_stars"].sort(sortStars);
        for (let star of stars) {
            rowHTML += "<a class=\"star-item\" href=\"star.html?id=" + star["star_id"] + "\">" + star["star_name"] + "</a><br>";
        }
        rowHTML += "</td>";

        rowHTML += "<td><button class=\"colored-btn\" onclick=\"submitLoginForm('" + data[i]['movie_id'] + "');\">Add to Cart</button></td>";

        rowHTML += "</tr>";

        moviesTableBodyElement.append(rowHTML);
    }
}

function onSelectLimit() {
    setParameterByName("limit", limitSelectElement.val())
}

function onPrevPage() {
    let currentOffset = getParameterByName("offset") ? parseInt(getParameterByName("offset")) : 0;
    let limitVal = parseInt(limitSelectElement.val());
    setParameterByName("offset", currentOffset - limitVal > 0 ? currentOffset - limitVal : 0);
}

function onNextPage() {
    let currentOffset = getParameterByName("offset") ? parseInt(getParameterByName("offset")) : 0;
    let limitVal = parseInt(limitSelectElement.val());
    setParameterByName("offset", currentOffset + limitVal);
}

let limitParam = getParameterByName("limit");
limitSelectElement.val(limitParam ? limitParam : "10");

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: formatUrl(),
    success: (resultData) => handleMoviesResult(resultData)
});