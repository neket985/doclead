function redirect(str) {
    window.location = str;
}

function apiPost(url, data, success) {
    return $.ajax({
        type: "POST",
        url: "http://0.0.0.0:8081" + url,
        data: JSON.stringify(data),
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + getJwtFromSession());
        },
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: success
    });
}

function apiGet(url, success) {
    $.ajax({
        type: "GET",
        url: "http://0.0.0.0:8081" + url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + getJwtFromSession());
        },
        success: success
    });
}

function getJwtFromSession() {
    var cookie = getCookie("USER_JWT_SESSION");
    return cookie.substring(7, cookie.length - 1)
}


function getCookie(name) {
    var matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}