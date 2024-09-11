package org.fako.roomapp.appDatabase

expect class DBFactory {
    fun createDatabase(): AppDatabase

}