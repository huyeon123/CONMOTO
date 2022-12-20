let check = false;

$(document).on('keyup', '#password', (e) => {
    if (e.keyCode === 13) {
        formSubmit();
    }
})

function formSubmit(url = "/auth/signup") {
    const request = {};

    request.name = this.username.value;
    if (request.name.length < 1) {
        alert("이름을 입력해주세요");
        return;
    }

    request.email = this.email.value;
    if (!check) {
        alert("이메일 중복체크를 확인해주세요.");
        return
    }

    request.password = this.password.value;
    if (request.password.length < 5) {
        alert("비밀번호는 최소 5자리입니다.");
        return
    }
    request.birthday = this.birthday.value;

    post(url, request)
        .then((res) => {
            if (res.ok) {
                window.location.href = "/";
            } else {
                alert("회원가입에 실패했습니다!");
            }
        }).catch(error => console.error(error));
}

function duplicateCheck(url = "/auth/check") {
    const request = {"body": this.email.value};

    if (isNotEmailFormat(request.body)) return;

    post(url, request)
        .then(res => {
            if (canGetData(res)) return getJson(res);
        })
        .then((isDuplicate) => {
            if (isDuplicate) {
                alert("중복된 이메일입니다.");
            } else {
                alert("사용 가능한 이메일입니다.");
                check = true;
            }
        })
}

function isNotEmailFormat(email) {
    const regEmail = /^[\w]+@[\w-]+.[a-zA-Z]{2,3}$/;

    if (!regEmail.test(email)) {
        alert("유효하지 않은 이메일 형식입니다.")
        return true;
    }
    return false;
}