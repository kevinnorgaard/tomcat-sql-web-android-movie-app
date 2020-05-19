let cartForm = $("#add-to-cart-form");
let movieId = getParameterByName('id');

function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
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

function handleResult(resultData) {
    console.log(resultData);

    let backBtn = jQuery("#back-btn");
    if (resultData["prevParams"] != null) {
        backBtn.attr("href", "movies.html?" + resultData["prevParams"]);
    } else {
        backBtn.attr("href", "movies.html");
    }

    let movieInfoElement = jQuery("#movie_info");

    let data = resultData["data"];

    movieInfoElement.append(
        "<label>Title</label><p class=detail>" + data["movie_title"] + "</p>" +
        "<label>Release Year</label><p class=detail>" + data["movie_year"] + "</p>" +
        "<label>Director</label><p class=detail>" + data["movie_director"] + "</p>" +
        "<label>Rating</label><p class=detail>" + data["movie_ratings"] + "</p>");

    let genresTableBodyElement = jQuery("#genres_table_body");
    let starsTableBodyElement = jQuery("#stars_table_body");

    let genresHTML = "";
    let starsHTML = "";

    let genres = data["movie_genres"].sort(sortGenres);
    for (let genre of genres) {
        genresHTML += "<tr><td><a href=\"movies.html?genre=" + genre["genre"] + "\">" + genre["genre"] + "</a></td></tr>";
    }

    let stars = data["movie_stars"].sort(sortStars);
    for (let star of stars) {
        starsHTML += '<tr><td><a href="star.html?id=' + star["star_id"] + '">' + star["star_name"] + "</a></td></tr>";
    }

    $("#id").val(data["movie_id"]);

    genresTableBodyElement.append(genresHTML);
    starsTableBodyElement.append(starsHTML);
}

function handleCartResult(resultData) {
    console.log("handling cart response");
    console.log(resultData);
    let cartBtnElement = $("#cart-btn");
    for (let i = 0; i < resultData.length; i++) {
        let movie = resultData[i];
        if (movie["id"] === movieId) {
            cartBtnElement.val("Added: " + movie["quantity"]);
        }
    }
}

function submitCartForm(formSubmitEvent) {
    console.log("submitted");
    console.log(cartForm.serialize());
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/cart", {
            method: "POST",
            data: cartForm.serialize(),
            success: handleCartResult
        }
    );
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movie?id=" + movieId,
    success: handleResult
});

cartForm.submit(submitCartForm);