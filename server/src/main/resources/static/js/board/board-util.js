const curr = new Date();
const utc = curr.getTime() + (curr.getTimezoneOffset() * 60 * 1000);
const KR_TIME_DIFF = 9 * 60 * 60 * 1000;
const KR_now = new Date(utc + (2 * KR_TIME_DIFF)); //서버에서 다시 KR_TIME_DIFF만큼 빠짐
let nextPage = 0;
let isFetching = false;

const getList = (where, request) => {
    isFetching = true;
    const url = "/api/board/latest/" + where;
    return post(url, request);
};

function createBoard() {
    const url = "/api/board/" + groupUrl + "/create";
    get(url)
        .then(res => {
            if (canGetData(res)) {
                getData(res)
                    .then(id => {
                        alert("게시글을 생성했습니다.");
                        window.location.href = "/workspace/" + groupUrl + "/board/" + id;
                    })
            }
        });
}

function content_scroll_plugin(getListCallback) {
    $(".scroll-section").mCustomScrollbar({
        theme: "minimal-dark",
        mouseWheelPixels: 150,
        scrollInertia: 300, // 부드러운 스크롤 효과 적용
        callbacks: {
            onTotalScroll: function () {
                console.log("BOTTOM!")
                getListCallback();
            },
            onTotalScrollOffset: 100,
            alwaysTriggerOffsets: false,
        }
    });
}

function drawBoards(data, type) {
    data.forEach((item, index) => {
        const boardElement = drawBoard(item, type);

        $(".content-body").append(boardElement);

        setNextPageIfEnd(index, data);
    });
    isFetching = false;
}

function drawBoard(item, type) {
    let boardBlock = "";
    boardBlock = drawBoardHeader(item, boardBlock);
    boardBlock = drawBoardBody(item, type, boardBlock);
    boardBlock = drawBoardFooter(item, boardBlock);
    return boardBlock;
}

function drawBoardHeader(item, boardBlock) {
    boardBlock += boardBlockStart(item.id, item.url);
    boardBlock += addAuthorAndUpdateTime(item.author, item.updatedAt);
    boardBlock += addTitle(item.title);
    boardBlock += addDescription(item.description);
    boardBlock += addHorizonLine();
    return boardBlock;
}

function drawBoardBody(item, type, boardBlock) {
    if (type === "USER") {
        boardBlock += addGroupName(item.groupName);
    }

    if (type !== "CATEGORY") {
        boardBlock += addCategoryName(item.categoryName);
    }

    boardBlock += addContents(item.contents);
    return boardBlock;
}

function drawBoardFooter(item, boardBlock) {
    boardBlock += addStatus(item.status);
    boardBlock += addTags(item.tags);
    boardBlock += boardBlockEnd();
    return boardBlock;
}

function boardBlockStart(id, url) {
    return `<div class="board" id="${id}" onclick="moveToBoard('${url}')">`;
}

function addAuthorAndUpdateTime(author, updatedAt) {
    return `<div class="board__header">
                <span>작성자 : ${author}</span>
                <span class="board__updatedAt">${updatedAt}</span>
            </div>`;
}

function addTitle(title) {
    return `<h2>${title}</h2>`;
}

function addHorizonLine() {
    return `<hr>`;
}

function addDescription(description) {
    if (description !== null) {
        return `<p>${description}</p>`;
    }
    return "";
}

function addGroupName(groupName) {
    if (groupName !== null) {
        return `<p>${groupName}</p>`;
    }
    return "";
}

function addCategoryName(categoryName) {
    if (categoryName !== null) {
        return `<p>${categoryName}</p>`;
    }
    return "";
}

function addContents(contents) {
    if (contents !== null) {
        let contentsHTML = "";
        contents.forEach((content) => {
            if (content.content != null) {
                contentsHTML += `<pre>${content.content}</pre>`;
            }
        })
        return contentsHTML;
    }
    return "";
}

function addStatus(status) {
    const statusElement = document.createElement("p");
    statusElement.setAttribute("class", "board__status");
    statusElement.innerText = status;
    addStatusClass(statusElement, status);
    return statusElement.outerHTML;
}

function addStatusClass(statusElement, status) {
    statusElement.className += (" status__" + status.toLowerCase());
}

function addTags(tags) {
    if (tags !== null) {
        let div = `<div>`;
        tags.forEach((tag) => {
            div += `<span class="board__tags">%#35;${tag.tag}</span>`;
        });
        div += `</div>`;
        return div;
    }
    return "";
}

function boardBlockEnd() {
    return `</div>`;
}

function setNextPageIfEnd(index, data) {
    if (index === data.length - 1) {
        nextPage++;
    }
}

function moveToBoard(url) {
    location.href = url;
}

function moveToMyPage() {
    location.href = "./info";
}

function moveToMyBoard() {
    location.href = "./board";
}

function moveToMyComment() {
    location.href = "./comment";
}

function moveToResign() {
    location.href = "./resign";
}