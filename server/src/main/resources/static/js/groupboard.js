$(function () {
    drawBoardsOfGroup();
    content_scroll_plugin(drawBoardsOfGroup);
});

function drawBoardsOfGroup() {
    const request = {
        type: "GROUP",
        query: groupUrl,
        now: KR_now,
        nextPage: nextPage
    };

    getList(request)
        .then(data => drawBoards(data))
        .catch(error => console.error(error));
}

function moveToBoard(url) {
    location.href = pathname + url;
}