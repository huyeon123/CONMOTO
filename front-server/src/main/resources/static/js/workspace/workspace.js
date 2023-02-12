function moveToCategoryPage(obj) {
    window.location.href = "/workspace/" + groupUrl + "/" + obj.outerText.substring(2);
}

function moveToGroupManagingPage() {
    location.href = pathname + "/manage";
}

function logout() {
    get("/auth/logout")
        .then(res => {
            if (res.ok) location.href = "/";
        })
        .catch(error => {
            alert("로그아웃 실패");
            console.error(error);
        })
}

$(document).on('mouseover', '.super-space-app__sidebar', () => {
    $("#sidebar__fold").animate({
        opacity: 1
    }, 'fast');
})

$(document).on('mouseleave', '.super-space-app__sidebar', () => {
    $("#sidebar__fold").animate({
        opacity: 0
    }, 'fast');
})

$(document).on('click', '#sidebar__fold', () => {
    $(".super-space-app__sidebar").toggle('fast', 'linear');
    $("#js-sidebar-expand-btn").toggle('fast', 'linear');
    offDimmed();
})

$(document).on('click', '#js-sidebar-expand-btn', () => {
    $("#js-sidebar-expand-btn").toggle();
    $(".super-space-app__sidebar").toggle('fast', 'swing');
    onDimmed();
});

$(document).on('click', '.sidebar-mobile-dimmed', () => {
    $(".super-space-app__sidebar").toggle('fast', 'linear');
    $(".sidebar-mobile-dimmed").css('display', 'none');
})

let sidebarHeaderSize;

$(() => {
    sidebarHeaderSize = getSidebarHeaderSize() + 25 + 20;
    const maxHeight = adjustSideBarMainHeight();
    addGoTopIfOverflow(maxHeight);
    dimmedSideBar();
})

function adjustSideBarMainHeight() {
    const viewHeight = window.innerHeight;
    const maxHeight = viewHeight - sidebarHeaderSize;

    const $scroll = $(".sidebar-menu-scroll");
    $scroll.css("maxHeight", maxHeight);

    return maxHeight;
}

function addGoTopIfOverflow(maxHeight) {
    const $scroll = $(".sidebar-menu-scroll");
    const height = $scroll.height();

    if (height >= maxHeight - 10) {
        const $goTop = $(".sidebar-go-top");
        $goTop.css('display', 'flex');
        $goTop.click(() => {
            $scroll.scrollTop(0);
        })

        const $category = $(".category");
        $category.css("padding-bottom", "15px");
        $category.css("border-bottom", "1px solid #eee");
    }
}

$(window).resize(() => {
    dimmedSideBar();
    adjustSideBarMainHeight();
})

function dimmedSideBar() {

    const width = window.outerWidth;

    if (width < 768) $(".super-space-app__sidebar").hide();
    else $(".super-space-app__sidebar").show();

    $(".sidebar-mobile-dimmed").hide();
}

function onDimmed() {
    const width = window.outerWidth;
    if (width < 768) $(".sidebar-mobile-dimmed").show();
}

function offDimmed() {
    const width = window.outerWidth;
    if (width < 768) $(".sidebar-mobile-dimmed").hide();
}

function getSidebarHeaderSize() {
    return $(".sidebar > header").height();
}

$(".sidebar-header .expand").click((e) => {
    const $main = $(e.target).parents(".sidebar-section").find("main");
    $main.toggle();
})