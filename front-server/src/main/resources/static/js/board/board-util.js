let nextPage = 0;
let isFetching = false;

const getList = (url) => {
    isFetching = true;
    return get(url);
};

        }
}

    data.forEach((item, index) => {
        drawBoard(item);

    });
}

function drawBoard(item) {
    if (index === data.length - 1) {
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