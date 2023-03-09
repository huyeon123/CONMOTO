$(function () {
    initNotyDialog();
});

function connectNotyService() {
    getUnreadNoty();
    const eventSource = createEventSource();
    addNotyListener(eventSource);
}

//미확인 알림 렌더링
function getUnreadNoty() {
    get("/api/noty/unread")
        .then(res => res.json())
        .then(data => {
            localStorage.setItem("curNotyNum", data.length);
            $("#js-noty-count").text(data.length);

            if (data.length !== 0) {
                $(".js-noty-empty").attr('hidden', true);
                data.forEach(item => {
                    $("#noty-dialog").prepend(createNotyHTML(item));
                });
            }
        });
}

//SSE 메세지 수신을 위한 EventSource 생성
function createEventSource() {
    return new EventSourcePolyfill("/api/noty/subscribe", {
        headers: {
            'Authorization': accessToken
        },
        heartbeatTimeout: 30 * 60 * 1000,
        withCredentials: true
    });
}

//SSE 메시시 수신 시 렌더링 후 알림창 OPEN
function addNotyListener(eventSource) {
    eventSource.addEventListener("message", function (event) {
        try {
            const data = JSON.parse(event.data)

            const $noty = $('#js-noty-count');
            const current = parseInt($noty.text());
            $noty.text(current + 1);
            localStorage.setItem("curNotyNum", (current + 1).toString());

            $("#noty-dialog").prepend(createNotyHTML(data));

            $(".js-noty-empty").attr('hidden', true);
            $("#noty").dialog("open");
        } catch (e) {
            console.error("[SSE 메세지 수신 에러]: " + e);
        }
    });
}

//NOTY 타입에 따른 Element 생성
function createNotyHTML(noty) {
    const defaultTypeNoty = [
        "GROUP_INVITE",
        "NOTICE",
        "GROUP_ROLE_CHANGE",
        "GROUP_EXPEL"
    ];

    let $element;
    if (defaultTypeNoty.includes(noty.type)) $element = drawDefaultNoty(noty);
    else $element = drawLongNoty(noty);

    if (noty.read) $element.addClass("read");

    addOnClick($element, noty);
    addTypeDiv($element, noty);

    return $element;
}

//타입에 따라 클릭 시 발생하는 이벤트 설정
function addOnClick($element, noty) {
    const canRedirectList = ["NOTICE", "BOARD_NEW_COMMENT"];

    if (canRedirectList.includes(noty.type)) {
        $element.attr("onclick", `moveToBoardAndRead(this)`) //게시글로 이동
            .addClass("pointer-hover");
    } else if (noty.type === "GROUP_INVITE" && !noty.read) { //그룹 초대 알림을 읽지않았으면
        $element.attr("onclick", `openJoinModal(this)`) //모달창 OPEN 가능
            .attr("data-payload", noty.payload)
            .addClass("pointer-hover");
    }
}

async function moveToBoardAndRead(element) {
    const $noty = $(element);
    const url = $noty.data("url");
    const notyId = $noty.attr('id').slice("noty_".length);
    setRead(notyId);
    location.href = url;
}

//그룹 가입 모달 오픈
function openJoinModal(element) {
    $("#modal-dimmed").show();
    createJoinModalHTML(element);
}

//그룹 가입 모달 Element 생성
function createJoinModalHTML(element) {
    const groupName = $(element).find(".noty-group-name").text();
    const notyId = $(element).attr("id").slice("noty_".length);

    const $modal = $(`
        <div class="custom-modal-wrapper">
            <div class="custom-modal-inner">
                <div class="custom-modal-title">${groupName}</div>
                <div class="custom-modal-main">
                    <input id="modal-join-nickname" class="form-control" type="text" placeholder="그룹에서 사용할 닉네임을 입력해주세요.">
                </div>
                <div class="flex-end">
                    <div>
                        <button class="noty-menu-button warn" onclick="closeJoinModal()">취소</button>
                        <button class="noty-menu-button" onclick="requestJoin(${notyId})">확인</button>
                    </div>
                </div>
            </div>
        </div>
    `);

    $("#modal").append($modal);
}

//그룹 가입 모달창 닫기
function closeJoinModal() {
    $("#modal-dimmed").hide();
    $("#modal").children().remove();
}

//그룹 가입 신청
function requestJoin(notyId) {
    const url = "/api/group/join";
    const request = {
        groupUrl: groupUrl,
        nickname: $("#modal-join-nickname").val(),
    }

    post(url, request)
        .then(res => res.text())
        .then(success => {
            if (success) alert("그룹에 가입했습니다!");
            else alert("그룹에 가입 신청했습니다.");
            setRead(notyId);
        })
}

