var captchaId;

var onloadCallback = function() {
    captchaId = grecaptcha.render('g-recaptcha', {
        "sitekey" : "6LespE4UAAAAADq77qSpD0PRvUisD18lEbM7e-AH",
        "callback" : "onSuccessCaptcha",
        "theme" : "light"
    });
};

var onSuccessCaptcha = function(response) {
    let errorDivs = document.getElementsByClassName("recaptcha-error");
    if (errorDivs.length) {
        errorDivs[0].className = "";
    }
    let errorMsgs = document.getElementsByClassName("recaptcha-error-message");
    if (errorMsgs.length) {
        errorMsgs[0].hidden = true;
    }
};

function checkEmail() {
    let email = document.getElementById("email").value;

    if (!email.match(/^[0-9a-z-\.]+\@[0-9a-z-]{2,}\.[a-z]{2,}$/i)) {
        document.getElementById("email").className = "error";
        return false;
    }
    document.getElementById("email").className = "";
    return true;
}

function checkFields() {
    return checkPassword() && checkReCatcha() && checkEmail();
}

function checkPassword() {
    let password = document.getElementById("password").value;
    let subtrendPassword = document.getElementById("subtrend_password").value;
    checkReCatcha();
    if(password !== subtrendPassword) {
        document.getElementById("password").className = "error";
        document.getElementById("subtrend_password").className = "error";
        return false;
    }
    document.getElementById("password").className = "";
    document.getElementById("subtrend_password").className = "";

    return true;
}

function checkReCatcha() {
    if(grecaptcha.getResponse(captchaId) === "") {
        showErrorMessage();
        return false;
    }
    return true;
}

function showErrorMessage() {
    let border = document.getElementById("recaptcha-border");
    border.className = "recaptcha-error";

    let errorMsgs = document.getElementsByClassName("recaptcha-error-message");
    if (errorMsgs.length) {
        errorMsgs[0].hidden = false;
    }
}

function saveUser() {
    let user = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value,
        email: document.getElementById("email").value,
        token: "",
        enabled: 0
    };

    sendRequest("POST", "create-user", user, parseResponse);

}

function parseResponse(response) {
    if(!response.emailCorrect && !response.usernameCorrect && !response.emailSent)
        alert("Ошибка! См. лог");
    else if(!response.emailCorrect && !response.usernameCorrect)
        alert("Указанный логин и e-mail заняты! Укажите другие данные.");
    else if(!response.emailCorrect)
        alert("Указанный e-mail занят! Укажите другой.");
    else if(!response.usernameCorrect)
        alert("Указанный логин занят! Укажите другой.");
    else if(!response.emailSent)
        alert("Произошла ошибка при отправке письма-подтверждения. Обратитесь в службу поддержки");
    else
        document.location.replace("http://localhost:8084/successfully");
}