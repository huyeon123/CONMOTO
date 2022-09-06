$(document).on('click', '.delBoardBtn', async (e) => {
    if (confirm("정말로 삭제하시겠습니까?")) {
        let target = e.target
            .parentElement
            .previousElementSibling
            .firstElementChild;
        let success = await delBoardReq("/api/board/" + target.id);
        if (success) {
            target.parentElement.parentElement.remove();
        }
    }
});

$(document).on('click', '.delCommentBtn', async (e) => {
    if (confirm("정말로 삭제하시겠습니까?")) {
        let target = e.target
            .parentElement
            .previousElementSibling
        let success = await delCommentReq("/api/comment?commentId=" + target.id);
        if (success) {
            target.parentElement.remove();
        }
    }
});

async function createBoard(url = "/api/board") {
    await fetch(url, {
        method: "GET",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: "cors",
    })
        .then((response) => response.json())
        .then((data) => {
            alert(data.message);
            if (data.success) {
                window.location.href = "/board/" + data.data;
            }
        })
        .catch((error) => console.log("실패 : ", error));
}

const delBoardReq = async (url) => {
    let success = undefined;
    await fetch(url, {
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

    return success;
}

const delCommentReq = async (url) => {
    let success = undefined;
    await fetch(url, {
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

    return success;
}

async function save(){
    const url = "/api/user/edit";
    const request = {
        email : this.email.value,
        name : this.username.value,
        password : this.password.value,
        birthday : this.birthday.value
    };

    const res = put(url, request);
    res.then(data => {
        if (data) {
            alert("회원정보를 수정했습니다.");
        } else {
            alert("회원정보 수정에 실패했습니다.");
        }
    }).catch((error) => console.error(error));
}