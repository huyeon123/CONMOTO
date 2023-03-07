let lastIndex = Number.MAX_SAFE_INTEGER;

$(() => {
    //현재 메뉴탭 기준으로 selected 설정
    const tab = currentTab();
    $(`.group-main-menu #${tab}`).addClass("selected");
})

function pageRender() {
    const url = createAPIURL();
    drawBoardsOfGroup(url);
}

function createAPIURL() {
    const url = new URL(location.origin + "/api/board/latest");
    const params = {"lastIndex": lastIndex};

    const defaultCategory = ["popular", "all", "notice"];
    const tab = currentTab();

    //선택한 탭이 기본 카테고리라면 URL path 추가
    //API에 맞게 params 추가
    if (defaultCategory.includes(tab)) {
        url.pathname += "/" + tab;
        params.url = groupUrl;
    } else {
        params.categoryId = tab;
    }

    for (let key in params) {
        url.searchParams.set(key, params[key]);
    }

    return url;
}

function currentTab() {
    const tab = new URLSearchParams(location.search).get("tab");
    if (tab == null) return "popular";
    return $(`.group-main-menu span:contains(${tab})`).attr("id");
}

function drawBoardsOfGroup(url) {
    getList(url)
        .then(res => res.json())
        .then(data => drawBoards(data))
        .catch(error => console.error(error));
}

let fixed = false;
$(".group-main-wrapper").scroll((e) => {
    const scrollable = e.target;
    const scrollTop = scrollable.scrollTop;

    const $nav = $(".group-category-nav");

    //group-menu fixed
    if (scrollTop > 115 && !fixed) {
        const $nav_fix = $nav.clone();
        $nav_fix.addClass("fix");
        $(".app-header").after($nav_fix);
        fixed = true;
    } else if (scrollTop <= 115 && fixed) {
        $(".group-category-nav.fix").remove();
        fixed = false;
    }

    const scrollHeight = scrollable.scrollHeight;
    const clientHeight = scrollable.clientHeight;

    // 스크롤이 끝에 도달했는지 확인하고 무한 스크롤 이벤트 처리
    if (scrollTop + clientHeight >= scrollHeight) {
        //데이터 fetch
        drawBoardsOfGroup();
    }
})

function moveToBoardPage(boardId) {
    location.href = "/community/" + groupUrl + "/board/" + boardId;
}

//메뉴탭 카테고리 클릭 시 이벤트 발생
$(".group-main-menu span").click((e) => {
    const $category = $(e.target);
    const categoryName = $category.text();
    changeTab(categoryName);

    //selected 클래스 변경
    $(".group-main-menu").children().removeClass("selected");
    $category.addClass("selected");

    //기존 게시글 전체 제거
    $("#draw-board-container").children().remove();

    //변경된 카테고리 게시글 렌더링
    lastIndex = Number.MAX_SAFE_INTEGER;
    pageRender();
})

//Change URL Request Parameter
function changeTab(categoryName) {
    history.pushState(null, null, location.pathname + "?tab=" + categoryName);
}

function moveToEditorPage() {
    location.href = location.pathname + "/board/editor";
}