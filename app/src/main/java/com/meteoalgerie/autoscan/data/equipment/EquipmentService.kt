package com.meteoalgerie.autoscan.data.equipment

import com.meteoalgerie.autoscan.data.OdooService
import com.meteoalgerie.autoscan.data.PreferenceStorage
import dagger.Reusable
import de.timroes.axmlrpc.XMLRPCClient
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class EquipmentService @Inject constructor(
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
                MODEL_EQUIPMENT_NAME,
                OdooService.METHOD_SEARCH_READ,
                listOf(emptyList<String>())
            )
        }
            .map { it as Array<*> }
    }

    fun get(offset: Int, limit: Int): Single<Array<*>> {
        return Single.fromCallable {
            client.call(
                OdooService.METHOD_MAIN,
                OdooService.DB_NAME,
                user.id,
                user.password,
                MODEL_EQUIPMENT_NAME,
                OdooService.METHOD_SEARCH,
                listOf(
                    emptyList<String>()
                ),
                hashMapOf("offset" to offset, "limit" to limit)
            )
        }
            .flatMap {
                Single.fromCallable {
                    client.call(
                        OdooService.METHOD_MAIN,
                        OdooService.DB_NAME,
                        user.id,
                        user.password,
                        MODEL_EQUIPMENT_NAME,
                        OdooService.METHOD_READ,
                        listOf(it)
                    )
                }
            }
            .map { it as Array<*> }
    }

    fun getByDesk(deskId: Int): Single<Array<*>> {
        return Single.fromCallable {
            client.call(
                OdooService.METHOD_MAIN,
                OdooService.DB_NAME,
                user.id,
                user.password,
                MODEL_EQUIPMENT_NAME,
                OdooService.METHOD_SEARCH_READ,
                listOf(
                    listOf(
                        listOf(
                            EquipmentResponseMapper.ATTRIBUTE_CODE_AFF,
                            "=",
                            deskId
                        )
                    )
                )
            )
        }
            .map { it as Array<*> }
        /*.delay(
            Random.nextLong(3000, 5000),
            TimeUnit.MILLISECONDS
        ) //TODO remove this delay*/
    }

    fun update(equipmentId: Int, equipment: HashMap<*, *>): Completable {
        return Completable.fromAction {
            client.call(
                OdooService.METHOD_MAIN,
                OdooService.DB_NAME,
                user.id,
                user.password,
                MODEL_EQUIPMENT_NAME,
                OdooService.METHOD_WRITE,
                listOf(
                    listOf(equipmentId),
                    equipment
                )
            )
        }
        /*.delay(
            Random.nextLong(3000, 5000),
            TimeUnit.MILLISECONDS
        ) //TODO remove this delay*/
    }

    fun getEquipmentCount(): Single<Int> {
        return Single.fromCallable {
            client.call(
                OdooService.METHOD_MAIN,
                OdooService.DB_NAME,
                user.id,
                user.password,
                MODEL_EQUIPMENT_NAME,
                OdooService.METHOD_COUNT,
                listOf(emptyList<String>())
            )
        }
            .map { it as Int }
    }

    companion object {
        private const val MODEL_EQUIPMENT_NAME = "gestact.equipement"
    }
}