let nextPage = 0;
let isFetching = false;
const curr = new Date();
const utc = curr.getTime() + (curr.getTimezoneOffset() * 60 * 1000);
const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
const KR_now = new Date(utc + (2 * KR_TIME_DIFF)); //서버에서 다시 KR_TIME_DIFF만큼 빠짐

$(function () {
    const request = {
        type: "GROUP",
        query: groupUrl,
        now: KR_now,
        nextPage: nextPage
    }
    getList(request);
});

const getList = (request) => {
    isFetching = true;
    const url = "/api/board/latest";
    const res = post(url, request);
    res.then(data => drawList(data));
};

const drawList = (data) => {
    let listHtml = "";
    data.forEach((item, index) => {
        const {id, author, title, description, categoryName, status, tags, url, updatedAt} = item;

        listHtml = `<div class="board" id="${id}" onclick="location.href=${url}">
                        <div class="board__header">
                            <span>작성자 : ${author}</span>
                            <span class="board__updatedAt">${updatedAt}</span>
                        </div>
                        <h2>${title}</h2>
                        <hr>`;

        if (description !== null) {
            listHtml += `<p>${description}</p>`
        }

        if (categoryName !== null) {
            listHtml += `<p>${categoryName}</p>`
        }

        const statusElement = document.createElement("p");
        statusElement.setAttribute("class", "board__status");
        statusElement.innerText = status;
        addStatusClass(statusElement, status);

        listHtml += statusElement.outerHTML + `<div>`;

        if (tags !== null) {
            tags.forEach((tag) => {
                listHtml += `<span class="board__tags">%#35;${tag.tag}</span>`;
            });
        }

        listHtml += `</div>`;

        $(".content-body").append(listHtml);

        if (index === data.length - 1) {
            nextPage++;
        }
    });
    isFetching = false;
};

function addStatusClass(statusElement, status) {
    statusElement.className += (" status__" + status.toLowerCase());
}