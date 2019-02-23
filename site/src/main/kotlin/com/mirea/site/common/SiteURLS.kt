package com.mirea.site.common

import com.mirea.mongo.entity.Project
import org.bson.types.ObjectId

object SiteURLS {
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

    fun homeUrl() = "/"

    fun projectAddUrl() = "/project/add"

    fun projectDetailUrl(uid: String) = "/project/$uid"
    fun projectDetailUrl(project: Project) = projectDetailUrl(project.accessUid)

    fun documentDetailUrl(uid: String) = "/project/$uid/document"
    fun documentDetailUrl(project: Project) = documentDetailUrl(project.accessUid)

    fun documentAddUrl(uid: String) = "/project/$uid/document/add"
    fun documentAddUrl(project: Project) = documentAddUrl(project.accessUid)
}