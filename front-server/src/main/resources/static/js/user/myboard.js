function pageRender() {
    drawMyBoards();
    content_scroll_plugin(drawMyBoards);
}

function drawMyBoards() {
    const url = "/api/board/latest/mine?page=" + nextPage;

    getList(url)
        .then(res => res.json())
        .then(data => drawBoards(data))
        .catch(error => console.error(error));
}