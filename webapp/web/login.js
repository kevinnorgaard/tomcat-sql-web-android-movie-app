let loginForm = $("#login-form");

function handleLoginResult(resultData) {
    console.log(resultData);

    if (resultData["gRecatchaError"]) {
        $("#login-error-message").text(resultData["gRecatchaError"]);
    } else {
        if (resultData["status"] === "success") {
            window.location.replace("index.html");
        }
        else {
            $("#login-error-message").text(resultData["message"]);
        }
    }
}

function submitLoginForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/login", {
            method: "POST",
            data: loginForm.serialize(),
            success: (resultData) => handleLoginResult(resultData)
        }
    );
}

loginForm.submit(submitLoginForm);