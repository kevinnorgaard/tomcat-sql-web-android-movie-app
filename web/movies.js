function handleMoviesResult(resultData) {
    if (resultData["redirect"]) {
        window.location.href = 'login.html';
    }
    else {
        let moviesTableBodyElement = jQuery("#movies_table_body");

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

            rowHTML += "</tr>";

            moviesTableBodyElement.append(rowHTML);
        }
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by MoviesServlet
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});