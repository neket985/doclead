package com.mirea.site

import io.ktor.auth.Principal

data class UserPrincipal(
        val name: String
) : Principal