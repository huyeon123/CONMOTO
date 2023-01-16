function pageRender() {
    drawMyComments();
    content_scroll_plugin(drawMyComments);
}

function drawMyComments() {
    const url = "/api/comment/latest?page=" + nextPage;

    getComments(url)
        .then(res => res.json())
        .then(data => drawComments(data))
        .catch(error => console.error(error));
}

const getComments = (url) => {
    isFetching = true;
    return get(url);
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
    drawBoardInfo(elementId, item);
}

function drawVDOM(item) {
    const virtualDom = `<div id="board_${item.boardId}" class="board-area">
        <a href="${item.url}" class="board-anchor">
            <div class="comment-card">
                <div class="comment-preview"></div>
                <div class="comment-board-title"></div>
                <div class="board-signature">
                    <div class="board-updatedAt"></div>
                </div>
            </div>
        </a>
    </div>`;

    $('.content-body').append(virtualDom);
    return '#board_' + item.boardId;
}

function drawPreview(elementId, item) {
    $(elementId + ' .comment-preview').text(item.body);
}

function drawBoardInfo(elementId, item) {
    addTitle(elementId, item.boardTitle);
    addUpdateTime(elementId, item.lastUpdate);
}

function addTitle(elementId, title) {
    $(elementId + ' .comment-board-title').text(title);
}

function addUpdateTime(elementId, lastUpdate) {
    $(elementId + ' .board-updatedAt').text(lastUpdate);
}

function setNextPageIfEnd(index, data) {
    if (index === data.length - 1) {
        nextPage++;
    }
}
