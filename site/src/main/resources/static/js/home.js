document.addEventListener("DOMContentLoaded", function (evt) {
    $('.project-item-trash').on('click', function (e) {
        e.stopPropagation();
        var item = getParentByClass(e.target, "project-item");
        var uid = item.dataset.id;
        console.log(item);
        apiPost("/project/remove", {uid: uid}, function (data) {
            location.href = ""
        })
    });
});