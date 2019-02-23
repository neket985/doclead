package com.mirea.mongo

import org.bson.conversions.Bson

class Pageable<T>(val page: Page, val total: Long, val content: List<T>) {
    val totalPages = total / page.size

    val hasNext = page.page < totalPages
    val hasPrev = page.page > 0

    fun next() = page.next()
    fun prev() = page.prev()
}

data class Page(val page: Int, val size: Int, val order: Bson?) {
    fun next() = Page(page + 1, size, order)
    fun prev() = Page(page - 1, size, order)
}