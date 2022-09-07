$(function () {
    const eventSource = new EventSource("/api/noty/subscribe/default");

    eventSource.addEventListener("message", function (event) {
        try {
            const data = JSON.parse(event.data)

            const $noty = $('#noty-count');
            const current = parseInt($noty.text());
            $noty.text(current + 1);

            addNoty(data);

            $("#noty").dialog("open");
        } catch (e) {
            console.error("JSON")
        }
    });
});

function addNoty(data) {
    $("#noty-container").append(
        `<div class="noty" id="${data.id}">
                <div class="profile">프로필</div>
                    <div class="noty-content">
                        <div class="noty-header">
                            <div>${data.senderName}</div>
                            <div class="time">${data.sendTime}</div>
                        </div>
                    <pre class="message">${data.message}</pre>
                </div>
            </div>`
    )
}

//알림 다이얼로그
$(function () {
    $("#noty").dialog({
        title: "미확인 알림",
        minWidth: 430,
        maxHeight: 320,
        position: {
            my: "right top",
            at: "right-110 bottom",
            of: ".header"
        },
        autoOpen: false,
        resizable: false,
        draggable: true,
        show: {
            effect: "slideDown",
        },
        hide: {
            effect: "fold"
        },
        buttons: [
            {
                text: "모두 읽음",
                icon: "ui-icon-trash",
                click: function () {
                    setReadAll();
                    $(".noty").remove();
                    $("#noty-count").text(0);
                    $(this).dialog("close");
                }
            }
        ]
    });

    $(".ui-dialog-content").css("padding", "0.5em");
    $(".ui-dialog-buttonpane").css("padding", 0);
    $(".ui-dialog-buttonset button").attr("class", "btn-primary");

    $("#noty-button").click((e) => {
        $("#noty").dialog("open");
    })
});

function setReadAll() {
    const url = "/api/noty";
    const request = [];

    $(".noty").each((idx, item) => {
        console.log(item.id);
        request.push(item.id);
    })

    put(url, request)
        .catch(error => console.error(error));
}