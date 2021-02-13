package com.infinum.collar.ui.domain.entities

import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.sources.local.dao.EntitiesDao
import com.infinum.collar.ui.domain.Repositories
import com.infinum.collar.ui.domain.entities.models.EntityParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class EntityRepository(
    private val dao: EntitiesDao
) : Repositories.Entity {

    override suspend fun save(input: EntityParameters) =
        input.entity?.let { dao.save(it) }
            ?: throw IllegalStateException("Cannot save null entity")

    override suspend fun load(input: EntityParameters): Flow<List<CollarEntity>> =
        with(input) {
            when {
                query.isNullOrBlank().not() && filters.isNotEmpty() ->
                    dao.load(query = query!!, filters = filters)
                query.isNullOrBlank() && filters.isNotEmpty() ->
                    dao.load(filters = filters)
                query.isNullOrBlank().not() && filters.isEmpty() ->
                    dao.load(query = query!!)
                query.isNullOrBlank() && filters.isEmpty() ->
                    flowOf(listOf())
                else -> dao.load()
            }
        }

    override suspend fun clear() =
        dao.delete()
}
