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

    const url = "/api/group?groupUrl=" + groupUrl;
    const res = delWithoutBody(url);
    res.then(data => {
        alert(data);
        location.href = "/workspace.js";
    })
}

function shouldBeChecked() {
    return !this.agree.checked;
}