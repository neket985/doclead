document.addEventListener("DOMContentLoaded", function (evt) {
    $('#doc-toggle').on('change', function (e) {
        setTimeout(function () {
            var labelForAtivate;
            var labelForDeative;
            if (e.target.checked) {
                labelForAtivate = $('.label-for-checked')[0];
                labelForDeative = $('.label-for-unchecked')[0];
            } else {
                labelForAtivate = $('.label-for-unchecked')[0];
                labelForDeative = $('.label-for-checked')[0];
            }

            if (labelForAtivate.classList.contains("inactive")) {
                labelForAtivate.classList.remove("inactive")
            }
            if (!labelForDeative.classList.contains("inactive")) {
                labelForDeative.classList.add("inactive")
            }
        }, 400);
    })
});