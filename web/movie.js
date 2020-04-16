let cartForm = $("#add-to-cart-form");

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
    console.log("Loading movie data into tables");
    console.log(resultData);
    let movieInfoElement = jQuery("#movie_info");

    movieInfoElement.append(
        "<label>Title</label><p class=detail>" + resultData[0]["movie_title"] + "</p>" +
        "<label>Release Year</label><p class=detail>" + resultData[0]["movie_year"] + "</p>" +
        "<label>Director</label><p class=detail>" + resultData[0]["movie_director"] + "</p>" +
        "<label>Ratings</label><p class=detail>" + resultData[0]["movie_ratings"] + "</p>");

    let genresTableBodyElement = jQuery("#genres_table_body");
    let starsTableBodyElement = jQuery("#stars_table_body");

    let genresHTML = "";
    let starsHTML = "";

    for (let genre of resultData[0]["movie_genres"]) {
        genresHTML += "<tr><td>" + genre["genre"] + "</td></tr>";
    }

    for (let star of resultData[0]["movie_stars"]) {
        starsHTML += '<tr><td><a href="star.html?id=' + star["star_id"] + '">' + star["star_name"] + "</a></td></tr>";
    }

    $("#id").val(resultData[0]["movie_id"]);

    genresTableBodyElement.append(genresHTML);
    starsTableBodyElement.append(starsHTML);
}

function handleCartResult(resultData) {
    console.log("handling cart response");
    console.log(resultData);
}

function submitLoginForm(formSubmitEvent) {
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

let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movie?id=" + movieId,
    success: handleResult
});

cartForm.submit(submitLoginForm);