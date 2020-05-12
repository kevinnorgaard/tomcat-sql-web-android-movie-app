let cartTableBodyElement = jQuery("#cart_table_body");

function handleCartResult(resultData) {
    console.log(resultData);

    let total = 0;
    for (let i = 0; i < resultData.length; i++) {
        total += resultData[i]["quantity"] * resultData[i]["price"];
        let rowHTML = "";

        rowHTML += "<tr>";
        rowHTML += "<td>" + resultData[i]["title"] + "</td>";
        rowHTML += "<td><i onclick=\"decrementQuantity('" + resultData[i]["id"] + "')\" class=\"fa fa-minus\"></i>" +
            resultData[i]["quantity"] +
            "<i onclick=\"incrementQuantity('" + resultData[i]["id"] + "')\" class=\"fa fa-plus\"></i>" +
            "<i onclick=\"removeItem('" + resultData[i]["id"] + "')\" class=\"fa fa-trash\"></i></td>";
        rowHTML += "<td class=\"price\">$" + resultData[i]["price"] + "</td>";
        rowHTML += "</tr>";

        cartTableBodyElement.append(rowHTML);
    }

    let totalElement = jQuery("#total");
    totalElement.text("Total: $" + Math.round(total * 100) / 100);
}

function handleUpdateCartResult(resultData) {
    cartTableBodyElement.empty();
    handleCartResult(resultData);
    console.log("handling cart response");
    console.log(resultData);
}

function decrementQuantity(id) {
    console.log(id);
    $.ajax(
        "api/cart", {
            method: "POST",
            data: {"id": id, "op": "DECREMENT"},
            success: handleUpdateCartResult
        }
    );
}

function removeItem(id) {
    console.log(id);
    $.ajax(
        "api/cart", {
            method: "POST",
            data: {"id": id, "op": "REMOVE"},
            success: handleUpdateCartResult
        }
    );
}

function incrementQuantity(id) {
    console.log(id);
    $.ajax(
        "api/cart", {
            method: "POST",
            data: {"id": id, "op": "INCREMENT"},
            success: handleUpdateCartResult
        }
    );
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cart",
    success: (resultData) => handleCartResult(resultData)
});