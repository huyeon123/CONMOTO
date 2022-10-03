$(function () {
    drawBoardsOfGroup();
    content_scroll_plugin(drawBoardsOfGroup);
});

function drawBoardsOfGroup() {
    const request = {
        query: groupUrl,
        now: KR_now,
        nextPage: nextPage
    };

    getList("group", request)
        .then(res => res.json())
        .then(data => drawBoards(data))
        .catch(error => console.error(error));
}

function moveToBoard(url) {
    location.href = url;
}