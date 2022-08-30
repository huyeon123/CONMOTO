$(function () {
    content_scroll_plugin();
});

function content_scroll_plugin() {
    $(".scroll-section").mCustomScrollbar({
        theme: "minimal-dark",
        mouseWheelPixels: 150,
        scrollInertia: 300, // 부드러운 스크롤 효과 적용
        callbacks: {
            onTotalScroll: function () {
                console.log("BOTTOM!")
                getList();
            },
            onTotalScrollOffset: 100,
            alwaysTriggerOffsets: false,
        }
    });
}