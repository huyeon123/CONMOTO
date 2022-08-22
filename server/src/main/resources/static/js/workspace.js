const currentPage = window.location.pathname;

function moveToCategoryPage(obj) {
    window.location.href = currentPage + "/" + obj.outerText;
}

function moveToCreateGroupPage() {
    window.location.href = "/workspace/new";
}

function moveToGroupManagingPage() {
    window.location.href = currentPage + "/manage";
}

function moveToCreateCategoryPage() {
    window.location.href = currentPage + "/new-category";
}

function moveToManageCategoryPage() {
    window.location.href = currentPage + "/category";
}