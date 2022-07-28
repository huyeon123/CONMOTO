const host = "http://" + window.location.host;
const pathname = window.location.pathname;
const id = pathname.substr(pathname.lastIndexOf("/"));

async function save() {
    const request = {};

    let selectOption = document.getElementById("status");
    selectOption = selectOption.options[selectOption.selectedIndex].value;

    const contents = [];
    let order = 0;
    let contentElements = document.getElementsByName("inputBox");
    for (let content of contentElements.values()) {
        contents[order] = {order: order, content: content.value};
        order++;
    }

    request.title = this.title.value;
    request.status = selectOption;
    request.contents = contents;
    console.log(request)

    let boardStatus = saveBoard(host + "/api/board" + id, request);
    let contentsStatus = saveContents(host + "/api/contents" + id, request);

    if (boardStatus && contentsStatus) {
        alert("성공적으로 저장되었습니다.")
        window.location.href = host + "/user/feed"
    }
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
        body: JSON.stringify(request.contents)
    })
        .then((response) => response.json())
        .then((data) => {
            return data.success
        })
        .catch((error) => console.log("실패 : ", error));
}