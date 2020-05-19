let limitSelectElement = jQuery("#limit-select");
let primarySortSelectElement = jQuery("#primary-sort");
let secondarySortSelectElement = jQuery("#secondary-sort");
let prevButtonElement = jQuery("#prev-btn");
let nextButtonElement = jQuery("#next-btn");
let lastPageElement = jQuery("#last-page");
let maxOffset = 0;

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
            let newURL = url + "?" + target + "=" + value;
            window.location.href = newURL;
        }
        else {
            let newURL = url + "&" + target + "=" + value;
            window.location.href = newURL;
        }
    }

    if (results[2] !== value) {
        let paramIndex = url.indexOf(results[0]);
        let valueStartIndex = paramIndex + (results[2] === "" ? results[0].length : results[0].indexOf(results[2]));
        let valueEndIndex = valueStartIndex + results[2].length;
        window.location.href = url.substr(0, valueStartIndex) + value + url.substr(valueEndIndex, url.length);
    }
    return true;
}

function formatUrl() {
    let query = getParameterByName("query") != null ? getParameterByName("query") : "";
    let title = getParameterByName("title") != null ? getParameterByName("title") : "";
    let year = getParameterByName("year") != null ? getParameterByName("year") : "";
    let director = getParameterByName("director") != null ? getParameterByName("director") : "";
    let star = getParameterByName("star") != null ? getParameterByName("star") : "";
    let genre = getParameterByName("genre") != null ? getParameterByName("genre") : "";
    let titleStart = getParameterByName("titlestart") != null ? getParameterByName("titlestart") : "";
    let limit = getParameterByName("limit") != null ? getParameterByName("limit") : "";
    let offset = getParameterByName("offset") != null ? getParameterByName("offset") : "";
    let psort = getParameterByName("psort") != null ? getParameterByName("psort") : "";
    let ssort = getParameterByName("ssort") != null ? getParameterByName("ssort") : "";
    return "api/movies?query=" + query + "&title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&genre=" + genre +
        "&titlestart=" + titleStart + "&limit=" + limit + "&offset=" + offset + "&psort=" + psort + "&ssort=" + ssort;
}

function handleCartResult(resultData, id, i) {
    console.log("handling cart response", id, i);
    console.log(resultData);
    let cartBtnElement = jQuery("#cart-btn-" + i);
    for (let j = 0; j < resultData.length; j++) {
        let movie = resultData[j];
        if (movie["id"] === id) {
            cartBtnElement.text("Added: " + movie["quantity"]);
        }
    }
}

function submitCartForm(id, i, formSubmitEvent) {
    console.log("submitted");
    console.log(i, id);
    $.ajax(
        "api/cart", {
            method: "POST",
            data: {"id": id, "op": "ADD"},
            success: (resultData) => handleCartResult(resultData, id, i)
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
    console.log(resultData);
    let moviesTableBodyElement = jQuery("#movies_table_body");

    let rowCount = resultData["rowCount"];
    lastPageElement.text(Math.floor(rowCount / limitSelectElement.val()) + 1);
    maxOffset = parseInt(rowCount);

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

        rowHTML += "<td><button id=\"cart-btn-" + i + "\" class=\"colored-btn\" onclick=\"submitCartForm('" + data[i]['movie_id'] + "', " + i + ");\">Add to Cart</button></td>";

        rowHTML += "</tr>";

        moviesTableBodyElement.append(rowHTML);
    }
}

function onSelectLimit() {
    setParameterByName("limit", limitSelectElement.val());
}

function onFirstPage() {
    setParameterByName("offset", 0);
}

function onLastPage() {
    let offset = maxOffset - maxOffset % limitSelectElement.val();
    if (offset !== 0 && offset === maxOffset) {
        offset += limitSelectElement.val();
    }
    setParameterByName("offset", offset);
}

function onPrevPage() {
    let currentOffset = getParameterByName("offset") ? parseInt(getParameterByName("offset")) : 0;
    let limitVal = parseInt(limitSelectElement.val());
    if (currentOffset - limitVal > 0) {
        setParameterByName("offset", currentOffset - limitVal);
    }
}

function onNextPage() {
    let currentOffset = getParameterByName("offset") ? parseInt(getParameterByName("offset")) : 0;
    let limitVal = parseInt(limitSelectElement.val());
    if (currentOffset + limitVal < maxOffset) {
        setParameterByName("offset",  currentOffset + limitVal);
    }
}

function onSelectPrimarySort() {
    setParameterByName("psort", primarySortSelectElement.val())
}

function onSelectSecondarySort() {
    setParameterByName("ssort", secondarySortSelectElement.val())
}

let limitParam = getParameterByName("limit");
limitSelectElement.val(limitParam ? limitParam : "10");

let psortParam = getParameterByName("psort");
primarySortSelectElement.val(psortParam ? psortParam : "rating-desc");
let ssortParam = getParameterByName("ssort");
secondarySortSelectElement.val(ssortParam ? ssortParam : "");

let currentOffset = getParameterByName("offset") ? parseInt(getParameterByName("offset")) : 0;
if (currentOffset === 0) {
    prevButtonElement.prop("disabled", true);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: formatUrl(),
    success: (resultData) => handleMoviesResult(resultData)
});