$(function () {
    drawMyBoards();
    content_scroll_plugin(drawMyBoards);
});

function drawMyBoards() {
    const request = {
        type: "USER",
        now: KR_now,
        nextPage: nextPage
    }

    getList(request)
        .then(data => drawBoards(data, request.type))
        .catch(error => console.error(error));
}