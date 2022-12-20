$(function () {
    const today = new Date();
    const todayStr =
        "현재 날짜 : " + today.getFullYear() + "년 " +
        (today.getMonth() + 1) + "월 " +
        today.getDate() + "일";

    $("#today").text(todayStr);

    const expireDate = new Date(today.setDate(today.getDate() + 15));
    const expireStr =
        "만료 날짜 : " + expireDate.getFullYear() + "년 " +
        (expireDate.getMonth() + 1) + "월 " +
        expireDate.getDate() + "일";

    $("#expire-date").text(expireStr);
});

async function resignReq() {
    if(shouldBeChecked()) {
        alert("동의란에 체크해주세요.")
        return;
    }

    const url = "/api/user/resign";
    const res = delWithoutBody(url);
    res.then(success => {
        if (success) {
            alert("회원 탈퇴가 완료되었습니다.")
            location.href = "/";
        }
    });
}

function shouldBeChecked() {
    return !this.agree.value;
}
