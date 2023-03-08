function moveToCategoryPage(element) {
    location.href = "/community/" + groupUrl + "/" + $(element).text().replaceAll(" ", "_");
}

function moveToGroupJoinPage() {
    location.href = "/community/" + groupUrl + "/apply/join";
}

function moveToGroupManagingPage() {
    location.href = "/community/" + groupUrl + "/manage";
}

function moveToGroup(element) {
    const url = $(element).data("url");
    location.href = "/community/" + url + "?tab=🔥한눈에 보기";
}

function moveToMainPage(element) {
    location.href = "/community/" + groupUrl + "?tab=" + element.innerText;
}

function moveToMemberPage(element) {
    location.href = "/community/" + groupUrl + "/manage/members/" + element.id;
}

function moveToBoardPage(boardId) {
    location.href = "/community/" + groupUrl + "/board/" + boardId;
}

function goToEditor() {
    location.href = "/community/" + groupUrl + "/board/editor";
}

function logout() {
    get("/auth/logout")
        .then(res => {
            if (res.ok) location.href = "/";
        })
        .catch(error => {
            alert("로그아웃 실패");
            console.error(error);
        })
}

$(document).on('mouseover', '.super-space-app__sidebar', () => {
    $("#sidebar__fold").animate({
        opacity: 1
    }, 'fast');
})

$(document).on('mouseleave', '.super-space-app__sidebar', () => {
    $("#sidebar__fold").animate({
        opacity: 0
    }, 'fast');
})

$(document).on('click', '#sidebar__fold', () => {
    $(".super-space-app__sidebar")
        .addClass("closed")
        .removeClass("open");
    $("#js-sidebar-expand-btn").show();
    offDimmed();
})

function openSideBar() {
    $("#js-sidebar-expand-btn").hide();
    $(".super-space-app__sidebar")
        .addClass("open")
        .removeClass("closed");
    onDimmed();
}

$(document).on('click', '.sidebar-mobile-dimmed', () => {
    $(".super-space-app__sidebar")
        .addClass("closed")
        .removeClass("open");
    $(".sidebar-mobile-dimmed").hide();
})

let sidebarHeaderSize;

//사이드바 렌더링
$(() => {
    sidebarHeaderSize = getSidebarHeaderSize() + 25 + 20;
    const maxHeight = adjustSideBarMainHeight();
    addGoTopIfOverflow(maxHeight);
    dimmedSideBar();
    $(".super-space-app__sidebar").show();
})

function adjustSideBarMainHeight() {
    const viewHeight = window.innerHeight;
    const maxHeight = viewHeight - sidebarHeaderSize;

    const $scroll = $(".sidebar-menu-scroll");
    $scroll.css("maxHeight", maxHeight);

    return maxHeight;
}

function addGoTopIfOverflow(maxHeight) {
    const $scroll = $(".sidebar-menu-scroll");
    const height = $scroll.height();

    if (height >= maxHeight - 10) {
        const $goTop = $(".sidebar-go-top");
        $goTop.css('display', 'flex');
        $goTop.click(() => {
            $scroll.scrollTop(0);
        })

        const $category = $(".category");
        $category.css("padding-bottom", "15px");
        $category.css("border-bottom", "1px solid #eee");
    }
}

$(window).resize(() => {
    dimmedSideBar();
    adjustSideBarMainHeight();
})

function dimmedSideBar() {

    const width = window.outerWidth;

    //모바일
    if (width < 768) $(".super-space-app__sidebar")
        .addClass("closed")
        .removeClass("open")

    else $(".super-space-app__sidebar")
        .addClass("open")
        .removeClass("closed")

    $(".sidebar-mobile-dimmed").hide();
}

function onDimmed() {
    const width = window.outerWidth;
    if (width < 768) $(".sidebar-mobile-dimmed").show();
}

function offDimmed() {
    const width = window.outerWidth;
    if (width < 768) $(".sidebar-mobile-dimmed").hide();
}

function getSidebarHeaderSize() {
    return $(".sidebar > header").height();
}

$(".sidebar-header .expand").click((e) => {
    const $main = $(e.target).parents(".sidebar-section").find("main");
    $main.toggle();
})

function openInviteModal() {
    $("#modal-dimmed").show();

    const $element = $(`
        <div class="custom-modal-wrapper">
            <div class="custom-modal-inner">
                <div class="custom-modal-title">현재 그룹에 멤버를 초대합니다</div>
                <div class="custom-modal-main">
                    <span class="custom-input-label mb-1">이메일</span>
                    <input id="modal-invite-nickname" class="form-control" type="text" placeholder="초대할 멤버의 이메일을 입력해주세요.">
                </div>
                <div class="flex-end">
                    <div>
                        <button id="grade-modal-cancel" class="manage-menu-button warn" onclick="closeInviteModal()">취소</button>
                        <button id="grade-modal-save" class="manage-menu-button default" onclick="inviteMember()">초대</button>
                    </div>
                </div>
            </div>
        </div>
    `);

    $("#modal").append($element);
    $("#modal").show();
}

