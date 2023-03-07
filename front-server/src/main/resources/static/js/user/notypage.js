
function pageRender() {
    getNoty()
        .then(res => res.json())
        .then(data => {
        })
        .catch(error => console.error(error));
}

function getNoty() {
    isFetching = true;
    const url = "/api/noty/" + page;
    return get(url);
}

}