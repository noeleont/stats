package org.thalheim.stats.entity

import kotlinx.datetime.Instant

data class Entry (
    val id: Long,
    val date: Instant,
) { }