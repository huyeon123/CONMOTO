const pathname = decodeURI(window.location.pathname);
const groupUrlStartIdx = String("/workspace").length + 1;
const groupUrlEndIdx = pathname.indexOf("/", groupUrlStartIdx + 1);
const groupUrl = groupUrlEndIdx === -1 ?
    pathname.substring(groupUrlStartIdx) : pathname.substring(groupUrlStartIdx, groupUrlEndIdx);

async function get(url) {
    return await fetch(url, {
        method: "GET",
        mode: 'cors',
    });
}

async function post(url, body) {
    return postOrPut(url, body, "POST");
}

async function put(url, body) {
    return postOrPut(url, body, "PUT");
}

async function postOrPut(url, body, method) {
    return await fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify(body)
    });
}

async function del(url, body) {
    return await fetch(url, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify(body)
    });
}

async function delWithoutBody(url) {
    return await fetch(url, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors'
    });
}

function getData(response) {
    return response.json()
        .catch((error) => console.error("json parsing error : ", error));
}

function canGetData(res) {
    return res.status === 200 || res.status === 201;
}