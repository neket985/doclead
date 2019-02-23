package com.mirea.site.pebble

import com.mitchellbosecke.pebble.extension.Filter
import com.mitchellbosecke.pebble.template.EvaluationContext
import com.mitchellbosecke.pebble.template.PebbleTemplate
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object DateTimeFilter : Filter {
    override fun getArgumentNames() = listOf("format", "offset")

    override fun apply(obj: Any, params: MutableMap<String, Any>, p2: PebbleTemplate?, p3: EvaluationContext?, p4: Int): Any {
        val datetime = when (obj) {
            is Instant -> {
                obj
            }
            is Date -> {
                obj.toInstant()
            }
            else -> return Unit
        }

        val pattern = params["format"] as String? ?: defaultFormat
        val offset = params["offset"] as Int? ?: defaultOffset

        val formatter = DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.ofOffset("", ZoneOffset.ofHours(offset)))

        return formatter.format(datetime)
    }

    private val defaultOffset = +3
    private val defaultFormat = "yyyy-MM-dd"

}