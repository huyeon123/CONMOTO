const categoryNameStartIdx = pathname.lastIndexOf("/") + 1;
const categoryName = pathname.substring(categoryNameStartIdx);

function pageRender() {
    drawBoardsOfCategory();
    content_scroll_plugin(drawBoardsOfCategory);
}

function drawBoardsOfCategory() {
    const url = "/api/board/latest/category?name=" + categoryName + "&page=" + nextPage;

    getList(url)
        .then(res => res.json())
        .then(data => drawBoards(data))
        .catch(error => console.error(error));
}