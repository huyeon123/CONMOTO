const baseUrl = "http://" + window.location.host;
const pathname = window.location.pathname;
const groupUrlStartIdx = String("/workspace").length + 1;
const groupUrlEndIdx = pathname.indexOf("/", groupUrlStartIdx + 1);
const groupUrl =  groupUrlEndIdx === -1 ?
    pathname.substring(groupUrlStartIdx) : pathname.substring(groupUrlStartIdx, groupUrlEndIdx);

async function get(url) {
    const response = await fetch(url, {
        method: "GET",
        mode: 'cors',
    });

    return getData(response);
}

async function post(url, body) {
    return postOrPut(url, body, "POST");
}

async function put(url, body) {
    return postOrPut(url, body, "PUT");
}

async function postOrPut(url, body, method) {
    const response = await fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify(body)
    });

    return getData(response);
}

function getData(response) {
    return response.json()
        .catch((error) => console.log("실패 : ", error));
}

async function del(url, body) {
    const response = await fetch(url, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify(body)
    });

    return getData(response);
}