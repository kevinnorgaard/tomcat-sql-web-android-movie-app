let paymentForm = $("#payment-form");

function handleCartResult(resultData) {
    console.log(resultData);

    let totalElement = jQuery("#total");

    let total = 0;
    for (let i = 0; i < resultData.length; i++) {
        total += resultData[i]["quantity"] * resultData[i]["price"];
    }

    totalElement.text("Total: $" + Math.round(total * 100) / 100);
}

function handlePaymentResult(resultData) {
    console.log(resultData);
    if (resultData["processed"]) {
        window.location.href = "confirmation.html";
    }
    else {
        $("#payment-error-message").text("Payment information is invalid");
    }
}

function submitPaymentForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/payment", {
            method: "POST",
            data: paymentForm.serialize(),
            success: (resultData) => handlePaymentResult(resultData)
        }
    );
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cart",
    success: (resultData) => handleCartResult(resultData)
});

paymentForm.submit(submitPaymentForm);