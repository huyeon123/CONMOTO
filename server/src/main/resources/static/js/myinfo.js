$(function () {
    const width = window.innerWidth * 0.8;
    const height = width * 0.8;

    $("#account").dialog({
        minWidth: width,
        minHeight: height,
        position: {
            my: "center",
            at: "center",
            of: window
        },
        dialogClass: "no-close",
        modal: true,
        autoOpen: false,
        resizable: false,
        draggable: false,
    });

    $(".ui-dialog").css("padding", 0);

    $("#account-button").click((e) => {
        $("#account").dialog("open");
    })
});

