$(function () {
    drawMyComments();
    content_scroll_plugin(drawMyComments);
});

function drawMyComments() {
    const request = {
        type: "COMMENT",
        now: KR_now,
        nextPage: nextPage
    }

    getComments(request)
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
        const commentElement = drawComment(item);
        $(".content-body").append(commentElement);
        setNextPageIfEnd(index, data);
    })
}

function drawComment(item) {
    let commentBlock = "";
    commentBlock += boardBlockStart('#', item.url);
    commentBlock += addTitle(item.title);
    commentBlock += addDescription(item.location);
    commentBlock += addUpdateTime(item.time);
    commentBlock += addHorizonLine();
    commentBlock += addComment(item.comment);
    commentBlock += boardBlockEnd();
    return commentBlock;
}

function addUpdateTime(updatedAt) {
    return `<p class="board__updatedAt">${updatedAt}</p>`
}

function addComment(comment) {
    return `<p>${comment}</p>`
}


