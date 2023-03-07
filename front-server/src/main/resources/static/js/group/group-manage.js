/*그룹 관리 페이지 메뉴 버튼*/
function moveToMemberManagePage() {
    location.href = "./manage/members";
}

function moveToCategoryManagePage() {
    location.href = "./manage/category";
}

function moveToJoinManagePage() {
    location.href = "./manage/join";
}

function moveToGradeManagePage() {
    location.href = "./manage/grade";
}

/*그룹 관리 페이지 정보 변경*/
function saveGroupName() {
    const changedName = $("#group-info-name").val();
    const request = {name: changedName};

    saveGroupInfo("name", request);
}

function saveGroupDescription() {
    const changedDescription = $('#group-description').val();
    const request = {description: changedDescription};

    saveGroupInfo("description", request);
}

//radio button event
$("input:radio[name='group-open']").change(saveGroupOpenStatus)
$("input:radio[name='group-auto-join']").change(saveGroupAutoJoinStatus)

function saveGroupOpenStatus() {
    const changedOpenStatus = $('input:radio[name="group-open"]:checked').val();
    const request = {open: changedOpenStatus};

    saveGroupInfo("open", request);
}

function saveGroupAutoJoinStatus() {
    const changedAutoJoinStatus = $('input:radio[name="group-auto-join"]:checked').val();
    const request = {autoJoin: changedAutoJoinStatus};

    saveGroupInfo("autoJoin", request);
}

function saveGroupInfo(type, request) {
    const url = "/api/group/" + groupUrl + "?type=" + type;
    put(url, request)
        .then(res => {
            if (!res.ok) alert("반영에 실패했습니다.");
        }).catch(error => console.error(error));
}

function resignReq() {
    const input = prompt("정말로 삭제하시겠습니까?\n삭제하려면 '삭제한다'를 입력해주세요.", "'삭제한다'를 입력하세요.");
    if (input !== "삭제한다") return;

    delWithoutBody("/api/group/" + groupUrl)
        .then(res => {
            if (res.ok) {
                alert("그룹이 삭제되었습니다.");
                location.href = "/community";
            } else {
                res.text().then(message => alert(message));
            }
        })
}

/*가입 관리 페이지*/
//선택한 가입자 수 변경
$(document).on("click", ".applicant-check", () => {
    const applicantCount = $(".applicant-check:checked").length;
    $("#applicant-count").text(applicantCount);
})

function pageRender() {
    const exist = $("#join-request-list").length === 1;
    if (exist) renderJoinRequest();
}

function renderJoinRequest() {
    const url = "/api/group/join-request-list?groupUrl=" + groupUrl;
    get(url)
        .then(res => res.json())
        .then(data => {
            const requesters = data.requesters;
            requesters.forEach(requester => appendJoinRequestMemberHTML(requester));
        })
}

function appendJoinRequestMemberHTML(requester) {
    const $element = $(`
        <li class="member-info">
                    <div class="profile flex-space-between">
                        <div class="flex-horizon-center">
                            <div class="profile-thumbnail">
                                <img src="/img/user.svg" alt="thumbnail" class="profile-thumbnail-img">
                            </div>
                            <div>
                                <div class="js-member-nickname-email">${requester.nickname} (${requester.userEmail})</div>
                                <div class="member-info-detail">
                                    <span>신청일 ${requester.timestamp}</span>
                                </div>
                            </div>
                        </div>
                        <div class="m-3">
                            <input type="checkbox" class="applicant-check">
                        </div>
                    </div>
                </li>
    `);

    $("#join-request-list").append($element);
}

function selectAll() {
    const $applicant = $(".applicant-check");

    //모두 다 체크되있는지 확인
    let alreadyChecked = true;
    for (let key of $applicant) {
        if (!$(key).is(":checked")) {
            alreadyChecked = false;
        }
    }

    //이미 전체선택이 되었다면 전체 취소 아니면 전체 선택
    if (alreadyChecked) {
        $applicant.prop("checked", false);
    } else {
        $applicant.prop("checked", true);
    }
}

function acceptJoin() {
    const url = "/api/group/accept-join";
    const request = {
        groupUrl: groupUrl,
        requesters: []
    }

    const $applicant = $(".applicant-check");
    for (let key of $applicant) {
        if ($(key).is(":checked")) {
            const text = $($(key).parents(".member-info")).find(".js-member-nickname-email").text();
            const nicknameAndEmail = text.split(" ");
            const nickname = nicknameAndEmail[0];
            const email = nicknameAndEmail[1].replace(/[()]/g, "");
            const item = {
                userEmail: email,
                nickname: nickname
            }

            request.requesters.push(item);
        }
    }

    post(url, request)
        .then(res => {
            if (res.ok) {
                alert("정상 반영되었습니다.");
                location.reload();
            }
        })
}

function showSearchResults() {
    const searchText = $("#member-search").val();
    const seeAll = (searchText === "");

    const regex = new RegExp(searchText, "gi");

    //멤버들 중 닉네임 혹은 이메일이 검색어와 일치하면 해당 멤버 엘리먼트만 display
    $("#join-request-list .member-info").each((idx, element) => {
        const $element = $(element);
        if (seeAll) {
            $element.show();
        } else {
            const itemText = $element.find(".js-member-nickname-email").text();
            if (itemText.match(regex)) $element.show();
            else $element.hide();
        }
    });
}