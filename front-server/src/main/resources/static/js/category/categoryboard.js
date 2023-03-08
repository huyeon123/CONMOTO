let lastIndex = Number.MAX_SAFE_INTEGER;

function pageRender() {
    const url = createAPIURL();
    renderPost(url, "작성된 게시글이 없습니다.");
}

function createAPIURL() {
    const url = new URL(location.origin + "/api/board/latest");
    const params = {
        "lastIndex": lastIndex,
        "categoryId": $("#draw-board-container").data("category-id")
    };

    for (let key in params) {
        url.searchParams.set(key, params[key]);
    }

    return url;
}

function renderPost(url, nothing_body) {
    getList(url)
        .then(res => res.json())
        .then(data => {
            if (Object.keys(data).length === 0) {
                renderHasNothing(nothing_body);
            }
            data.forEach((item, index) => {
                drawBoard(item);
                setLastIndex(index, data);
            })
        });
}

function renderHasNothing(body) {
    const $element = $(`<div class="nothing">${body}</div>`);
    $("#draw-board-container").append($element);
}