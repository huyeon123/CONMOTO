function moveToCategoryPage(obj) {
    window.location.href = "/workspace/" + groupUrl + "/" + obj.outerText.substring(2);
}

function moveToCreateGroupPage() {
    window.location.href = "/workspace/new";
}

function moveToGroupManagingPage(url) {
    window.location.href = "/workspace/" + url + "/manage";
}

function moveToCreateCategoryPage() {
    window.location.href = "/workspace/" + groupUrl + "/new-category";
}

function moveToManageCategoryPage() {
    window.location.href = "/workspace/" + groupUrl + "/category";
}

function moveToLogoutPage() {
    window.location.href = "/logout";
}

$(document).on('mouseover', '#super-space-app__sidebar', () => {
    $("#sidebar__fold").animate({
        opacity: 1
    }, 'fast');
})

$(document).on('mouseleave', '#super-space-app__sidebar', () => {
    $("#sidebar__fold").animate({
        opacity: 0
    }, 'fast');
})

$(document).on('click', '#sidebar__fold', () => {
    $("#super-space-app__sidebar").toggle('fast', 'linear');
    $("#sidebar__expand").toggle('fast', 'linear');
})

$(document).on('click', '#sidebar__expand', () => {
    $("#sidebar__expand").toggle();
    $("#super-space-app__sidebar").toggle('fast', 'linear');
});