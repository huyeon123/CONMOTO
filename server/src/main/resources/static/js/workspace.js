function moveToCategoryPage(obj) {
    window.location.href = "/workspace/" + groupUrl + "/" + obj.outerText;
}

function moveToCreateGroupPage() {
    window.location.href = "/workspace/new";
}

function moveToGroupManagingPage() {
    window.location.href = "/workspace/" + groupUrl + "/manage";
}

function moveToCreateCategoryPage() {
    window.location.href = "/workspace/" + groupUrl + "/new-category";
}

function moveToManageCategoryPage() {
    window.location.href = "/workspace/" + groupUrl + "/category";
}