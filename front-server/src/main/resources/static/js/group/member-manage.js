}

}

function activateExpelMemberBtn() {
    $(".js-expel-btn")
        .attr("disabled", false)
        .css("opacity", 1);

    $('#activate-expel')
        .html("멤버 강퇴 취소")
        .attr("class", "default-style-btn default")
        .attr("onclick", "cancelExpelMemberBtn()");
}

function cancelExpelMemberBtn() {
    $(".js-expel-btn")
        .attr("disabled", true)
        .css("opacity", 0.5);

    $('#activate-expel')
        .html("멤버 강퇴 활성화")
        .attr("class", "default-style-btn warn")
        .attr("onclick", "activateExpelMemberBtn()");
}