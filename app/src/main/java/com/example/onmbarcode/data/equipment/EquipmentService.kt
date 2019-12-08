package com.example.onmbarcode.data.equipment

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
class EquipmentService @Inject constructor() {
    //TODO refactor
    fun getByDesk(deskBarcode: String): Single<List<Equipment>> {
        //Get user id
        return Single.fromCallable {
            val client = XMLRPCClient(URL(URL_COMMON))
            client.call(
                "authenticate",
                DB_NAME,
                "admin",
                "admin",
                emptyMap<Any, Any>()
            ) as Int
        }.flatMap { uid ->
            val client = XMLRPCClient(URL(URL_OBJECT))
            Single.fromCallable {
                client.call(
                    "execute_kw",
                    DB_NAME,
                    uid,
                    "admin",
                    "actif.equipment",
                    "search_read",
                    listOf(listOf(listOf("aff_code", "=", deskBarcode)))
                )
            }
        }
            //TODO move the mapping to mapper
            .map { equipments ->
                (equipments as Array<*>).map {
                    val fieldsMap = it as HashMap<*, *>
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val scanDate = dateFormat.parse((fieldsMap["date_de_scan"] as String))!!
                        .time
                        .div(SECOND_IN_MILLIS)

                    Equipment(
                        fieldsMap["code"] as String,
                        fieldsMap["libelle"] as String,
                        ScanState.NotScanned, //TODO deal with this
                        EquipmentCondition.getByTranslation(fieldsMap["observation"] as String),
                        scanDate,
                        deskBarcode
                    )
                }
            }

    }

    companion object {
        private const val URL_BASE = "http://10.0.2.2:8069/xmlrpc/2"
        private const val URL_COMMON = "$URL_BASE/common"
        private const val URL_OBJECT = "$URL_BASE/object"
        private const val DB_NAME = "ali"
        private const val SECOND_IN_MILLIS = 1000
    }
}