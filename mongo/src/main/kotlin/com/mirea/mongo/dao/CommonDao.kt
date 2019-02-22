package com.mirea.mongo.dao

import com.mirea.mongo.Page
import com.mirea.mongo.Pageable
import com.mirea.mongo.entity.Persistent
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.eq
import org.litote.kmongo.find
import org.litote.kmongo.findOne
import org.litote.kmongo.util.KMongoUtil
import kotlin.reflect.KClass

abstract class CommonDao<T : Persistent>(mongoDb: MongoDatabase, clazz: KClass<T>) {
    protected val mongoCollection = mongoDb.getCollection(KMongoUtil.defaultCollectionName(clazz), clazz.java)

    fun getById(id: ObjectId) = mongoCollection.findOne(Persistent::_id eq id)

    fun findOne(vararg filter: Bson) = mongoCollection.findOne(*filter)
    fun find(vararg filter: Bson) = mongoCollection.find(*filter)

    fun insert(obj: T) = mongoCollection.insertOne(obj)
    fun insert(obj: T, opts: InsertOneOptions) = mongoCollection.insertOne(obj, opts)
    fun insertMany(obj: List<T>) = mongoCollection.insertMany(obj)
    fun insertMany(obj: List<T>, opts: InsertManyOptions) = mongoCollection.insertMany(obj, opts)

    fun updateById(id: ObjectId, update: Bson) = mongoCollection.updateOne(Persistent::_id eq id, update)
    fun updateOne(filter: Bson, update: Bson) = mongoCollection.updateOne(filter, update)
    fun updateMany(filter: Bson, update: Bson) = mongoCollection.updateMany(filter, update)

    fun replaceById(id: ObjectId, obj: T) = mongoCollection.replaceOne(Persistent::_id eq id, obj)
    fun replaceOne(filter: Bson, obj: T) = mongoCollection.replaceOne(filter, obj)

    fun page(page: Page, filter: Bson = EMPTY_BSON): Pageable<T> {
        val total = mongoCollection.countDocuments(filter)
        val content = mongoCollection.find(filter)
                .sort(page.order)
                .skip(page.page * page.size)
                .limit(page.size)
                .toList()

        return Pageable(page, total, content)
    }
}