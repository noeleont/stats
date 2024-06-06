package org.thalheim.stats

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import org.thalheim.stats.cache.Database
import org.thalheim.stats.cache.DatabaseDriverFactory
import org.thalheim.stats.entity.Entry
import org.thalheim.stats.entity.TotalEntriesPerPeriod

class StatsSDK(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = Database(databaseDriverFactory)

    @Throws(Exception::class)
    suspend fun getEntries(): List<Entry> {
        return database.getAllEntries()
    }

    @Throws(Exception::class)
    suspend fun addEntry() {
        database.insertEntry(Clock.System.now())
    }

    fun entrySubscription(period: String): Flow<List<TotalEntriesPerPeriod>> {
        val now = Clock.System.now()
        val systemZone = TimeZone.currentSystemDefault()
        val start: Instant
        val end: Instant
        when (period) {
            "day" -> {
                start = now.minus(7, DateTimeUnit.DAY, systemZone)
                end = now
            }
            "week" -> {
                start = now.minus(7, DateTimeUnit.WEEK, systemZone)
                end = now
            }
            "month" -> {
                start = now.minus(7, DateTimeUnit.MONTH, systemZone)
                end = now
            }
            else -> {
                start = now.minus(7, DateTimeUnit.DAY, systemZone)
                end = now
            }
        }
        return database.entrySubscription(start = start.toString(), end = end.toString(), period = period)
    }
}