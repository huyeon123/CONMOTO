const host = "http://" + window.location.host;
const pathname = window.location.pathname;
const boardId = pathname.substr(pathname.lastIndexOf("/"));

/*header*/
$('#title').on('keyup', async (e) => {
    if (e.keyCode === 13) {
        e.target.blur();
        await extractHeaderValue();
    }
})

$('#status').on('change', async () => {
    await extractHeaderValue();
})

/*comment*/
$('#addCommentBtn').on('click', (e) => {
    const input = document.createElement('input');
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
    let contentId = await addContentReq(host + "/api/contents" + boardId);

    const newContentDiv = document.createElement("div");
    newContentDiv.setAttribute("class", "contentContainer")
    newContentDiv.setAttribute("id", contentId)

    const targetLocation = e.target.parentElement;
    targetLocation.after(newContentDiv)

    const newContentInput = document.createElement("input");
    newContentInput.setAttribute("class", "content")
    newContentInput.setAttribute("placeholder", "내용을 입력하세요")

    const newContentDelBtn = document.createElement("button");
    newContentDelBtn.setAttribute("type", "button");
    newContentDelBtn.setAttribute("class", "delContentsBtn");
    newContentDelBtn.innerHTML = "-";

    const newContentAddBtn = document.createElement("button");
    newContentAddBtn.setAttribute("type", "button");
    newContentAddBtn.setAttribute("class", "addContentsBtn");
    newContentAddBtn.innerHTML = "+";

    newContentDiv.appendChild(newContentInput);
    newContentDiv.appendChild(newContentDelBtn);
    newContentDiv.appendChild(newContentAddBtn);
})

$(document).on('click', '.delContentsBtn', async (e) => {
    const target = e.target.parentElement;
    const delContentRes = await delContentReq(host + "/api/contents" + boardId + "/?id=", target.id);
    if (delContentRes) {
        target.remove()
    }
})

$(document).on('keyup', '.content', (e) => {
    if (e.keyCode === 13) {
        const contentId = e.target.parentElement.id;
        const url = host + "/api/contents" + boardId + "?contentId=" + contentId;
        const request = {content: e.target.value};
        e.target.blur();
        saveContents(url, request);
    }
})

async function extractHeaderValue() {
    const url = host + "/api/board" + boardId;
    const selectOpt = $('#status option:selected').val();
    const request = {title: this.title.value, status: selectOpt};
    await saveBoard(url, request);
}

const saveBoard = async (url, request) => {
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
    const url = host + "/api/comment/?boardId=" + boardId.substr(1);

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
    const url = host + "/api/comment/?commentId=" + commentId;
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