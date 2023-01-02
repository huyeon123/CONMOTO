$(window).on('load', () => {
    connectNotyService()
        .then(() => console.log("[NOTY] : EventStream Successfully Created."));
})

async function connectNotyService() {
    await get("/api/noty/unread")
        .then(res => {
            if (canGetData(res)) {
                getJson(res).then(data => {
                    $("#noty-count").text(data.length);
                    data.forEach(item => {
                        addNoty(item);
                    });
                });
            }
        })

    const eventSource = new EventSourcePolyfill("/api/noty/subscribe", {
        headers: {
            'Authorization': accessToken
        },
        heartbeatTimeout: 30 * 60 * 1000,
        withCredentials: true
    });

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
}

function addNoty(data) {
    $("#noty-container").prepend(
        `<div class="noty" id="noty_${data.id}">
                <div class="noty__profile">프로필</div>
                <div class="noty__content">
                    <div class="noty__header">
                        <div>${data.senderName}</div>
                        <div class="time">${data.sendTime}</div>
                    </div>
                    <pre class="message">${data.message}</pre>
                    <div id="noty__option"></div>
                </div>
        </div>`
    );

    let notyOption;
    if (data.type === "GROUP_INVITE") {
        notyOption =
            `<div class="flex-end mb-1">
                <a onclick="setRead(${data.id})">거절</a>
                <a onclick="acceptInvite('${data.id}', '${data.url}')">수락</a>
            </div>`;
    } else {
        $("#" + data.id).attr("onclick", `location.href='${data.url}'`);
        notyOption =
            `<div class="flex-end"><a onclick="setRead(${data.id})">확인</a></div>`
    }

    $("#noty__option").append(notyOption);
}

function acceptInvite(id, groupUrl) {
    const url = "/api/group/" + groupUrl + "/join";
    get(url)
        .then(res => {
            if (res.ok) {
                alert("그룹에 가입되었습니다.");
                location.href = "/workspace/" + groupUrl;
            } else {
                getJson(res).then(error => alert(error));
            }
        })
        .catch(error => console.error(error));
    setRead(id);
}

function setRead(id) {
    const request = [id];
    requestSetRead(request);
    $("#noty_" + id).remove();

    const $noty = $('#noty-count');
    const current = parseInt($noty.text());
    $noty.text(current - 1);
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
    const request = [];

    $(".noty").each((idx, item) => {
        request.push(parseInt(item.id.replace(/[^0-9]/g, "")));
    })

    requestSetRead(request);
}

function requestSetRead(request) {
    const url = "/api/noty";
    put(url, request).catch(error => console.error(error));
}