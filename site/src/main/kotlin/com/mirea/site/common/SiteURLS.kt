package com.mirea.site.common

object SiteURLS {
    fun homeUrl() = "/"

    fun loginUrl() = "/login"
    fun loginUrl(msgForConfirm: Boolean = false, emailConfirm: Boolean = false) =
            "/login?msgForConfirm=$msgForConfirm&emailConfirm=$emailConfirm"

    fun registrationUrl() = "/register"

    fun confirmEmailUrl(uuid: String) = "/register/confirm/$uuid"
}