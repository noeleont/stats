package org.thalheim.stats.cache

import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import org.thalheim.stats.entity.Entry

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    internal fun getAllEntries(): List<Entry> {
        return dbQuery.selectAllEntriesInfo()
            .executeAsList()
            .map { mapEntrySelecting(it) }
    }

    private fun mapEntrySelecting(
        dateUTC: Long
    ): Entry {
        return Entry(
            date = Instant.fromEpochMilliseconds(dateUTC)
        )
    }

    internal fun insertEntry(entry: Entry) {
        dbQuery.insertEntry(entry.date.toEpochMilliseconds())
    }

}