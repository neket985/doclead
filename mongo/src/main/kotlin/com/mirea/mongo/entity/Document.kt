package com.mirea.mongo.entity

import org.bson.types.ObjectId
import java.time.Instant

class Document(
        val project: ObjectId,
        val createdAt: Instant,
        val author: User.UserEmbedded,
        val version: String,
        val description: String?, //описание изменений/создания для удобства при просмотре истории изменений
        val filename: String, //местоположение документа в файловой системе сервера (относительно директории, указанной в конфиге)
        override var _id: ObjectId? = null
) : Persistent