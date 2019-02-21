package com.mirea.doclead.common

import com.mirea.doclead.PebbleModule
import org.kodein.di.Kodein

val kodein = Kodein {
    import(PebbleModule.module())
}