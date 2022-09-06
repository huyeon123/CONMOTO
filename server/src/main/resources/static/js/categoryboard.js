const categoryNameStartIdx = pathname.lastIndexOf("/") + 1;
const categoryName = pathname.substring(categoryNameStartIdx);

$(function () {
    drawBoardsOfCategory();
    content_scroll_plugin(drawBoardsOfCategory);
});

function drawBoardsOfCategory() {
    const request = {
        type: "CATEGORY",
        query: categoryName,
        now: KR_now,
        nextPage: nextPage
    }

    getList(request)
        .then(data => drawBoards(data, request.type))
        .catch(error => console.error(error));
}

function moveToBoard(url) {
    location.href = url;
}