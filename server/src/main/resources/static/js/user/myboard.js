$(function () {
    drawMyBoards();
    content_scroll_plugin(drawMyBoards);
});

function drawMyBoards() {
    const request = {
        now: KR_now,
        nextPage: nextPage
    }

    getList("user", request)
        .then(data => drawBoards(data, request.type))
        .catch(error => console.error(error));
}