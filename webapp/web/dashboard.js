let insertNewStarForm = jQuery("#insert-new-star-form");
let insertNewMovieForm = jQuery("#insert-new-movie-form");

function handleDatabaseMetadataResult(resultData) {
    console.log(resultData);
    let dbMetadataTableBodyElement = jQuery("#db_metadata_table_body");

    let rowHTML = "<tr>";

    for (let i = 0; i < resultData.length; i++) {
        let table = resultData[i];
        let columns = table["columns"];

        rowHTML += "<td>" + table["tableName"] + "</td>";

        rowHTML += "<td>";
        for (let i = 0; i < columns.length; i++) {
            let column = columns[i];
            rowHTML += column["columnName"] + ": " + column["columnType"] + "(" + column["columnSize"] + ")" + "<br>";
        }
        rowHTML += "</td>";

        rowHTML += "</tr>";
    }

    dbMetadataTableBodyElement.append(rowHTML);
}

function handleInsertNewStarFormResult(resultData) {
    console.log(resultData);

    let insertNewStarSuccessMessageElement = jQuery("#insert-new-star-success-message");
    if (resultData["status"] === "success") {
        insertNewStarSuccessMessageElement.text("Success: Inserted new star with ID " + resultData["row"]["id"]);
        let insertStarNameInputElement = jQuery("#insert-star-name");
        let insertStarBirthyearInputElement = jQuery("#insert-star-birthyear");
        insertStarNameInputElement.val("");
        insertStarBirthyearInputElement.val("");
    }
}

function handleInsertNewMovieFormResult(resultData) {
    console.log(resultData);

    let insertNewMovieAddedMessageElement = jQuery("#insert-new-movie-added-message");
    let insertNewMovieStarAddedMessageElement = jQuery("#insert-new-movie-star-added-message");
    let insertNewMovieGenreAddedMessageElement = jQuery("#insert-new-movie-genre-added-message");
    if (resultData["movie-added"]) {
        let movieMsg = "Success: Inserted new movie with ID " + resultData["movie-id"] + ".";
        let starMsg = "";
        if (resultData["star-added"]) {
            starMsg += "Inserted new star with ID " + resultData["star-id"] + ".";
        }
        else {
            starMsg += "Used existing star with ID " + resultData["star-id"] + ".";
        }
        let genreMsg = "";
        if (resultData["genre-added"]) {
            genreMsg += "Inserted new genre with ID " + resultData["genre-id"] + ".";
        }
        else {
            genreMsg += "Used existing genre with ID " + resultData["genre-id"] + ".";
        }
        insertNewMovieAddedMessageElement.text(movieMsg);
        insertNewMovieStarAddedMessageElement.text(starMsg);
        insertNewMovieGenreAddedMessageElement.text(genreMsg);

        let insertMovieNameInputElement = jQuery("#insert-movie-name");
        let insertMovieYearInputElement = jQuery("#insert-movie-year");
        let insertMovieDirectorInputElement = jQuery("#insert-movie-director");
        let insertStarNameInputElement = jQuery("#insert-movie-star-name");
        let insertGenreNameInputElement = jQuery("#insert-movie-genre-name");
        insertMovieNameInputElement.val("");
        insertMovieYearInputElement.val("");
        insertMovieDirectorInputElement.val("");
        insertStarNameInputElement.val("");
        insertGenreNameInputElement.val("");
    }
    else {
        insertNewMovieAddedMessageElement.text("Error: Movie with ID " + resultData["movie-id"] + " already exists");
    }
}

function submitInsertNewStarForm(formSubmitEvent) {
    console.log("submitted");
    console.log(insertNewStarForm.serialize());
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/dashboard", {
            method: "POST",
            data: insertNewStarForm.serialize(),
            success: handleInsertNewStarFormResult
        }
    );
}

function submitInsertNewMovieForm(formSubmitEvent) {
    console.log("submitted");
    console.log(insertNewMovieForm.serialize());
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/dashboard", {
            method: "POST",
            data: insertNewMovieForm.serialize(),
            success: handleInsertNewMovieFormResult
        }
    );
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: handleDatabaseMetadataResult
});

insertNewStarForm.submit(submitInsertNewStarForm);
insertNewMovieForm.submit(submitInsertNewMovieForm);