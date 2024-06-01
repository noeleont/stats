package org.thalheim.stats.cache

import app.cash.sqldelight.db.SqlDriver
interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}