package com.mirea.mongo.dao

import com.mirea.mongo.entity.Project
import com.mirea.mongo.entity.User
import com.mongodb.client.MongoDatabase
import org.bson.types.ObjectId
import org.litote.kmongo.contains
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.litote.kmongo.or

class ProjectDao(mongoDb: MongoDatabase) : CommonDao<Project>(mongoDb, Project::class) {
    fun getByUid(uid: String, user: User.UserEmbedded?) = // доступ к проекту по uid только если установлена галочка о доступе всем, либо если пользователь находится среди авторов
            findOne(
                    Project::accessUid eq uid,
                    or(
                            Project::accessByLink eq true,
                            Project::authors contains user
                    )
            )
}