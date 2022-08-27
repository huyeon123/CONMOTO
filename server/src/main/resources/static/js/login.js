$(document).on('keyup', '#password', (e) => {
    if (e.keyCode === 13) {
        formSubmit();
    }
})

function formSubmit(url = "/auth/login") {
    const request = {};
    request.email = this.email.value
    request.password = this.password.value
    request.rememberMe = this.rememberMe.value === "on";

    const data = post(url, request);
    data.then((data) => {
        if (data) {
            window.location.href = "/workspace";
        } else {
            alert("잘못된 이메일/비밀번호 입니다!");
        }
    })
}