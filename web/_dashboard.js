let dashboardLoginForm = $("#dashboard-login-form");

function handleDashboardLoginResult(resultData) {
    console.log("Logging in", resultData)
    if (resultData["status"] === "success") {
        window.location.replace("dashboard.html");
    }
    else {
        $("#login-error-message").text(resultData["message"]);
    }
}

function submitDashboardLoginForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/dashboard-login", {
            method: "POST",
            data: dashboardLoginForm.serialize(),
            success: (resultData) => handleDashboardLoginResult(resultData)
        }
    );
}

dashboardLoginForm.submit(submitDashboardLoginForm);