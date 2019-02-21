package com.mirea.doclead

import io.ktor.auth.Principal

data class UserPrincipal(
        val name: String
) : Principal