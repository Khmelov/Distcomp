package com.example.distcomp.cache

object CacheKeys {
    fun page(page: Int, size: Int, sort: Array<String>): String =
        buildString {
            append(page)
            append(':')
            append(size)
            append(':')
            append(sort.joinToString(","))
        }
}
