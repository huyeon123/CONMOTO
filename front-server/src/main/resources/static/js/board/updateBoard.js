const boardId = pathname.substring(pathname.lastIndexOf("/") + 1);

$(function () {
    $("input.tag").each((idx, element) => {
        const value = element.value;
        $("#tags").append("<div id='virtual_dom'>" + value + "</div>");

        const calWidth = $('#virtual_dom').width() + 15;
        $('#virtual_dom').remove();

        $("#" + element.id).css("width", calWidth);
    })
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
                    $('#tags').append(`<input class="tag" placeholder="#태그 추가">`);
                }
            })
            .catch((error) => {
                alert("태그 저장에 실패했습니다!");
                console.error(error);
            });
    }
});

/*comment*/
$(document).on('click', '#addCommentBtn', (e) => {
    const input = `<textarea id="addCommentInput" placeholder="댓글을 작성해주세요. (엔터 시 저장됩니다.)"></textarea>`;
    $(".comments-header").append(input);
})

$(document).on('keyup', '#addCommentInput', (e) => {
    if (e.keyCode === 13) {
        const request = {comment: e.target.value}
        e.target.readOnly = true;
        setComment(request);
    }
})

$(document).on('click', '.editCommentBtn', (e) => {
    const commentBox = e.target.nextElementSibling.lastElementChild;
    commentBox.readOnly = false;
    commentBox.focus();
})

$(document).on('keyup', '.comment-body', (e) => {
    if (e.keyCode === 13) {
        const request = {
            comment: e.target.value,
            id: e.target.parentElement.parentElement.id
        }
        setComment(request);
    }
})

$(document).on('click', '.delCommentBtn', (e) => {
    if (confirm("정말로 삭제하시겠습니까?")) {
        const commentId = e.target.parentElement.id;
        e.target.parentElement.remove();
        deleteComment(commentId);
    }
})

function setComment(request) {
    const url = "/api/comment?boardId=" + boardId;

    post(url, request)
        .then(res => {
            if (!res.ok) {
                alert("댓글 저장에 실패했습니다.");
            }
        });

    location.reload();
}

function deleteComment(commentId) {
    const url = "/api/comment?commentId=" + commentId;
    delWithoutBody(url)
        .catch((error) => console.error(error));
}

/*contents*/
$(document).on('click', '.addContentsBtn', (e) => {
    makeContentId()
        .then(contentId => {
            const newContent = `<div class="contentContainer" id="${contentId}">
                            <textarea class="content" placeholder="내용을 입력하세요."></textarea>
                            <div class="content__buttons">
                                <button type="button" class="delContentsBtn">-</button>
                                <button type="button" class="addContentsBtn">+</button>
                            </div>
                        </div>`;
            const template = document.createElement("template");
            template.innerHTML = newContent;

            const targetLocation = e.target.parentElement.parentElement;
            targetLocation.after(template.content.firstChild);
        })
})

$(document).on('click', '.delContentsBtn', (e) => {
    const target = e.target.parentElement.parentElement;
    deleteContent("/api/contents/", target);
})

$(document).on('keyup', '.content', (e) => {
    if (e.keyCode === 13) {
        if (!e.shiftKey) {
            const contentId = e.target.parentElement.id;
            const url = "/api/contents/" + contentId;
            const request = {content: e.target.value};
            e.target.blur();
            saveContents(url, request);
        }
    }
})

$(document).on('keyup keydown', '.content', () => {
    const content = $('.content');
    content.height(1)
        .height(content.prop('scrollHeight') + 10);
})

async function makeContentId() {
    const url = "/api/contents/" + boardId;
    return get(url);
}

function saveContents(url, request) {
    put(url, request)
        .catch(error => console.error(error));
}

function deleteContent(url, target) {
    delWithoutBody(url + target.id)
        .then(res => {
            if (res.ok) {
                target.remove();
            }
        })
        .catch((error) => console.error(error));
}

function moveToPreviousPage() {
    history.go(-1);
}