function inviteMember() {
    const url = "/api/group/" + groupUrl + "/invite";
    const request = {email: $("#modal-invite-nickname").val()};
    post(url, request)
        .then(res => {
            if (res.ok) {
                alert("해당 유저에게 초대메세지를 보냈습니다.");
            } else {
                alert("초대에 실패했습니다.");
            }
        })
}

function closeInviteModal() {
    $("#modal-dimmed").hide();
    $("#modal").hide()
        .children().remove();
}

/*사이드바 카테고리 그룹핑*/
const $customCategory = $(".custom-category");
const categoryLength = $customCategory.children().length;
let groupIdx = [];
$customCategory.children(".js-category-group").each((idx, element) => {
    groupIdx.push($(element).index());
})

for (let i = 0; i < groupIdx.length; i++) {
    let targetDom = [];
    let onlyItemDom = [];
    const start = groupIdx[i];
    const end = (i === groupIdx.length - 1) ? categoryLength : groupIdx[i + 1];
    const $custom = $(".custom-category > div");

    for (let j = start; j < end; j++) {
        const item = $custom.get(j);
        targetDom.push(item);
        if (j > start) onlyItemDom.push(item); //그룹을 제외한 나머지 요소 묶기
    }
    $(targetDom).wrapAll(`<div class="sidebar-category-group-wrapper"></div>`);
    const groupItem = $(onlyItemDom).wrapAll(`<div class="js-group-item"></div>`);
    const isFold = $($custom.get(start)).data("fold");
    if (isFold) {
        groupItem.parent().hide();
    }
}

//카테고리 그룹 접기/펼치기
$("i.js-category-expand").click((e) => {
    $($(e.target).parents(".sidebar-category-group-wrapper"))
        .find(".js-group-item").toggle();
})

//dimmed 클릭 시 모달 닫기
$("#modal-dimmed").click(() => {
    $("#modal-dimmed").hide();
    $("#modal")
        .hide()
        .removeClass("wide-style")
        .children().remove();
})

function moveToGroupMain() {
    location.href = "/community/" + groupUrl;
}

function openUserFloat() {
    $("#user-floating").show();
}

$(document).mouseup((e) => {
    const userFloat = $("#user-floating");
    if(userFloat.has(e.target).length === 0){
        userFloat.hide();
    }
});

function openMyAccountModal() {
    $("#user-floating").hide();
    $("#modal-dimmed").show();
    $("#modal").show();

    appendMyAccountModal();
    renderMyInfo();
}

function renderMyInfo() {
    const url = "/api/user";

    get(url)
        .then(res => res.json())
        .then(data => {
            $("#account-modal-email").val(data.email);
            $("#account-modal-name").val(data.name);
            $("#account-modal-birthday").val(data.birthday);

            if (data.expireIn == null) appendDangerousZone();
            else appendResignCancel();
        });
}

function appendDangerousZone() {
    const $element = $(`
        <div>
            <h3 class="mb-3">Dangerous Zone</h3>
            <div class="alert alert-warning">
                <p>회원탈퇴 요청 시 해당 계정이 15일 간 만료 대기 상태에 들어갑니다.</p>
                <p>만료 대기 상태에서는 평소처럼 CONMOTO 서비스를 사용할 수 있습니다.</p>
                <p>또한, 언제든지 탈퇴를 철회할 수 있습니다.</p>
            </div>
            <div class="flex-end">
                <button class="manage-menu-button warn" onclick="resignService()">회원 탈퇴</button>
            </div>
        </div>
    `);

    $("#account-modal-main").append($element);
}

function appendResignCancel() {
    const $element = $(`
        <div>
            <h3 class="mb-3">회원탈퇴 취소</h3>
            <div class="alert alert-warning">
                <p>아직 계정이 삭제되지 않았습니다!</p>
                <p>언제든지 탈퇴를 철회할 수 있습니다.</p>
            </div>
            <div class="flex-end">
                <button class="manage-menu-button warn" onclick="resignCancel()">회원 탈퇴</button>
            </div>
        </div>
    `);

    $("#account-modal-main").append($element);
}

function resignCancel() {
    const url = "/api/user/resign";

    put(url)
        .then(res => {
            if (res.ok) {
                alert("탈퇴가 철회되었습니다!\n다시 돌아오신 것을 환영합니다!");
            } else {
                alert("탈퇴 철회에 실패했습니다. 잠시 후 다시 시도해주세요.");
            }
        })
}

function appendMyAccountModal() {
    const $element = createMyAccountModalHTML();
    $("#modal").append($element);
}

