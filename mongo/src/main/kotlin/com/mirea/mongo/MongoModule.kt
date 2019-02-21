package com.mirea.mongo

import com.mirea.mongo.dao.UserDao
import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import com.typesafe.config.ConfigFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.litote.kmongo.KMongo

object MongoModule {
    private val config = ConfigFactory.load().getConfig("mongo")

    private val databaseName = config.getString("dbName")
    private val host = config.getString("host")
    private val port = config.getInt("port")
    private val creds = config.getConfigList("credentials").map {
        MongoCredential.createCredential(it.getString("username"), databaseName, it.getString("password").toCharArray())
    }

    private val client = KMongo.createClient(ServerAddress(host, port), creds)
    private val database = client.getDatabase(databaseName)

    fun module() = Kodein.Module("mongo") {
        bind<MongoClient>() with singleton { client }
        bind<MongoDatabase>() with singleton { database }

        bind<UserDao>() with singleton { UserDao(instance()) }
    }
}