package co.infinum.collar.ui.domain.entities

import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.sources.local.DatabaseProvider
import co.infinum.collar.ui.domain.Repositories
import co.infinum.collar.ui.domain.entities.models.EntityParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class EntityRepository(
    private val database: DatabaseProvider
) : Repositories.Entity {

    override suspend fun save(input: EntityParameters) =
        input.entity?.let { database.collar().entities().save(it) }
            ?: throw IllegalStateException("Cannot save null entity")

    override suspend fun load(input: EntityParameters): Flow<List<CollarEntity>> =
        with(input) {
            when {
                query.isNullOrBlank().not() && filters.isNotEmpty() ->
                    database.collar().entities().load(query = query!!, filters = filters)
                query.isNullOrBlank() && filters.isNotEmpty() ->
                    database.collar().entities().load(filters = filters)
                query.isNullOrBlank().not() && filters.isEmpty() ->
                    database.collar().entities().load(query = query!!)
                query.isNullOrBlank() && filters.isEmpty() ->
                    flowOf(listOf())
                else -> database.collar().entities().load()
            }
        }

    override suspend fun clear() =
        database.collar().entities().delete()
}
