const boardId = pathname.substring(pathname.lastIndexOf("/") + 1);

$(function () {
    autoScalingWidth("input.tag", "#tags", 15);
})

/*header*/
$(document).on('blur', '#js-title', (e) => {
    const url = "/api/board/edit/title";
    const request = {
        title: $(e.target).val(),
    };
    saveBoard(url, request);
})

$(document).on('blur', '#js-description', (e) => {
    const url = "/api/board/edit/description";
    const request = {
        description: $(e.target).val(),
    };
    saveBoard(url, request);
})

$(document).on('change', '#js-status', () => {
    const url = "/api/board/edit/status";
    const request = {
        status: $("#js-status option:selected").val(),
    };
    saveBoard(url, request);
})

$(document).on('change', '#js-categoryOption', () => {
    const url = "/api/board/edit/category";
    const categoryName = $("#js-categoryOption option:selected").val();

    if (categoryName === "카테고리 미선택") return;

    const request = {
        categoryName: categoryName,
    };
    saveBoard(url, request);
})

function saveBoard(url, request) {
    request.id = boardId;

    put(url, request)
        .catch((error) => {
            alert("저장에 실패했습니다!");
            console.error(error);
        })
}

function deleteBoard() {
    if (!confirm("정말로 삭제하시겠습니까?")) {
        return;
    }

    delWithoutBody("/api/board/" + boardId)
        .then(() => {
            location.href = "/workspace/" + groupUrl;
        })
        .catch(error => {
            alert("삭제에 실패했습니다!");
            console.error(error);
        });
}

$(document).on('keydown', '.tag', (e) => {
    const $target = $(e.target);
    const value = $target.val();
    $("#tags").append("<div id='virtual_dom'>" + value + "</div>");

    const calWidth = $('#virtual_dom').width() + 15;
    $('#virtual_dom').remove();

    $target.css("width", calWidth);
})

$(document).on('click', '#tags i.delete', (e) => {
    if (!confirm("태그를 삭제하시겠습니까?")) return;

    $(e.target).parent().remove();
    saveTags();
})

$(document).on('keyup', '.tag', (e) => {
    if (e.keyCode === 13) {
        saveTags();
    }
});

function saveTags() {
    const url = "/api/board/edit/tags";
    const request = {
        id: boardId,
        tags: []
    };

    $('.tag').each((idx, tag) => {
        if (tag.value !== '') request.tags.push(tag.value);
    })

    put(url, request)
        .then((res) => {
            if (res.ok) {
                location.reload();
            }
        })
        .catch((error) => {
            alert("태그 저장에 실패했습니다!");
            console.error(error);
        });
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

/*contents*/
function moveToPreviousPage() {
    history.go(-1);
}

function registerContent() {
    save("/api/board/edit/content");
}

function saveTemporarily() {
    save("/api/board/content/temp");
}

function save(url) {
    const request = {
        boardId: boardId,
        markdown: editor.getMarkdown()
    }

    put(url, request)
        .then((res) => {
            if (res.ok) location.href = "../" + boardId;
        })
        .catch(error => {
            alert("컨텐츠 저장에 실패했습니다!");
            console.error(error);
        });
}

function autoScalingWidthDefault(containerSelector, elementSelector) {
    autoScalingWidth(containerSelector, elementSelector, 10);
}

function autoScalingWidth(elementSelector, containerSelector, extraSize) {
    $(elementSelector).not('.empty').each((idx, element) => {
        const value = element.value;
        $(containerSelector).append("<div id='virtual_dom'>" + value + "</div>");

        const calWidth = $('#virtual_dom').width() + extraSize;
        $('#virtual_dom').remove();

        $(element).css("width", calWidth);
    })
}