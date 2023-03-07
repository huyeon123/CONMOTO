
function pageRender() {
}


    getList(url)
        .then(res => res.json())
        .then(data => drawBoards(data))
        .catch(error => console.error(error));
}