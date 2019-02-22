package com.mirea.mongo.dao

import com.mirea.mongo.entity.Document
import com.mongodb.client.MongoDatabase

class DocumentDao(mongoDb: MongoDatabase) : CommonDao<Document>(mongoDb, Document::class) {
}