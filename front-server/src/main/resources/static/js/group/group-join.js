function applyJoin() {
    const url = "/api/group/join";
    const request = {
        groupUrl: groupUrl,
        nickname: $("#js-group-nickname").val(),
    }

    post(url, request)
        .then(res => res.text())
        .then(success => {
            if (success) alert("그룹에 가입했습니다!");
            else alert("그룹에 가입 신청했습니다.");
        })
}