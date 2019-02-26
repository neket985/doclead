document.addEventListener("DOMContentLoaded", function (evt) {

    $('#author-add').on('click', function (e) {
        var name = $('#author-name')[0].value;
        var uid = location.pathname.replace('/project/', '');
        apiPost("/project/author/add", {name: name, projectUid: uid}, function (data) {
            location.href = ""
        })
    });
});