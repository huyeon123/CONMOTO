$(document).on('keyup', '#email', (e) => {
    if (e.keyCode === 13) {
        $('.submit-box').click();
    }
})

function signUpRequest() {
    generateLoginCode()
        .then(res => {
            if (res.ok) {
                appendSignUpDescription();
                changeSubmit();
            }
        })
        .catch(error => console.error(error));
}

function appendSignUpDescription() {
    $('.js-sign-up-description').append(
        `<div class="sign-up-description-text" style="text-align:center; font-size: 11pt; color: gray; margin-bottom: 10px">
            해당 이메일로 임시 로그인 코드를 전송했습니다.<br>
            받은 메일함을 확인하시고 아래에 로그인 코드를 붙여 넣어 주세요.<br>
            서버 상황에 따라 수 분 소요될 수 있습니다.
        </div>
        <label for="login-code" class="input-label">로그인 코드</label>
        <div class="input-box">
            <input type="text"
                   placeholder="로그인 코드를 입력해주세요."
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
    $form.attr('onclick', 'signUpSubmit()');
}

function signUpSubmit() {
    const url = "/auth/signup";
    const request = {
        email: $('#email').val(),
        loginCode: $('#login-code').val()
    };

    post(url, request)
        .then(res => {
            if (res.ok) {
                tempLoginSubmit();
            } else {
                res.json().then(error => alert(error.message));
            }
        }).catch(error => {
        alert("전송에 문제가 발생했습니다!");
        console.error(error);
    })
}

function tempLoginSubmit() {
    const url = "/auth/login/temp";
    const request = {
        email: $('#email').val(),
        loginCode: $('#login-code').val()
    };

    post(url, request)
        .then(res => {
            if (res.ok) {
                alert("보안을 위해 비밀번호를 변경해주세요!\n사이드 바 > 내 활동 > 비밀번호 변경");
                location.href = "/community";
            } else {
                res.json().then(error => alert(error.message));
            }
        }).catch(error => {
        alert("전송에 문제가 발생했습니다!");
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

function initializePassword() {
    generateLoginCode()
        .then(res => {
            if (res.ok) {
                appendSignUpDescription();
                changeSubmitInit();
            }
        })
        .catch(error => {
            alert("회원가입에 실패했습니다!");
            console.error(error)
        });
}

function changeSubmitInit() {
    const $form = $('form .submit-box');
    $form.text('임시 로그인');
    $form.attr('onclick', 'tempLoginSubmit()');
}

async function generateLoginCode(url = "/auth/login-code") {
    const request = {email: this.email.value};
    if (isNotEmailFormat(request.email)) return;

    return await post(url, request);
}

function post(url, request) {
    return fetch(url, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify(request)
    });
}