package com.mirea.site.common

import com.mirea.site.pebble.PebbleModule
import com.mirea.mongo.MongoModule
import org.kodein.di.Kodein

val kodein = Kodein {
    import(PebbleModule.module())
    import(MongoModule.module())
}