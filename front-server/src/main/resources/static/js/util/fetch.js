const pathname = decodeURI(window.location.pathname);
const groupUrlStartIdx = String("/workspace").length + 1;
const groupUrlEndIdx = pathname.indexOf("/", groupUrlStartIdx + 1);
const groupUrl = groupUrlEndIdx === -1 ?
    pathname.substring(groupUrlStartIdx) : pathname.substring(groupUrlStartIdx, groupUrlEndIdx);
let accessToken = null;

$(function () {
    if (isMainPage()) return;

    init()
        .then(() => console.log("[Page Initiation Success]"));
});

function isMainPage() {
    const currentPath = window.location.pathname;
    return !!currentPath.match("^\/[\w]*$");
}

async function init() {
    try {
        accessToken = "Bearer " + await getAccessToken();
        connectNotyService();
    } catch(error) {
        console.error(error);
    }

    pageRender().catch();
}

function getAccessToken() {
    const response = fetch("/auth/refresh", {
            method: 'GET',
            mode: 'cors'
    });
    return response.then(res => res.text());
}

async function get(url) {
    return await fetch(url, {
        method: "GET",
        headers: {
            'Authorization': accessToken
        },
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
            'Content-Type': 'application/json',
            'Authorization': accessToken
        },
        mode: 'cors',
        body: JSON.stringify(body)
    });
}

async function del(url, body) {
    return await fetch(url, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': accessToken
        },
        mode: 'cors',
        body: JSON.stringify(body)
    });
}

async function delWithoutBody(url) {
    return await fetch(url, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': accessToken
        },
        mode: 'cors'
    });
}

function getJson(response) {
    return response.json()
        .catch((error) => console.error("json parsing error : ", error));
}

function canGetData(res) {
    return res.ok;
}