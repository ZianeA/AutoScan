package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.OdooService
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.presentation.equipment.Equipment.*
import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@Reusable
class EquipmentService @Inject constructor(private val odooService: OdooService) {
    //TODO refactor
    fun getByDesk(deskBarcode: String): Single<Array<*>> {
        //Get user id
        return odooService.authenticate()
            .flatMap { uid ->
                val client = XMLRPCClient(URL(OdooService.URL_OBJECT))
                Single.fromCallable {
                    client.call(
                        OdooService.METHOD_MAIN,
                        OdooService.DB_NAME,
                        uid,
                        OdooService.PASSWORD,
                        MODEL_EQUIPMENT_NAME,
                        OdooService.METHOD_SEARCH_READ,
                        listOf(listOf(listOf("aff_code", "=", deskBarcode)))
                    )
                }
            }
            .map { it as Array<*> }

    }

    fun getAll(): Single<Array<*>> {
        return odooService.authenticate()
            .flatMap { uid ->
                val client = XMLRPCClient(URL(OdooService.URL_OBJECT))
                Single.fromCallable {
                    client.call(
                        OdooService.METHOD_MAIN,
                        OdooService.DB_NAME,
                        uid,
                        OdooService.PASSWORD,
                        MODEL_EQUIPMENT_NAME,
                        OdooService.METHOD_SEARCH_READ,
                        listOf(emptyList<String>())
                    )
                }
            }.map { it as Array<*> }
    }

    companion object {
        private const val MODEL_EQUIPMENT_NAME = "actif.equipment"
    }
}