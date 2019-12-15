package com.example.onmbarcode.data

import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Maybe
import io.reactivex.Single
import java.net.URL
import java.util.*
import javax.inject.Inject

//TODO should authenticate only once by storing the UID
//TODO remove hardcoded password and username
@Reusable
class OdooService @Inject constructor() {
    fun authenticate(username: String = USERNAME, password: String = PASSWORD): Maybe<Int> {
        return Maybe.fromCallable {
            val client = XMLRPCClient(URL(URL_COMMON))
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

    companion object {
        private const val URL_BASE = "http://192.168.4.94:8069/xmlrpc/2"
        const val URL_COMMON = "$URL_BASE/common"
        const val URL_OBJECT = "$URL_BASE/object"
        const val METHOD_MAIN = "execute_kw"
        const val METHOD_READ = "read"
        const val METHOD_SEARCH = "search"
        const val METHOD_SEARCH_READ = "search_read"
        const val METHOD_WRITE = "write"
        const val DB_NAME = "ali"
        const val PASSWORD = "admin"
        private const val USERNAME = "admin"
        private const val METHOD_AUTHENTICATE = "authenticate"
    }
}