$(document).on('keyup', '#password', (e) => {
    if (e.keyCode === 13) {
        formSubmit();
    }
})

function formSubmit(url = "/auth/login") {
    const request = {
        email: this.email.value,
        password: this.password.value,
        rememberMe: this.rememberMe.value === "on"
    };

    post(url, request)
        .then((res) => {
            if (res.ok) {
                window.location.href = "/workspace";
            } else {
                alert("잘못된 이메일/비밀번호 입니다!");
            }
        });
}