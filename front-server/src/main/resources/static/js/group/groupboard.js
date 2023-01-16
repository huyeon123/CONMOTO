function pageRender() {
    drawBoardsOfGroup();
    content_scroll_plugin(drawBoardsOfGroup);
}

function drawBoardsOfGroup() {
    const url = "/api/board/latest/group?url=" + groupUrl + "&page=" + nextPage;
    getList(url)
        .then(res => res.json())
        .then(data => drawBoards(data))
        .catch(error => console.error(error));
}