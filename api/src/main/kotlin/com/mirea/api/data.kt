package com.mirea.api

data class AuthorAddRemove(
        val name: String,
        val projectUid: String
)
data class ProjectUid(
        val uid: String
)