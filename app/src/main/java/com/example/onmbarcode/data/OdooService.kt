package com.example.onmbarcode.data

import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Single
import java.net.URL
import javax.inject.Inject

@Reusable
class OdooService @Inject constructor() {
    fun authenticate(): Single<Int> {
        return Single.fromCallable {
            val client = XMLRPCClient(URL(URL_COMMON))
            client.call(
                METHOD_AUTHENTICATE,
                DB_NAME,
                USERNAME,
                PASSWORD,
                emptyMap<Any, Any>()
            ) as Int
        }
    }

    companion object {
        private const val URL_BASE = "http://10.0.2.2:8069/xmlrpc/2"
        const val URL_COMMON = "$URL_BASE/common"
        const val URL_OBJECT = "$URL_BASE/object"
        const val METHOD_MAIN = "execute_kw"
        const val METHOD_READ = "read"
        const val METHOD_SEARCH = "search"
        const val METHOD_SEARCH_READ = "search_read"
        const val DB_NAME = "ali"
        const val PASSWORD = "admin"
        private const val USERNAME = "admin"
        private const val METHOD_AUTHENTICATE = "authenticate"
    }
}