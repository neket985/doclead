package com.mirea.site.common

import com.mirea.mongo.entity.Document
import com.mirea.mongo.entity.Project

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

    fun projectDetailUrl(project: Project) = projectDetailUrl(project.accessUid)
    fun projectDetailUrl(uid: String) = "/project/$uid"

    fun documentDetailUrl(uid: String, branch: String) = "/project/$uid/document/v$branch"
    fun documentDetailUrl(project: Project) = documentDetailUrl(project.accessUid, project.currentBranch)
    fun documentDetailUrl(project: Project, document: Document?) =
            documentDetailUrl(project.accessUid, document?.branch ?: project.currentBranch)


    fun documentHtmlUrl(uid: String, branch: String) = "/project/$uid/document/v$branch/html"
    fun documentHtmlUrl(project: Project) = documentHtmlUrl(project.accessUid, project.currentBranch)
    fun documentHtmlUrl(project: Project, document: Document?) =
            documentHtmlUrl(project.accessUid, document?.branch ?: project.currentBranch)


    fun documentAddUrl(uid: String, branch: String) = "/project/$uid/document/v$branch/add"
    fun documentAddUrl(project: Project) = documentAddUrl(project.accessUid, project.currentBranch)
    fun documentAddUrl(project: Project, document: Document?) =
            documentAddUrl(project.accessUid, document?.branch ?: project.currentBranch)


    fun documentDownloadUrl(uid: String, branch: String) = "/project/$uid/document/v$branch/download"
    fun documentDownloadUrl(project: Project) = documentDownloadUrl(project.accessUid, project.currentBranch)
    fun documentDownloadUrl(project: Project, document: Document?) =
            documentDownloadUrl(project.accessUid, document?.branch ?: project.currentBranch)

}