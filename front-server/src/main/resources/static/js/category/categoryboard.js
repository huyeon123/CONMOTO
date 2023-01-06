const categoryNameStartIdx = pathname.lastIndexOf("/") + 1;
const categoryName = pathname.substring(categoryNameStartIdx);

function pageRender() {
    drawBoardsOfCategory();
    content_scroll_plugin(drawBoardsOfCategory);
}

function drawBoardsOfCategory() {
    const request = {
        query: categoryName,
        now: KR_now,
        nextPage: nextPage
    }

    getList("category", request)
        .then(res => getJson(res))
        .then(data => drawBoards(data, request.type))
}

function moveToBoard(url) {
    location.href = url;
}