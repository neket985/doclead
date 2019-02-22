package com.mirea.common

import io.ktor.auth.Principal

data class UserPrincipal(
        val name: String
) : Principal