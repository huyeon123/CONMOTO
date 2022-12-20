const today = new Date();

$("#today").text(
    today.getFullYear() + "년 " +
    (today.getMonth() + 1) + "월 " +
    today.getDate() + "일"
);

async function resignReq() {
    if (shouldBeChecked()) {
        alert("동의란에 체크해주세요.")
        return;
    }

    const url = "/api/group/" + groupUrl;
    delWithoutBody(url)
        .then(res => {
            if (res.ok) {
                alert("그룹이 삭제되었습니다.");
                location.href = "/workspace";
            } else {
                getJson(res).then(message => alert(message));
            }
        })
}

function shouldBeChecked() {
    return !this.agree.checked;
}