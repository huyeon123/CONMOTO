function pageRender() {
    addSelectedClass();
    render();
    fetchGrade();
}

function addSelectedClass() {
    const tab = currentTab();
    $('.member-menu > span:contains(${tab})').addClass("selected");
}
//관리자 - 등급 관리 용
let levelList;

function fetchGrade() {
    const url = "/api/group/grade?url=" + groupUrl;
    get(url)
        .then(res => res.json())
        .then(data => levelList = data.levelList);
}

//스크롤 이벤트 감지
$(".scrollable").scroll((e) => {
    const scrollable = e.target;
    const scrollTop = scrollable.scrollTop;
    const scrollHeight = scrollable.scrollHeight;
    const clientHeight = scrollable.clientHeight;

    // 스크롤이 끝에 도달했는지 확인하고 무한 스크롤 이벤트 처리
    if (scrollTop + clientHeight >= scrollHeight) {
        render();
    }
})

//메뉴 탭 클릭 시 URL 변경
$(".member-menu span").click((e) => {
    const $tab = $(e.target);
    const tabName = $tab.text();
    changeTab(tabName);

    //selected 클래스 변경
    $(".member-menu").children().removeClass("selected");
    $tab.addClass("selected");

    //기존 게시글 전체 제거
    $("#draw-board-container").children().remove();

    //변경된 카테고리 게시글 렌더링
    isEnd = false;
    lastIndex = Number.MAX_SAFE_INTEGER;
    render();
})

//Change URL Request Parameter
function changeTab(categoryName) {
    history.pushState(null, null, location.pathname + "?tab=" + categoryName);
}

let lastIndex = Number.MAX_SAFE_INTEGER;
let isEnd;
const memberId = pathname.slice(pathname.lastIndexOf("/") + 1);

function render(first) {
    if (!first && isEnd) return; //더 이상 가져올 데이터가 없음

    //현재 메뉴 탭 판단
    const tab = currentTab();
    const url = createBoardReqAPI(tab);

    renderInit(tab, url, first);
}

function createBoardReqAPI(tab) {
    const url = new URL(location.origin + "/api/board/latest/member");
    const params = {
        memberId: memberId,
        type: tab,
        lastIndex: lastIndex
    };

    for (let key in params) {
        url.searchParams.set(key, params[key]);
    }

    if (tab === "comment") {
        url.pathname = "/api/comment/latest/member/comment";
    } else if (tab === "commented") {
        url.pathname = "/api/comment/latest/member/commented";
    }

    return url;
}

function currentTab() {
    let tab = new URLSearchParams(location.search).get("tab");
    if(tab == null) tab = "📝작성한 글";

    const $tab = $(`.member-menu span:contains(${tab})`);
    $tab.addClass("selected");

    let type = $tab.attr("id");
    if (type === undefined) type = "write";
    return type;
}

function renderInit(tab, url, first) {
    get(url)
        .then(res => res.json())
        .then(posts => {
            renderStart(tab, first, posts);
        });
}

function renderStart(tab, first, posts) {
    const nothingBody = {
        "write": "작성한 글이 존재하지 않습니다.",
        "commented": "댓글단 게시글이 존재하지 않습니다.",
        "like": "좋아요한 게시글이 존재하지 않습니다.",
        "comment": "댓글단 게시글이 존재하지 않습니다."
    }

    //최초 로딩이면 무조건 렌더링 하되 게시글이 없으면 nothingBody 렌더링
    if (first) {
        renderFirst(posts, nothingBody[tab]);
        return;
    }

    //게시글이 없으면 isEnd Flag On
    if (posts.length === 0) {
        isEnd = true;
        return;
    }

    if (tab === "comment") renderCommentPosts(posts);
    else renderPost(posts);
}

function renderFirst(data, nothing_body) {
    if (Object.keys(data).length === 0) {
        renderHasNothing(nothing_body);
        return;
    }

    data.forEach((item, index) => {
        drawPosts(item);
        setLastIndex(index, data);
    })
}

function renderPost(data) {
    data.forEach((item, index) => {
        drawPosts(item);
        setLastIndex(index, data);
    })
}

