package com.infinum.collar.ui.domain.shared.base

import kotlinx.coroutines.flow.Flow

internal interface BaseRepository<InputModel : BaseParameters, OutputModel> {

    suspend fun save(input: InputModel): Long = throw NotImplementedError()

    suspend fun load(input: InputModel): Flow<OutputModel> = throw NotImplementedError()

    suspend fun loadById(input: InputModel): OutputModel = throw NotImplementedError()

    suspend fun clear(): Unit = throw NotImplementedError()
}
