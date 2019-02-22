package com.mirea.mongo.entity

import org.bson.types.ObjectId
import java.time.Instant

class Project(
        val title: String,
        val description: String,
        val createdAt: Instant,
        val creator: User.UserEmbedded,
        val authors: List<User.UserEmbedded>, // в авторов должен входить и создатель
        val accessByLink: Boolean,//показывать всем, у кого есть ссылка если true
        val accessUid: String,
        override val _id: ObjectId? = null
) : Persistent