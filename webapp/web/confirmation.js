let confirmationTableBody = $("#confirmation_table_body");

function handlePaymentResult(resultData) {
    console.log(resultData);

    let total = 0;
    for (let i = 0; i < resultData.length; i++) {
        total += resultData[i]["quantity"] * resultData[i]["price"];
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML += "<td>" + resultData[i]["id"] + "</td>";
        rowHTML += "<td>" + resultData[i]["title"] + "</td>";
        rowHTML += "<td>" + resultData[i]["quantity"] + "</td>";
        rowHTML += "<td class=\"price\">$" + resultData[i]["price"] + "</td>";
        rowHTML += "</tr>";

        confirmationTableBody.append(rowHTML);
    }

    let totalElement = jQuery("#total");
    totalElement.text("Total: $" + Math.round(total * 100) / 100);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/payment",
    success: (resultData) => handlePaymentResult(resultData)
});