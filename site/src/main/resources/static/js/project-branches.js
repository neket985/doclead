document.addEventListener("DOMContentLoaded", function (evt) {
    cutLineHeight();

    $('.branch-item').on('click', function (e) {
        var itemNode = getParentByClass(e.target, "branch-item");
        var name = itemNode.dataset.name;
        var path = location.pathname.replace('/project/', '');
        var uid = path.substr(0, path.indexOf('/'));
        apiPost("/project/branch/checkout", {name: name, projectUid: uid}, function (data) {
            location.href = ""
        })
    });
});

function cutLineHeight() {
    $('.version-items').each(function (e) {
        var item = $('.version-items')[e];
        var child = item.children;
        if (child.length > 0) {
            var lastItemHeight = child[child.length - 1].offsetHeight;
            var line = child[0];
            line.style.height = (line.offsetHeight - lastItemHeight) + "px";
        }
    })
}

function getParentByClass(e, clazz) {
    var parent = e.parentNode;
    if (parent === null || parent === undefined) {
        return null;
    }else if(parent.classList.contains(clazz)){
        return parent;
    }else{
        return getParentByClass(parent, clazz);
    }
}