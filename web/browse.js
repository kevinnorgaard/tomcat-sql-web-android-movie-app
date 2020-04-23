function sortGenres(a, b) {
    return a["genre_name"].localeCompare(b["genre_name"])
}

function handleGenresResult(resultData) {
    let genresTableBodyElement = jQuery("#genres_table_body");

    let sortedResult = resultData.sort(sortGenres);
    for (let i = 0; i < sortedResult.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr><td><a href=\"movies.html?genre=" + sortedResult[i]["genre_name"] + "\">" + sortedResult[i]["genre_name"] + "</a></td></tr>";

        genresTableBodyElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: 'api/genres',
    success: (resultData) => handleGenresResult(resultData)
});