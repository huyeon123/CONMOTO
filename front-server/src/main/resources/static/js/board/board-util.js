let nextPage = 0;
let isFetching = false;

const getList = (url) => {
    isFetching = true;
    return get(url);
};

function drawBoards(data) {
    if (Array.isArray(data)) {
        drawEachItem(data)
    } else {
        for (let value of Object.values(data)) {
            drawEachItem(value);
        }
    }

    isFetching = false;
}

function drawEachItem(data) {
    data.forEach((item, index) => {
        drawBoard(item);

        setLastIndex(index, data);
    });
}

function drawBoard(item) {
    const $element = $(`
        <li id="${item.id}" class="article pointer-hover" onclick="moveToBoardPage(this.id)">
                    <div class="article-body-wrapper">
                        <div class="article-preview-wrapper">
                            <div class="article-title">${item.title}</div>
                            <div class="article-description">${item.description}</div>
                        </div>
                        <div class="article-thumbnail">
                            <img src="/img/user.svg" alt="thumbnail" class="article-thumbnail-img">
                        </div>
                    </div>
                    <div class="article-detail-wrapper">
                        <span>${item.author}</span>
                        <span>${item.lastUpdate}</span>
                    </div>
                </li>
    `);

    $("#draw-board-container").append($element);
}

function setLastIndex(index, data) {
    if (index === data.length - 1) {
        lastIndex = data[index].id;
    }
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