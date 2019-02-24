package com.mirea.mongo.dao

import com.mirea.mongo.entity.User
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.or

class UserDao(mongoDb: MongoDatabase) : CommonDao<User>(mongoDb, User::class) {
    fun getConfirmedByEmailOrName(str: String) =
            findOne(
                    or(User::email eq str, User::name eq str),
                    User::confirmed eq true
            )

    fun getConfirmedByEmail(email: String) = findOne(User::email eq email, User::confirmed eq true)
    fun getConfirmedByName(name: String) = findOne(User::name eq name, User::confirmed eq true)

    fun getByEmail(email: String) = findOne(User::email eq email)
    fun getByName(name: String) = findOne(User::name eq name)
}