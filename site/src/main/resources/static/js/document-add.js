document.addEventListener("DOMContentLoaded", function (evt) {
    $('#doc-toggle').on('change', function (e) {
        setTimeout(function () {
            var labelForAtivate;
            var labelForDeative;
            if (e.target.checked) {
                labelForAtivate = $('.for-checked');
                labelForDeative = $('.for-unchecked');
            } else {
                labelForAtivate = $('.for-unchecked');
                labelForDeative = $('.for-checked');
            }

            labelForAtivate.removeClass("inactive");
            labelForDeative.addClass("inactive");
        }, 400);
    });

    $('#branch-add').on('click', function (e) {
        var name = $('#branch-name')[0].value;
        var path = location.pathname.replace('/project/', '');
        var uid = path.substr(0, path.indexOf('/'));
        apiPost("/project/branch/add", {name: name, projectUid: uid}, function (data) {
            location.href = ""
        })
    });
});