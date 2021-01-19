package co.infinum.collar.ui.data.sources.local

internal interface DatabaseProvider {

    fun collar(): CollarDatabase
}
