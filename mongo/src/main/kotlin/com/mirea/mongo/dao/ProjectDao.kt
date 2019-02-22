package com.mirea.mongo.dao

import com.mirea.mongo.entity.Project
import com.mongodb.client.MongoDatabase

class ProjectDao(mongoDb: MongoDatabase) : CommonDao<Project>(mongoDb, Project::class) {
}