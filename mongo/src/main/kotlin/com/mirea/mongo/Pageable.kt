package com.mirea.mongo

import org.bson.conversions.Bson

class Pageable<T>(val page: Page, val total: Long, val content: List<T>) {
    val totalPages = total / page.size

    val hasNext = page.page < totalPages
    val hasPrev = page.page > 0

    val next = page.next
    val prev = page.prev
}

data class Page(val page: Int, val size: Int, val order: Bson?) {
    val next = Page(page + 1, size, order)
    val prev = Page(page - 1, size, order)
}