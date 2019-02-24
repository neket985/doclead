package com.mirea.mongo.entity

import org.bson.types.ObjectId
import java.time.Instant

class Project(
        val title: String,
        val description: String?,
        val createdAt: Instant,
        val creator: User.UserEmbedded,
        val authors: Set<User.UserEmbedded>, // в авторов должен входить и создатель
        val accessByLink: Boolean,//показывать всем, у кого есть ссылка если true
        val accessUid: String,
//        val versions: Set<String>, todo нужен ли параметр
        override var _id: ObjectId? = null
) : Persistent