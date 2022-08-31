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