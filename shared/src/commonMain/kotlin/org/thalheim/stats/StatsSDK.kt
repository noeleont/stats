package org.thalheim.stats

import kotlinx.datetime.Clock
import org.thalheim.stats.cache.Database
import org.thalheim.stats.cache.DatabaseDriverFactory
import org.thalheim.stats.entity.Entry

class StatsSDK(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = Database(databaseDriverFactory)

    @Throws(Exception::class)
    suspend fun getEntries() {
        database.getAllEntries()
    }

    @Throws(Exception::class)
    suspend fun addEntry() {
        val entry = Entry(date = Clock.System.now())
        database.insertEntry(entry)
    }
}