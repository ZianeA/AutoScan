package com.meteoalgerie.autoscan.data

import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Maybe
import io.reactivex.Single
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Reusable
class OdooService @Inject constructor(private val storage: PreferenceStorage) {
    fun authenticate(username: String, password: String): Single<Int> {
        return Single.fromCallable {
            val url = URL("${storage.serverUrl}/$PATH_BASE/$PATH_COMMON")
            XMLRPCClient(url).call(
                METHOD_AUTHENTICATE,
                DB_NAME,
                username,
                password,
                emptyMap<Any, Any>()
            )
        }.flatMap {
            if (it is Int) Single.just(it)
            else Single.error { IllegalArgumentException() }
        }
    }

    companion object {
        const val PATH_BASE = "xmlrpc/2"
        const val PATH_COMMON = "common"
        const val PATH_OBJECT = "object"
        const val METHOD_MAIN = "execute_kw"
        const val METHOD_READ = "read"
        const val METHOD_SEARCH = "search"
        const val METHOD_COUNT = "search_count"
        const val METHOD_SEARCH_READ = "search_read"
        const val METHOD_WRITE = "write"
        const val DB_NAME = "mydatabase"
        private const val METHOD_AUTHENTICATE = "authenticate"
    }
}