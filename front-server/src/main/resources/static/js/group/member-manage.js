async function save() {
    const url = "/api/group/" + groupUrl + "/member";
    const request = [];
    $(".member-info").each((idx) => {
        const email = $('.member-email').get(idx).outerText;
        const authority = $('.member-authority').get(idx).value;
        request.push({
            email: email,
            groupRole: authority
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
        .then(res => {
            if (res.ok) {
                const name = memberInfo.children(".member-name").text();
                memberInfo.remove();
                alert(name + " 사용자가 그룹에서 추방되었습니다.")
            }
        });
}

function activateExpelMemberBtn() {
    $(".expel-btn")
        .attr("disabled", false)
        .css("opacity", 1);

    $('#activate-expel')
        .html("멤버 강퇴 취소")
        .attr("class", "default")
        .attr("onclick", "cancelExpelMemberBtn()");
}

function cancelExpelMemberBtn() {
    $(".expel-btn")
        .attr("disabled", true)
        .css("opacity", 0.5);

    $('#activate-expel')
        .html("멤버 강퇴 활성화")
        .attr("class", "warn")
        .attr("onclick", "activateExpelMemberBtn()");
}