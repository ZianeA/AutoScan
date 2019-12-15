package com.example.onmbarcode.data.desk

import com.example.onmbarcode.data.OdooService
import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.desk.Desk
import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Single
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@Reusable
class DeskService @Inject constructor(private val odooService: OdooService) {
    fun getAll(): Single<Array<*>> {
        return odooService.authenticate()
            .toSingle()
            .flatMap { uid ->
                val client = XMLRPCClient(URL(OdooService.URL_OBJECT))
                Single.fromCallable {
                    client.call(
                        OdooService.METHOD_MAIN,
                        OdooService.DB_NAME,
                        uid,
                        OdooService.PASSWORD,
                        MODEL_DESK_NAME,
                        OdooService.METHOD_SEARCH_READ,
                        listOf(emptyList<String>())
                    )
                }
            }.map { it as Array<*> }
    }

    companion object {
        private const val MODEL_DESK_NAME = "actif.bureau"
    }
}