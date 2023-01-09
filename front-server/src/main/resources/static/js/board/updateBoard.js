const boardId = pathname.substring(pathname.lastIndexOf("/") + 1);

$(function () {
    autoScalingWidth("input.tag", "#tags", 15);
})

/*header*/
$(document).on('keyup', '#title', (e) => {
    if (e.keyCode === 13) {
        const request = {
            title: $("#title").val(),
            target: "title"
        };
        e.target.blur();
        saveBoard(request);
    }
})

$(document).on('keyup', '#description', (e) => {
    if (e.keyCode === 13) {
        const request = {
            description: $("#description").val(),
            target: "description"
        };
        e.target.blur();
        saveBoard(request);
    }
})

$(document).on('change', '#status', () => {
    const request = {
        status: $("#status option:selected").val(),
        target: "status"
    };
    saveBoard(request);
})

$(document).on('change', '#categoryOption', () => {
    const categoryId = $("#categoryOption option:selected").val();

    if (categoryId === '' || categoryId === undefined) {
        alert("카테고리를 선택하세요");
        return;
    }

    const request = {
        categoryId: categoryId,
        target: "category"
    };
    saveBoard(request);
})

function saveBoard(request) {
    const url = "/api/board/" + boardId;
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
        .then(res => {
            if (res.ok) {
                alert("삭제되었습니다.");
                location.href = "/workspace/" + groupUrl;
            } else alert("삭제에 실패했습니다.");
        });
}

$(document).on('keydown', '.tag', (e) => {
    const value = e.target.value;
    $("#tags").append("<div id='virtual_dom'>" + value + "</div>");

    const calWidth = $('#virtual_dom').width() + 15;
    $('#virtual_dom').remove();

    $("#" + e.target.id).css("width", calWidth);
})

$(document).on('click', '#tags .delete', (e) => {
    const tagLocation = e.target.parentElement;
    const id = tagLocation.firstElementChild.id.split("tag_")[1];
    tagLocation.remove();

    const url = "/api/tag?boardId=" + boardId;
    const request = {id: id};
    del(url, request)
        .catch(error => {
            alert("태그 삭제에 실패했습니다.")
            console.error(error);
        })
})

$(document).on('keyup', '.tag', (e) => {
    if (e.keyCode === 13) {
        const url = "/api/tag?boardId=" + boardId;
        const request = [];

        $('.tag').each((idx, tag) => {
            request.push({tag: tag.value});
        })

        put(url, request)
            .then((res) => {
                if (res.ok) {
                    $('#tags').append(`<input class="empty tag" placeholder="#태그 추가">`);
                }
            })
            .catch((error) => {
                alert("태그 저장에 실패했습니다!");
                console.error(error);
            });
    }
});

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
        author: $('.comment-inbox-name').text(),
        body: $('.comment-inbox-text').val()
    };

    registerComment(request);
})

function registerComment(request) {
    const url = "/api/comment?boardId=" + boardId;

    post(url, request)
        .then(res => {
            if (res.ok) renderNewComment(request);
        })
        .catch(error => {
            alert("댓글을 저장하지 못했습니다!\n잠시후 다시 시도해주세요.");
            console.error(error);
        });
}

function renderNewComment(request) {
    $('.comment-inbox-text').val('');

    const currentTime = new Date().toLocaleString();
    const newComment =
        `<li id="comment_id" class="comment-item">` +
        `<div class="comment-area">` +
        `<div class="comment-nick-box">${request.author}</div>` +
        `<div class="comment-text-box">${request.body}</div>` +
        `<div class="comment-info-box">` +
        `<span class="comment-info-date">${currentTime}</span>` +
        `</div></div></li>`;

    $('#comment-hide-area').append(newComment);
}

$(document).on('keyup keydown', '.comment-inbox-text', (e) => {
    const $comment = $(e.target);
    $comment.height(1);
    $comment.height($comment.prop('scrollHeight') + 12);
})

function editComment(request) {
    const url = "/api/comment?boardId=" + boardId;

    put(url, request)
        .then(res => {
            if (!res.ok) {
                alert("댓글 저장에 실패했습니다.");
            } else location.reload();
        });
}

$(document).on('click', '.delCommentBtn', (e) => {
    if (confirm("정말로 삭제하시겠습니까?")) {
        const commentId = e.target.parentElement.id;
        e.target.parentElement.remove();
        deleteComment(commentId);
    }
})

function deleteComment(commentId) {
    commentId = commentId.split("comment_")[1];
    const url = "/api/comment?commentId=" + commentId;
    delWithoutBody(url)
        .catch((error) => console.error(error));
}

/*contents*/
function moveToPreviousPage() {
    history.go(-1);
}

function moveToEditPage() {
    const editorIdx = pathname.lastIndexOf("/");
    location.href = pathname.slice(0, editorIdx) + "/editor" + pathname.slice(editorIdx);
}

function registerContent() {
    save("/api/content");
}

function saveTemporarily() {
    save("/api/content/temp");
}

function save(url) {
    const request = {
        contentId: $('.js-toast-editor').attr("id"),
        markdown: editor.getMarkdown()
    }

    post(url, request)
        .then(res => res.json())
        .catch(error => console.error(error));
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

        $("#" + element.id).css("width", calWidth);
    })
}

function autoScalingHeightDefault(containerSelector, elementSelector,) {
    autoScalingHeight(containerSelector, elementSelector, 10);
}

function autoScalingHeight(containerSelector, elementSelector, extraSize) {
    $(containerSelector).not('.empty').each((idx, element) => {
        const contentId = element.id;
        const $content = $('#' + contentId + elementSelector);

        $content.height(5);

        $content.height($content.prop('scrollHeight') + extraSize);
    })
}