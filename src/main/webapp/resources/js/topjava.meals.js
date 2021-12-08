const mealAjaxUrl = "ui/meals/";

const ctx = {
    ajaxUrl: mealAjaxUrl
};

function clearFilters() {
    $('#filter input').val("");
    updateTable();
}

function filter() {
    $.ajax({
        url: ctx.ajaxUrl + "filter",
        data:$('#filter input').serialize(),
        type: "GET"
    }).done( function (data) {
        fillTable(data);
        successNoty("Filtered");
    })
}

$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ]
        })
    );
});