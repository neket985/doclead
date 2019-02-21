package com.mirea.site

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.Loader
import com.typesafe.config.ConfigFactory
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.io.StringWriter
import java.util.*

object PebbleModule {
    private val config = ConfigFactory.load().getConfig("template")
    private val baseDir = config.getString("templDir")
    private val autoEscaping: Boolean = config.getBoolean("autoEscaping")

    fun module() = Kodein.Module("pebble") {
        bind<PebbleEngine>() with singleton {
            val cl = Thread.currentThread().contextClassLoader
            val loaderClass = cl.loadClass(config.getString("loaderClass"))
            val loader: Loader<String> = (loaderClass.newInstance() as Loader<String>).also {
                val templatePrefix: String = baseDir
                it.setPrefix(templatePrefix)
            }

            PebbleEngine.Builder()
                    .autoEscaping(autoEscaping)
                    .loader(loader)
                    .cacheActive(false)
                    .newLineTrimming(true)
                    .build()!!
        }
    }

    fun PebbleEngine.render(name: String, vararg params: Pair<String, Any?>): String {
        val template = this.getTemplate("$name.pebble")
        return StringWriter().use { writer ->
            template.evaluate(writer, params.toMap(), defLocale)
            writer.toString()
        }
    }

    private val defLocale = Locale("ru")
}