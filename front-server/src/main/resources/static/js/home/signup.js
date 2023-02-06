let check = false;

$(document).on('keyup', '#password', (e) => {
    if (e.keyCode === 13) {
        formSubmit();
    }
})

function formSubmit(url = "/auth/login-code") {
    const request = {email: this.email.value};
    if (isNotEmailFormat(request.email)) return;

    fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify(request)
    })
        .then(res => {
            if (res.ok){
                appendSignUpDescription();
                changeSubmit();
            }
        })
        .catch(error => {
            alert("회원가입에 실패했습니다!");
            console.error(error)
        });
}

function appendSignUpDescription() {
    $('.js-sign-up-description').append(
        `<div class="sign-up-description-text" style="text-align:center; font-size: 11pt; color: gray; margin-bottom: 10px">
            해당 이메일로 임시 로그인 코드를 전송했습니다.<br>
            받은 메일함을 확인하시고 아래에 로그인 코드를 붙여 넣어 주세요.
        </div>
        <label for="login-code" class="input-label">로그인 코드</label>
        <div class="input-box">
            <input type="text"
                   placeholder="이메일을 입력해주세요."
                   id="login-code"
                   name="login-code"
                   required
                   autofocus>
        </div>`
    );
}

function changeSubmit() {
    const $form = $('form .submit-box');
    $form.text('계정 생성');
    $form.attr('onclick', 'generateAccount()')
}

function generateAccount(url = "/auth/signup") {
    const request = {
        email: $('#email').val(),
        loginCode: $('#login-code').val()
    };

    fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify(request)
    }).then(res => {
        if (res.ok) {
            location.href = "/workspace";
        }
    }).catch(error => {
        alert("계정 생성에 실패했습니다!");
        console.error(error);
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