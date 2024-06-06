package org.thalheim.stats.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import org.thalheim.stats.entity.Entry

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

    internal fun entrySubscription(): Flow<List<Entry>> {
        return dbQuery.selectAllEntriesInfo(::mapEntrySelecting).asFlow().mapToList(Dispatchers.IO)
    }
}