package com.mirea.common

data class WebError(val code: Int, val desc: String, val template: Pair<String, Map<String, Any?>>?) : Throwable() {
    companion object {
        fun webError(code: Int, desc: String, template: Pair<String, Map<String, Any?>>? = null): Nothing =
                throw WebError(code, desc, template)
    }
}