package com.meteoalgerie.autoscan.desk

import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.common.network.DB_NAME
import com.meteoalgerie.autoscan.common.network.METHOD_MAIN
import com.meteoalgerie.autoscan.common.network.METHOD_SEARCH_READ
import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class DeskApi @Inject constructor(
    private val client: XMLRPCClient,
    storage: PreferenceStorage
) {
    private val user by lazy { storage.user!! }

    fun getAll(): Single<Array<*>> {
        return Single.fromCallable {
            client.call(
                METHOD_MAIN,
                DB_NAME,
                user.id,
                user.password,
                MODEL_DESK_NAME,
                METHOD_SEARCH_READ,
                listOf(emptyList<String>())
            )
        }
            .map { it as Array<*> }
    }

    companion object {
        private const val MODEL_DESK_NAME = "gestact.bureau"
    }
}