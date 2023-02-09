$(document).on('keyup', '#password', (e) => {
    if (e.keyCode === 13) {
        formSubmit();
    }
})

function formSubmit(url = "/auth/login") {
    const request = {
        email: this.email.value,
        password: this.password.value,
    };

    if (request.password.length < 5) {
        alert("비밀번호는 5자 이상입니다.");
        return;
    }

    fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify(request)
    }).then((res) => {
            if (res.status === 200) {
                window.location.href = "/workspace";
            } else if (res.status === 202) {
                res.text().then(data => alert(data));
            } else {
                alert("서버에 문제가 있습니다! 관리자에게 문의주세요.");
            }
        });
}