function setLastIndex(index, data) {
    if (index === data.length - 1) {
        lastIndex = data[index].id;
    }
}

function drawPosts(data) {
    const $element = $(`
        <li id="${data.id}" class="article pointer-hover" onclick="moveToBoardPage(this.id)">
                    <div class="article-body-wrapper">
                        <div class="article-preview-wrapper">
                            <div class="article-title">${data.title}</div>
                            <div class="article-description">${data.description}</div>
                        </div>
                        <div class="article-thumbnail">
                            <img src="/img/user.svg" alt="thumbnail" class="article-thumbnail-img">
                        </div>
                    </div>
                    <div class="article-detail-wrapper">
                        <span>${data.author}</span>
                        <span>${data.lastUpdate}</span>
                    </div>
                </li>
    `);

    $(".scrollable").append($element);
}

function renderCommentPosts(data) {
    data.forEach((item, index) => {
        drawCommentPosts(item);
        setLastIndex(index, data);
    })
}

function drawCommentPosts(data) {
    const $element = $(`
        <li id="${data.url}" class="article" onclick="moveToBoardPage(this.id)">
                    <div class="article-body-wrapper">
                        <div class="article-preview-wrapper">
                            <div class="article-title">${data.body}</div>
                            <div class="article-description">${data.boardTitle}</div>
                        </div>
                    </div>
                    <div class="article-detail-wrapper">
                        <span>${data.lastUpdate}</span>
                    </div>
                </li>
    `);

    $(".scrollable").append($element);
}

function renderHasNothing(body) {
    const $element = $(`<div class="nothing">${body}</div>`);
    $(".scrollable").append($element);
}

//등급변경 모달창
function changeGrade() {
    $dimmed.show();
    renderGradeModal();
    renderGradeOption();
    applyEventListener();
    $("#modal").show();
}

function renderGradeModal() {
    const $element = $(`
        <div class="custom-modal-wrapper" style="overflow: initial !important;">
            <div class="custom-modal-inner">
                <div class="custom-modal-title">변경할 등급을 선택하세요</div>
                <div class="custom-modal-main" style="align-items: center">
                    <div class="group-custom-select" onclick="openOptions()">
                        <span>등급 단계</span>
                        <i class="material-icons-outlined md-16">unfold_more</i>
                    </div>
                    <ul id="grade-level" class="custom-option-wrapper">
                        <div class="custom-option-inner"></div>
                    </ul>
                </div>
                <div class="flex-end">
                    <div>
                        <button id="grade-modal-cancel" class="manage-menu-button warn">취소</button>
                        <button id="grade-modal-save" class="manage-menu-button default">적용</button>
                    </div>
                </div>
            </div>
        </div>
    `);

    $("#modal").append($element);
}

function renderGradeOption() {
    for (let item of levelList) {
        const element = $(`<li class="custom-option" data-level="${item.level}">${item.gradeName}</li>`);
        $("#grade-level div").append(element);
    }
}

function openOptions() {
    $(".custom-option-wrapper").show();
}

function closeModal() {
    $dimmed.hide();
    $("#modal").children().remove();
}

//아무데나 클릭 시 modal 닫음
const $dimmed = $("#modal-dimmed");
$dimmed.click(closeModal);

//단계 선택 시 option 닫음
$(document).on("click", ".custom-option", (e) => {
    const $selected = $(e.target);
    const levelName = $selected.text();
    const level = $selected.attr("data-level");

    $(".custom-option-wrapper").hide();
    $(".group-custom-select span").text(levelName);
    $("#grade-modal-save").data("level", level);
})

function applyEventListener() {
    //취소
    $("#grade-modal-cancel").click(closeModal);

    //적용
    $("#grade-modal-save").click(() => {
        const level = $("#grade-modal-save").data("level");
        const url = "/api/group/member/grade?memberId=" + memberId + "&level=" + level;

        put(url)
            .then(res => {
                if (res.ok) {
                    location.reload();
                }
            })
    })
}

function expelMember() {
    const url = "/api/group/" + groupUrl + "/member";
    delWithoutBody(url)
        .then(res => {
            if (res.ok) {
                alert("해당 멤버를 강제퇴장 시켰습니다.");
            }
        })
}


