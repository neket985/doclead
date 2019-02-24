package com.mirea.api

import com.mirea.mongo.MongoModule
import org.kodein.di.Kodein

val kodein = Kodein {
    import(MongoModule.module())
}