package co.infinum.generator.dependencies

import com.squareup.moshi.Moshi

class MoshiModule {

    companion object {

        fun getMoshi() : Moshi = Moshi.Builder().build()
    }
}