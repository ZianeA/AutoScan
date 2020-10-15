package com.meteoalgerie.autoscan.login

import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import com.meteoalgerie.autoscan.common.network.METHOD_AUTHENTICATE
import com.meteoalgerie.autoscan.common.network.PATH_BASE
import com.meteoalgerie.autoscan.common.network.PATH_COMMON
import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Single
import java.net.URL
import javax.inject.Inject

@Reusable
class AuthApi @Inject constructor(private val storage: PreferenceStorage) {
    fun authenticate(username: String, password: String): Single<Int> {
        return Single.fromCallable {
            val url = URL("${storage.serverUrl}/$PATH_BASE/$PATH_COMMON")
            XMLRPCClient(url).call(
                METHOD_AUTHENTICATE,
                storage.databaseName,
                username,
                password,
                emptyMap<Any, Any>()
            )
        }.flatMap {
            if (it is Int) Single.just(it)
            else Single.error { IllegalArgumentException() }
        }
    }
}