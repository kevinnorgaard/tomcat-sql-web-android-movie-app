let storage = window.localStorage;

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");

    let cachedResult = storage.getItem(query);
    if (cachedResult != null) {
        console.log("Found query results in cache", storage);
        doneCallback( { suggestions: JSON.parse(cachedResult) } );
        return;
    }

    console.log("Sending AJAX request to server");
    jQuery.ajax({
        "method": "GET",
        "url": "api/movie-suggestion?query=" + escape(query),
        "success": function(resultData) {
            handleLookupAjaxSuccess(resultData, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("Lookup AJAX error");
            console.log(errorData)
        }
    })
}

function handleLookupAjaxSuccess(resultData, query, doneCallback) {
    console.log("Lookup AJAX successful", resultData);

    // Cache result
    storage.setItem(query, JSON.stringify(resultData));

    doneCallback( { suggestions: resultData } );
}


function handleSelectSuggestion(suggestion) {
    console.log("Selected movie " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"]);

    window.location.href = "movie.html?id=" + suggestion["data"]["movieId"];
}


$('#autocomplete').autocomplete({
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    deferRequestBy: 300,
    minChars: 3
});


function handleNormalSearch() {
    let query = $('#autocomplete').val();

    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here

    window.location.href = "movies.html?query=" + query;
}

$('#autocomplete').keypress(function(event) {
    if (event.keyCode === 13) {
        handleNormalSearch();
    }
});

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button