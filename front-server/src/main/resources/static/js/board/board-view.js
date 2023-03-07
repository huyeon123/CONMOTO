let boardId = pathname.slice(pathname.lastIndexOf("/") + 1);

function pageRender() {
    appendBoardLike();
    increaseViews();
}

function appendBoardLike() {
    const url = "/api/board/likes?boardId=" + boardId;
    get(url)
        .then(res => res.json())
        .then(data => {
            $("#like-post-num").text(data.like);

            const $like = $("#like-post-heart");
            if (data.checked) $like.addClass("like-color");
            else $like.addClass("material-symbols-outlined");

            $("#viewer").show();
            $("#loading").remove();
        })
}

/*comment*/
$(document).on('click', '#comment-toggle-btn', () => {
    const area = $('#comment-hide-area').get(0);

    if (area.hidden) {
        $('#comment-toggle-btn').text("댓글 접기");
        area.hidden = false;
    } else {
        $('#comment-toggle-btn').text("댓글 펼치기");
        area.hidden = true;
    }
})

$(document).on('click', '.js-comment-add-btn', () => {
    const request = {
        author: $('.comment-inbox-name').attr('id'),
        body: $('.comment-inbox-text').val()
    };

    registerComment(request);
})

function registerComment(request) {
    const url = "/api/comment";
    request.boardId = boardId;

    post(url, request)
        .then(res => res.text())
        .then((commentId) => {
            $('.comment-inbox-text').val('');
            const newComment = createNewComment(request, commentId);
            $('#comment-hide-area').append(newComment);

            const commentNum = getCommentNum();
            setCommentNum(commentNum + 1);
        })
        .catch(error => {
            alert("댓글을 저장하지 못했습니다!\n잠시후 다시 시도해주세요.");
            console.error(error);
        });
}

function createNewComment(request, commentId) {
    const currentTime = new Date().toLocaleString();
    const nickname = $('.comment-inbox-name').text();
    return `<li id="comment_${commentId}" class="comment-item">` +
        `<div class="comment-area">` +
        `<div class="comment-nick-box">${nickname}</div>` +
        `<span class="comment-text-box">${request.body}</span>` +
        `<div class="comment-info-box">` +
            `<span class="comment-info-date">${currentTime}</span>` +
        `</div>
        <div class="comment-tool">
            <button type="button" class="js-comment-edit simple-button">수정</button>
            <button type="button" class="js-comment-del simple-button">삭제</button>
        </div>
        </div></li>`;
}

$(document).on('input', '.comment-inbox-text', (e) => {
    const $comment = $(e.target);
    autoScalingHeight($comment);
})

function autoScalingHeight(textarea) {
    textarea.height(0);
    const autoHeight = textarea.prop('scrollHeight');
    textarea.css('height', autoHeight);
}

$(document).on('click', '.js-comment-edit', (e) => {
    const $commentBtn = $(e.target);
    const $commentArea = $($commentBtn.parents('div.comment-area'));
    const $commentItem = $($commentBtn.parents('li.comment-item'));

    $commentArea.hide(); //기존 commentArea 숨김

    //commentWriter 적용
    const nickname = $commentArea.find('.comment-nick-box').text();
    const body = $commentArea.find('.comment-text-box').html().replaceAll('<br>', '\n');

    const writer = $(`
        <div class="comment-writer">
            <div class="comment-inbox">
                <div class="comment-inbox-name">${nickname}</div>
                <textarea class="comment-inbox-text" placeholder="댓글을 남겨보세요.">${body}</textarea>
            </div>
            <div class="comment-attach">
                <div class="comment-register">
                    <button type="button" class="js-comment-edit-cancel simple-button">취소</button>
                    <button type="button" class="js-comment-edit-save simple-button">완료</button>
                </div>
            </div>
        </div>
    `);

    $commentItem.append(writer);

    const textarea = writer.find('.comment-inbox-text');
    autoScalingHeight(textarea);
})

$(document).on('click', '.js-comment-edit-cancel', (e) => {
    $($(e.target).parents('.comment-item')).find('.comment-area').show();
    $(e.target).parents('.comment-writer').remove();
})

$(document).on('click', '.js-comment-edit-save', (e) => {
    const $commentItem = $($(e.target).parents('.comment-item'));

    const url = "/api/comment";
    const request = {
        id: $commentItem.attr('id').slice("comment_".length),
        author: $commentItem.find('.comment-inbox-name').text(),
        body: $commentItem.find('.comment-inbox-text').val()
    }

    put(url, request)
        .then(() => {
            const newComment = createNewComment(request);
            const $commentArea = $commentItem.find('.comment-area');
            $commentArea.replaceWith(newComment);
            $commentArea.show();
            $commentItem.find('.comment-writer').remove();
        })
        .catch(error => {
            alert("댓글 수정에 실패했습니다!");
            console.error(error);
        });
})

$(document).on('click', '.js-comment-del', (e) => {
    if (confirm("정말로 삭제하시겠습니까?")) {
        const $commentBtn = $(e.target);
        const $commentItem = $commentBtn.parents('li.comment-item');
        const commentId = $commentItem.attr('id').slice("comment_".length);
        $commentItem.remove();

        const commentNum = getCommentNum();
        setCommentNum(commentNum - 1);

        const url = "/api/comment/" + commentId;
        delWithoutBody(url)
            .catch(error => {
                alert("댓글 삭제에 실패했습니다!");
                console.error(error);
            });
    }
})

function getCommentNum() {
    const commentNumBox = $('#js-comment-num');
    return parseInt(commentNumBox.text());
}

function setCommentNum(num) {
    const commentNumBox = $('#js-comment-num');
    commentNumBox.text(num);
}

function moveToPreviousPage() {
    history.go(-1);
}