package com.meteoalgerie.autoscan.data.desk

import com.meteoalgerie.autoscan.data.OdooService
import com.meteoalgerie.autoscan.data.PreferenceStorage
import com.meteoalgerie.autoscan.data.user.User
import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Single
import java.net.URL
import javax.inject.Inject

@Reusable
class DeskService @Inject constructor(
    private val client: XMLRPCClient,
    storage: PreferenceStorage
) {
    private val user by lazy { storage.user!! }

    fun getAll(): Single<Array<*>> {
        return Single.fromCallable {
            client.call(
                OdooService.METHOD_MAIN,
                OdooService.DB_NAME,
                user.id,
                user.password,
                MODEL_DESK_NAME,
                OdooService.METHOD_SEARCH_READ,
                listOf(emptyList<String>())
            )
        }
            .map { it as Array<*> }
    }

    companion object {
        private const val MODEL_DESK_NAME = "gestact.bureau"
    }
}