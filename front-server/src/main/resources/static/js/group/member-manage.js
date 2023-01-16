async function save() {
    const url = "/api/group/" + groupUrl + "/member-role";
    const request = [];

    $(".member-info").each((idx) => {
        const email = $('.member-email').get(idx).outerText;
        const authority = $('.member-authority').get(idx).value;

        request.push({
            email: email,
            role: authority === "일반 멤버" ? "ROLE_MEMBER" : "ROLE_MANAGER"
        });
    })

    post(url, request)
        .then(res => {
            if (res.ok) {
                alert("변경 사항이 반영되었습니다.");
            }
        })
}

function expelMember(id) {
    const url = "/api/group/" + groupUrl + "/member";
    const memberInfo = $(".member-info#member_" + id);
    const email = memberInfo.children(".member-email").text();
    const request = {email: email};

    del(url, request)
        .then(() => memberInfo.remove())
        .catch(error => {
            alert("그룹 추방에 실패했습니다!");
            console.error(error);
        });
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