function createMyAccountModalHTML() {
    $(".custom-modal").addClass("wide-style");

    return $(`
        <div class="custom-modal-wrapper">
            <div class="custom-modal-inner">
                <div class="custom-modal-title">내 계정</div>
                <div id="account-modal-main" class="custom-modal-main">
                    <div class="pb-3 mb-3 bb"> 
                        <div class="mb-2">
                            <span class="custom-input-label">내 이메일</span>
                            <input id="account-modal-email" class="form-control" type="text" value="로딩 중..." readonly> 
                        </div>
                        <div class="mb-2">
                            <span class="custom-input-label">내 이름</span>
                            <input id="account-modal-name" class="form-control" type="text" value="로딩 중..."> 
                        </div>
                        <div class="flex-end mt-2">
                            <button class="manage-menu-button default" onclick="saveMyName()">저장</button>
                        </div>
                    </div>
                    <div class="pb-3 mb-3 bb">
                        <h4>비밀번호 변경</h4>
                        <div class="mb-2">
                            <span class="custom-input-label">현재 비밀번호</span>
                            <input id="account-modal-current" type="password" class="form-control" placeholder="본인확인을 위해 현재 사용중인 비밀번호를 입력해주세요.">
                        </div>
                        <div class="mb-2">
                            <span class="custom-input-label">현재 비밀번호</span>
                            <input id="account-modal-change" type="password" class="form-control" placeholder="앞으로 사용할 비밀번호를 입력해주세요.">
                        </div>
                        <div class="mb-2">
                            <span class="custom-input-label">바꿀 비밀번호</span>
                            <input id="account-modal-again" type="password" class="form-control" placeholder="한 번 더 입력해주세요.">
                        </div>
                        <div class="flex-end mt-2">
                            <button class="manage-menu-button default" onclick="saveMyPassword()">저장</button>
                        </div>
                    </div>
                    <div class="pb-3 mb-3 bb"> 
                        <h4>생년월일 변경</h4>
                        <div class="mb-2">
                            <span class="custom-input-label">생년월일</span>
                            <input id="account-modal-birthday" type="date" class="form-control">
                        </div>
                        <div class="flex-end mt-2">
                            <button class="manage-menu-button default" onclick="saveMyBirthday()">저장</button>
                        </div>                    
                    </div>
                </div>
            </div>
        </div>
    `);
}

function saveMyName() {
    const url = "/api/user/edit/name";
    const request = {
        name: $("#account-modal-name").val()
    }

    put(url, request)
        .then(res => res.json())
        .then(data => {
            if (data.success) alert("정상 반영되었습니다!");
            else alert(data.message);
        });
}

function saveMyBirthday() {
    const url = "/api/user/edit/birthday";
    const request = {
        birthday: $("#account-modal-birthday").val()
    }

    put(url, request)
        .then(res => res.json())
        .then(data => {
            if (data.success) alert("정상 반영되었습니다!");
            else alert(data.message);
        });
}

function saveMyPassword() {
    const url = "/api/user/edit/password";
    const request = {
        current: $("#account-modal-current").val(),
        password: $("#account-modal-change").val(),
        again: $("#account-modal-again").val()
    }

    put(url, request)
        .then(res => res.json())
        .then(data => {
            if (data.success) alert("정상 반영되었습니다!");
            else alert(data.message);
        });
}

function resignService() {
    const url = "/api/user/resign";

    delWithoutBody(url)
        .then(res => {
            if (res.ok) {
                alert("CONMOTO 서비스를 탈퇴했습니다.\n15일 간 대기 후 계정이 삭제됩니다.");
            } else {
                alert("서비스 탈퇴에 실패했습니다. 잠시 후 다시 시도해주세요.");
            }
        })
}

function requestFavoriteCategory(element) {
    const $element = $(element);
    const $category = $($element.parents('.sidebar-elements'))
        .find('.js-category-name');

    const categoryId = $category.attr('id');

    const url = "/api/category/favorite";
    const request = {
        groupUrl: groupUrl,
        categoryId: categoryId,
        set: $element.text() === "star_border"
    }



    put(url, request)
        .then(res => {
            if (res.ok) {
                const categoryName = $category.text();

                if (request.set) {
                    $element.text("star");
                    appendFavorite(categoryName);
                } else {
                    $element.text("star_border");
                    removeFavorite();
                }
            }
        })
}

function appendFavorite(categoryName) {
    const $new = $(`
                    <li>
                        <div class="sidebar-elements pointer-hover">
                            <span onclick="moveToCategoryPage(this)">
                                ${categoryName}
                            </span>
                        </div>
                    </li>
                `);

    $("#favorite-category-list").append($new);
}

function removeFavorite(categoryName) {
    $(`#favorite-category-list span:contains("${categoryName}")`)
        .parents("li").remove();
}

function login() {
    //세션에 현재 url 저장하고 로그인 후 해당 url로 이동
    sessionStorage.setItem('prevUrl', location.href);
    location.href = "/login";
}