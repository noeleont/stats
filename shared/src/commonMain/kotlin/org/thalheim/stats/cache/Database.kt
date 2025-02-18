package org.thalheim.stats.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.thalheim.stats.entity.Entry
import org.thalheim.stats.entity.TotalEntriesPerPeriod

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    internal fun getAllEntries(): List<Entry> {
        return dbQuery.selectAllEntriesInfo(::mapEntrySelecting)
            .executeAsList()
    }

    private fun mapEntrySelecting(
        id: Long,
        dateUTC: Long
    ): Entry {
        return Entry(
            id = id,
            date = Instant.fromEpochMilliseconds(dateUTC)
        )
    }

    internal fun insertEntry(date: Instant) {
        dbQuery.insertEntry(date.toEpochMilliseconds())
    }

    //    TODO: period should be an enum
    internal fun entrySubscription(start: String, end: String, period: String): Flow<List<TotalEntriesPerPeriod>> {
        return dbQuery.getTotalEntriesPerPeriod(
            start = start,
            end = end,
            period = period,
            mapper = ::mapTotalEntriesPerPeriod
        ).asFlow().mapToList(Dispatchers.IO)
    }

    private fun mapTotalEntriesPerPeriod(
        period: String,
        totalEntries: Long
    ): TotalEntriesPerPeriod {
        return TotalEntriesPerPeriod(
            period = period,
            totalEntries = totalEntries
        )
    }
}