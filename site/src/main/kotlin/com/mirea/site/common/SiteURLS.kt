package com.mirea.site.common

object SiteURLS {
    fun homeUrl() = "/"

    fun loginUrl() = "/login"
    fun loginUrl(msgForConfirm: Boolean = false, emailConfirm: Boolean = false, loginError: Boolean = false): String {
        val queryParams = listOf(
                msgForConfirm to "msgForConfirm=true",
                emailConfirm to "emailConfirm=true",
                loginError to "loginError=true"
        ).filter { it.first }.map { it.second }.joinToString("&")
        return "/login" + if (queryParams.isNotBlank()) "?$queryParams" else ""
    }

    fun registrationUrl() = "/register"

    fun confirmEmailUrl(uuid: String) = "/register/confirm/$uuid"
}