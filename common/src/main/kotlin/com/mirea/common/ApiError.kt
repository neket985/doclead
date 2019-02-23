package com.mirea.common

data class ApiError(val code: Int, val desc: String) : Throwable() {
    companion object {
        fun apiError(code: Int, desc: String): Nothing =
                throw ApiError(code, desc)
    }
}