function drawDefaultNoty(noty) {
    return $(`
        <li id="noty_${noty.id}" class="noty" data-url="${noty.url}">
            <div class="profile-thumbnail">
                <img src="/img/user.svg" alt="thumbnail" class="profile-thumbnail-img">
            </div>
            <div class="noty-content">
                <div class="noty-type"></div>
                <strong class="noty-content-header">${noty.title}</strong>
                <div class="noty-content-main">${noty.body}</div>
                <div class="noty-content-footer">
                    <div class="noty-group-name">${noty.groupName}</div>
                    <div class="time">${noty.sendTime}</div>
                </div>
            </div>
        </li>
    `);
}

function drawLongNoty(noty) {
    return $(`
        <li id="noty_${noty.id}" class="noty" data-url="${noty.url}">
            <div class="profile-thumbnail">
                <img src="/img/user.svg" alt="thumbnail" class="profile-thumbnail-img">
            </div>
            <div class="noty-content">
                <div class="noty-type"></div>
                <strong class="noty-content-header">${noty.title}</strong>
                <div class="noty-content-main">${noty.body}</div>
                <div class="noty-content-main">${noty.payload}</div>
                <div class="noty-content-main">${noty.senderName}</div>
                <div class="noty-content-footer">
                    <div class="noty-group-name">${noty.groupName}</div>
                    <div class="time">${noty.sendTime}</div>
                </div>
            </div>
        </div>
    `);
}

//알림 타입 스티커 부착
function addTypeDiv($element, noty) {
    const $typeWrapper = $element.find(".noty-type");
    $typeWrapper.prepend(renderType(noty));
}

//알림 타입별로 스티커 스타일 지정
function renderType(noty) {
    if (noty.type === "GROUP_INVITE") {
        return $(`<div class="invite">초대</div>`);
    } else if (noty.type === "NOTICE") {
        return $(`<div class="notice">공지사항</div>`);
    } else if (noty.type === "BOARD_NEW_COMMENT") {
        return $(`<div class="comment">댓글</div>`);
    } else if (noty.type === "GROUP_ROLE_CHANGE") {
        return $(`<div class="grade">등급변경</div>`);
    } else if (noty.type === "GROUP_EXPEL") {
        return $(`<div class="expel">강제퇴장</div>`);
    }
}

//단일 알림 읽음처리
function setRead(id) {
    const request = [id];
    requestSetRead(request);
    $("#noty_" + id).remove();

    const $noty = $('#js-noty-count');
    const now = parseInt($noty.text()) - 1;
    $noty.text(now);

    localStorage.setItem("curNotyNum", now.toString());

    if (now === 0) $(".js-noty-empty").attr('hidden', false);
}

//미확인 알림창에서의 모두 읽음처리
function setReadAll() {
    const request = [];

    $("#noty-dialog .noty").each((idx, item) => {
        request.push(parseInt(item.id.replace(/[^0-9]/g, "")));
    })

    if (request.length !== 0) {
        requestSetRead(request);
        $("#noty-dialog li").remove();
        $("#js-noty-count").text(0);
        $(".js-noty-empty").attr('hidden', false);
        localStorage.setItem("curNotyNum", "0");
    }
}

//해당 알림ID들 읽음 처리
function requestSetRead(request) {
    const url = "/api/noty";
    put(url, request).catch(error => console.error(error));
}

//미확인 알림창 설정
function initNotyDialog() {
    const $modal = $(`
        <div class="custom-modal-wrapper">
            <div class="custom-modal-inner">
                <div class="custom-modal-title">미확인 알림</div>
                <div class="custom-modal-main">
                    <div id="noty-dialog">
                        <div class="js-noty-empty">
                            미확인 알림이 존재하지 않습니다.
                        </div>
                    </div>
                </div>
                <div class="flex-space-between">
                    <button class="noty-menu-button warn" onclick="closeUnreadModal()">닫기</button>
                    <button class="noty-menu-button" onclick="setReadAll()">모두 읽음</button>
                </div>
            </div>
        </div>
    `);

    $("#noty").append($modal);
    initNotyDialogEvent();
}

function closeUnreadModal() {
    $("#noty").hide();
}

function initNotyDialogEvent() {
    //알림 클릭 시 알림창 열림
    $(".header-noty").click((e) => {
        e.stopPropagation();
        $("#noty").show();
    })

    //아무데나 클릭 시 알림창 닫힘
    $(document).mouseup((e) => {
        const noty = $("#noty");
        if (noty.has(e.target).length === 0) {
            noty.hide();
        }
    });
}