package org.fako.roomapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform