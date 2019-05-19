document.addEventListener("DOMContentLoaded", function (evt) {
    $('#access-toggle').on('change', function (e) {
        console.log(1);
        var uid = location.pathname.replace('/project/', '');
        apiPost("/project/access/toggle", {uid: uid}, function (data) {
            var datachecked = data.accessByLink;
            if (datachecked === true || datachecked === false) {
                e.target.checked = datachecked;
                var labelForAtivate;
                var labelForDeative;
                if (e.target.checked) {
                    labelForAtivate = $('.label-accesed')[0];
                    labelForDeative = $('.label-unaccesed')[0];
                } else {
                    labelForAtivate = $('.label-unaccesed')[0];
                    labelForDeative = $('.label-accesed')[0];
                }

                if (labelForAtivate.classList.contains("inactive")) {
                    labelForAtivate.classList.remove("inactive")
                }
                if (!labelForDeative.classList.contains("inactive")) {
                    labelForDeative.classList.add("inactive")
                }
            }
            else {
                e.target.checked = !e.target.checked
            }
        }).fail(function () {
            e.target.checked = !e.target.checked
        });
    });

    $('.author-remove').on('click', function (e) {
        var author = e.target.dataset.name;
        var uid = location.pathname.replace('/project/', '');
        apiPost("/project/author/remove", {name: author, projectUid: uid}, function (data) {
            location.href = ""
        })
    });

    $('#author-add').on('click', function (e) {
        var name = $('#author-name')[0].value;
        var uid = location.pathname.replace('/project/', '');
        apiPost("/project/author/add", {name: name, projectUid: uid}, function (data) {
            location.href = ""
        })
    });

    $('#author-name').on("keyup", function(e) {
        console.log(e);
        if (e.keyCode === 13) {
            e.preventDefault();
            $('#author-add').click();
        }
    });
});