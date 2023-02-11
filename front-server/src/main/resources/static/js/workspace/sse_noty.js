$(function () {
    initNotyDialog();
    initWriteNicknameModal();
});

function connectNotyService() {
    getUnreadNoty();
    const eventSource = createEventSource();
    addNotyListener(eventSource);
}

function getUnreadNoty() {
    get("/api/noty/unread")
        .then(res => {
            if (canGetData(res)) {
                getJson(res).then(data => {
                    $("#js-noty-count").text(data.length);
                    data.forEach(item => {
                        addNoty(item);
                    });
                });
            }
        });
}

function createEventSource() {
    return new EventSourcePolyfill("/api/noty/subscribe", {
        headers: {
            'Authorization': accessToken
        },
        heartbeatTimeout: 30 * 60 * 1000,
        withCredentials: true
    });
}

function addNotyListener(eventSource) {
    eventSource.addEventListener("message", function (event) {
        try {
            const data = JSON.parse(event.data)

            const $noty = $('#js-noty-count');
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

    let notyOption = createNotyOption(data);

    $("#noty__option").append(notyOption);
    $(".js-noty-empty").attr('hidden', true);
}

function createNotyOption(data) {
    if (data.type === "GROUP_INVITE") {
        return `<div class="flex-end mb-1" id="noty_join_${data.payload}">
                <a onclick="setRead(${data.id})">거절</a>
                <a class="js-open-join-modal" onclick="openJoinModal('${data.payload}')">수락</a>
            </div>`;
    } else {
        $("#" + data.id).attr("onclick", `location.href='${data.url}'`);
        return `<div class="flex-end"><a onclick="setRead(${data.id})">확인</a></div>`;
    }
}

$(document).on('click', '.js-open-join-modal', (e) => {
    const groupId = $(e.target).parent().attr('id');
    $('.js-join-group').attr('id', groupId);


    const notyId = $(e.target).parents('.noty').attr('id');
    $('.js-join-noty').attr('id', notyId);

    openJoinModal();
})

function openJoinModal() {
    const $join = $('#join-modal');
    $join.show();
    $join.dialog("open");
}

function setRead(id) {
    const request = [id];
    requestSetRead(request);
    $("#noty_" + id).remove();

    const $noty = $('#js-noty-count');
    const now = parseInt($noty.text()) - 1;
    $noty.text(now);

    if (now === 0) $(".js-noty-empty").attr('hidden', false);
}

function setReadAll() {
    const request = [];

    $(".noty").each((idx, item) => {
        request.push(parseInt(item.id.replace(/[^0-9]/g, "")));
    })

    if (request.length !== 0) {
        requestSetRead(request);
        $(".js-noty-empty").attr('hidden', false);
    }
}

function requestSetRead(request) {
    const url = "/api/noty";
    put(url, request).catch(error => console.error(error));
}

function initNotyDialog() {
    $("#noty").dialog({
        title: "미확인 알림",
        minWidth: 430,
        maxHeight: 320,
        position: {
            my: "right top",
            at: "right-110 bottom",
            of: ".app-header"
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
                    $("#js-noty-count").text(0);
                    $(this).dialog("close");
                }
            }
        ]
    });

    $(".ui-dialog-content").css("padding", "0.5em");
    $(".ui-dialog-buttonpane").css("padding", 0);
    $(".ui-dialog-buttonset button").attr("class", "default-style-btn default");

    initNotyDialogEvent();
}

function initNotyDialogEvent() {
    $(".header-noty").click((e) => {
        e.stopPropagation();
        $("#noty").show();
        $("#noty").dialog("open");
    })

    $(".super-space-app").click(() => {
        $("#noty").dialog("close");
    })
}

function initWriteNicknameModal() {
    const width = innerWidth * 0.7;
    $("#join-modal").dialog({
        title: "그룹 닉네임",
        Width: width,
        minWidth: 430,
        maxHeight: 320,
        autoOpen: false,
        resizable: false,
        draggable: true,
        modal: true,
        buttons: [
            {
                text: "가입",
                click: function () {
                    joinGroup();
                    $("#invite-modal").dialog("close");
                },
                class: "js-join-request"
            }
        ]
    });

    $(".ui-dialog-content").css("padding", "0.5em");
    $(".ui-dialog-buttonpane").css("padding", 0);
    $(".ui-dialog-buttonset button").attr("class", "default-style-btn default");
}

function joinGroup() {
    const url = "/api/group/join";
    const request = {
        groupId: $(".js-join-group").attr('id').slice("noty_join_".length),
        nickname: $(".js-join-input-text").val()
    }

    post(url, request)
        .then(() => {
            alert("그룹에 가입되었습니다.");
            location.href = "/workspace/" + groupUrl;
        })
        .catch(error => {
            alert("그룹 가입에 실패했습니다!");
            console.error(error)
        });

    setRead($('.js-join-noty').attr('id').slice("noty_".length));
}