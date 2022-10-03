const boardId = pathname.substr(pathname.lastIndexOf("/") + 1);

/*header*/
$(document).on('keyup', '#title', async (e) => {
    if (e.keyCode === 13) {
        e.target.blur();
        const request = {title: $("#title").val(), target: "title"};
        await saveBoard(request);
    }
})

$(document).on('keyup', '#description', async (e) => {
    if (e.keyCode === 13) {
        e.target.blur();
        const request = {description: $("#description").val(), target: "description"};
        await saveBoard(request);
    }
})

$(document).on('change', '#status', async () => {
    const request = {status: $("#status option:selected").val(), target: "status"};
    await saveBoard(request);
})

$(document).on('change', '#categoryOption', async () => {
    const categoryId = $("#categoryOption option:selected").val();

    if (categoryId === '' || categoryId === undefined) {
        alert("카테고리를 선택하세요");
        return;
    }

    const request = {categoryId: categoryId, target: "category"};
    await saveBoard(request);
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

$(document).on('keyup', '.tag', (e) => {
    if (e.keyCode === 13) {
        const url = "/api/tag?boardId=" + boardId;
        const request = [];

        $('.tag').each((idx, tag) => {
            request.push({tag: tag.value});
        })

        put(url, request)
            .then(() => {
                $('#tags').append(`<input class="tag" placeholder="#태그 추가">`);
            })
            .catch((error) => {
                alert("태그 저장에 실패했습니다!");
                console.error(error);
            });
    }
});

/*comment*/
$(document).on('click', '#addCommentBtn', (e) => {
    const input = document.createElement('textarea');
    input.setAttribute("id", "addCommentInput");
    input.setAttribute("placeholder", "댓글을 작성해주세요. (엔터 시 저장됩니다.)");
    e.target.parentElement.after(input);
})

$(document).on('keyup', '#addCommentInput', async (e) => {
    if (e.keyCode === 13) {
        const request = {comment: e.target.value}
        await commentReq(request);
        e.target.readOnly = true;
    }
})

$(document).on('click', '.editCommentBtn', (e) => {
    const commentBox = e.target.nextElementSibling.lastElementChild;
    commentBox.readOnly = false;
    commentBox.focus();
})

$(document).on('keyup', '.comment-body', async (e) => {
    if (e.keyCode === 13) {
        const request = {
            comment: e.target.value,
            id: e.target.parentElement.parentElement.id
        }
        await commentReq(request);
    }
})

$(document).on('click', '.delCommentBtn', async (e) => {
    if (confirm("정말로 삭제하시겠습니까?")) {
        const commentId = e.target.parentElement.id;
        e.target.parentElement.remove();
        await delCommentReq(commentId);
    }
})

/*contents*/
$(document).on('click', '.addContentsBtn', async (e) => {
    let contentId = await addContentReq("/api/contents/" + boardId);

    const newContentDiv = document.createElement("div");
    newContentDiv.setAttribute("class", "contentContainer")
    newContentDiv.setAttribute("id", contentId)

    const targetLocation = e.target.parentElement.parentElement;
    targetLocation.after(newContentDiv)

    const newContentInput = document.createElement("textarea");
    newContentInput.setAttribute("class", "content")
    newContentInput.setAttribute("placeholder", "내용을 입력하세요")

    const newButtons = document.createElement("div");
    newButtons.setAttribute("class", "content__buttons");

    const newContentDelBtn = document.createElement("button");
    newContentDelBtn.setAttribute("type", "button");
    newContentDelBtn.setAttribute("class", "delContentsBtn");
    newContentDelBtn.innerHTML = "-";

    const newContentAddBtn = document.createElement("button");
    newContentAddBtn.setAttribute("type", "button");
    newContentAddBtn.setAttribute("class", "addContentsBtn");
    newContentAddBtn.innerHTML = "+";

    newButtons.appendChild(newContentDelBtn);
    newButtons.appendChild(newContentAddBtn);

    newContentDiv.appendChild(newContentInput);
    newContentDiv.appendChild(newButtons);
})

$(document).on('click', '.delContentsBtn', async (e) => {
    const target = e.target.parentElement;
    const delContentRes = await delContentReq("/api/contents/" + boardId + "?id=", target.id);
    if (delContentRes) {
        target.remove()
    }
})

$(document).on('keyup', '.content', (e) => {
    if (e.keyCode === 13) {
        if (!e.shiftKey) {
            const contentId = e.target.parentElement.id;
            const url = "/api/contents/" + boardId + "?contentId=" + contentId;
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

const saveContents = async (url, request) => {
    await fetch(url, {
        method: "PUT",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: "cors",
        body: JSON.stringify(request)
    })
        .then((response) => response.json())
        .then((data) => {
            return data.success
        })
        .catch((error) => console.log("실패 : ", error));
}

const commentReq = async (request) => {
    const url = "/api/comment?boardId=" + boardId;

    await fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: "cors",
        body: JSON.stringify(request)
    })
        .then((response) => response.json())
        .then((success) => {
            if (success) location.reload();
        })
        .catch((error) => console.log("실패 : ", error));

}

const delCommentReq = async (commentId) => {
    const url = "/api/comment?commentId=" + commentId;
    await fetch(url, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: "cors",
    })
        .catch((error) => console.log("실패 : ", error));
}

const addContentReq = async (url) => {
    let id = undefined;
    await fetch(url, {
        method: "GET",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: "cors",
    })
        .then((response) => response.json())
        .then((data) => {
            if (data.success) id = data.data
        })
        .catch((error) => console.log("실패 : ", error));
    return id;
}

const delContentReq = async (url, id) => {
    let success = undefined;

    await fetch(url + id, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: "cors",
    })
        .then((response) => {
            success = response
        })
        .catch((error) => console.log("실패 : ", error));

    return success
}

function moveToPreviousPage() {
    history.go(-1);
}