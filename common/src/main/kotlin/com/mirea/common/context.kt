package com.mirea.common

import com.mirea.mongo.entity.User

fun User.toPrincipal() = UserPrincipal(this.email)