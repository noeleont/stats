package org.thalheim.stats

import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.thalheim.stats.entity.Entry
import org.koin.core.component.inject
import org.thalheim.stats.cache.IOSDatabaseDriverFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

class KoinHelper : KoinComponent {
    private val sdk: StatsSDK by inject<StatsSDK>()

    suspend fun getEntries(): List<Entry> {
        return sdk.getEntries()
    }

    suspend fun addEntry() {
        sdk.addEntry()
    }

    fun entrySubscription(): Flow<List<Entry>> {
        return sdk.entrySubscription()
    }
}

fun initKoin() {
    startKoin {
        modules(module {
            single<StatsSDK> {
                StatsSDK (
                    databaseDriverFactory = IOSDatabaseDriverFactory()
                )
            }
        })
    }
}