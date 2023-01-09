function pageRender() {
    drawMyComments();
    content_scroll_plugin(drawMyComments);
}

function drawMyComments() {
    const request = {
        type: "COMMENT",
        now: KR_now,
        nextPage: nextPage
    }

    getComments(request)
        .then(res => res.json())
        .then(data => drawComments(data))
        .catch(error => console.error(error));
}

const getComments = (request) => {
    isFetching = true;
    const url = "/api/comment/latest";
    return post(url, request);
};

function drawComments(data) {
    data.forEach((item, index) => {
        drawComment(item);

        setNextPageIfEnd(index, data);
    });
    isFetching = false;
}

function drawComment(item) {
    const elementId = drawVDOM(item);
    drawPreview(elementId, item);
    draw
}
