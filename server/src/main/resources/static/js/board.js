let lastId = 0;
let isFetching = false;

$(function () {
    isFetching = true;
    const url = "/api/board/group?predicate=" + groupUrl;
    fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: "cors",
        body: lastId
    })
        .then((res) => res.json())
        .then((data) => {
            drawList(data);
        })
});

$(document).on("scroll", ".scroll-section", () => {
    const SCROLLED_HEIGHT = window.scrollY;
    const WINDOW_HEIGHT = window.innerHeight;
    const DOC_TOTAL_HEIGHT = document.body.offsetHeight;
    const IS_END = (WINDOW_HEIGHT + SCROLLED_HEIGHT > DOC_TOTAL_HEIGHT - 500);

    if (IS_END && !isFetching) { // isFetching이 false일 때 조건 추가
        getList();
    }
});

const getList = (url) => {
    isFetching = true;
    fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: "cors",
        body: lastId

    })
        .then((res) => res.json())
        .then((resJson) => {
            drawList(resJson);
        });
};

const drawList = (data) => {
    let listHtml = "";
    data.forEach((item, index) => {
        const {id, title, description, categoryName, status, tags, contents, url} = item;

        listHtml = `<div class="board" id="${id}" onclick="location.href=${url}">
                    <h2>${title}</h2>`;

        if (description !== null) {
            listHtml += `<p>${description}</p>`
        }

        if (categoryName !== null) {
            listHtml += `<p>${categoryName}</p>`
        }

        listHtml += `<p>${status}</p>`;

        if (tags !== null) {
            tags.forEach((tag) => {
                listHtml += `<p>${tag.tag}</p>`;
            });
        }

        listHtml += `<hr><div><p>내용 요약</p>`;

        if (contents !== null) {
            contents.forEach((content) => {
                listHtml += `<p>${content.content}</p>`
            });
        }

        listHtml += `<p>내용</p></div></div>`;

        $(".content-body").append(listHtml);

        if (index === data.length - 1) {
            lastId = id;
        }
        $("#mCSB_1_scrollbar_vertical .mCSB_dragger").css("height", "100px");
    });
    isFetching = false;
};