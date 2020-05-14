function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");
    console.log("sending AJAX request to backend Java Servlet", "movie-suggestion?query=" + escape(query));

    // TODO: if you want to check past query results first, you can do it here

    jQuery.ajax({
        "method": "GET",
        "url": "movie-suggestion?query=" + escape(query),
        "success": function(resultData) {
            handleLookupAjaxSuccess(resultData, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error");
            console.log(errorData)
        }
    })
}

function handleLookupAjaxSuccess(resultData, query, doneCallback) {
    console.log("lookup ajax successful");
    console.log("query:", query);

    console.log(resultData);
    console.log(resultData);

    // TODO: if you want to cache the result into a global variable you can do it here

    doneCallback( { suggestions: resultData } );
}


function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["heroID"])
}


$('#autocomplete').autocomplete({
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode === 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button