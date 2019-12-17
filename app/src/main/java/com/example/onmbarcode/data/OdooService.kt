package com.example.onmbarcode.data

import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Maybe
import java.net.URL
import javax.inject.Inject

@Reusable
class OdooService @Inject constructor(private val urlStore: KeyValueStore<String>) {
    fun authenticate(username: String, password: String): Maybe<Int> {
        return Maybe.fromCallable {
            val client = XMLRPCClient(URL(commonUrl))
            client.call(
                METHOD_AUTHENTICATE,
                DB_NAME,
                username,
                password,
                emptyMap<Any, Any>()
            )
        }.flatMap {
            if (it is Int) Maybe.just(it)
            else Maybe.empty<Int>()
        }
    }

    val baseUrl: String
        get() {
            val serverUrl =
                urlStore.get(PreferencesStringStore.SERVER_URL_KEY, URL_SERVER_DEFAULT)
            return "$serverUrl/$PATH_BASE"
        }

    val commonUrl: String get() = "$baseUrl/$PATH_COMMON"
    val objectUrl: String get() = "$baseUrl/$PATH_OBJECT"

    companion object {
        const val URL_SERVER_DEFAULT = "http://192.168.4.94:8069"
        private const val PATH_BASE = "xmlrpc/2"
        private const val PATH_COMMON = "common"
        private const val PATH_OBJECT = "object"
        const val METHOD_MAIN = "execute_kw"
        const val METHOD_READ = "read"
        const val METHOD_SEARCH = "search"
        const val METHOD_SEARCH_READ = "search_read"
        const val METHOD_WRITE = "write"
        const val DB_NAME = "ali"
        private const val METHOD_AUTHENTICATE = "authenticate"
    }
}