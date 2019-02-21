document.addEventListener("DOMContentLoaded", function (evt) {
    $('.register').on('click', function (e) {
       window.location = '/register';
    });
    $('.login').on('click', function (e) {
       window.location = '/login';
    });
});