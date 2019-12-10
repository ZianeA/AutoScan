package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.OdooService
import com.example.onmbarcode.presentation.equipment.Equipment
import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Completable
import io.reactivex.Single
import java.net.URL
import javax.inject.Inject

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

    fun update(equipmentId: Int, equipment: HashMap<*, *>): Completable {
        return odooService.authenticate()
            .flatMapCompletable { uid ->
                val client = XMLRPCClient(URL(OdooService.URL_OBJECT))
                Completable.fromAction {
                    client.call(
                        OdooService.METHOD_MAIN,
                        OdooService.DB_NAME,
                        uid,
                        OdooService.PASSWORD,
                        MODEL_EQUIPMENT_NAME,
                        OdooService.METHOD_WRITE,
                        listOf(
                            listOf(equipmentId),
                            equipment
                        )
                    )
                }
            }
    }

    companion object {
        private const val MODEL_EQUIPMENT_NAME = "actif.equipment"
    }
}