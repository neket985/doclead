package com.mirea.mongo.dao

import com.mirea.mongo.entity.Document
import com.mirea.mongo.entity.Project
import com.mongodb.client.MongoDatabase
import org.bson.types.ObjectId
import org.litote.kmongo.descending
import org.litote.kmongo.eq

class DocumentDao(mongoDb: MongoDatabase) : CommonDao<Document>(mongoDb, Document::class) {
    fun getNewest(projectId: ObjectId) =
            find(Document::project eq projectId)
                    .sort(descending(Document::createdAt))
                    .limit(1)
                    .firstOrNull()

    fun getByBranch(projectId: ObjectId, branch: String) =
            find(Document::project eq projectId, Document::branch eq branch)
                    .sort(descending(Document::createdAt))
                    .limit(1)
                    .firstOrNull()
}