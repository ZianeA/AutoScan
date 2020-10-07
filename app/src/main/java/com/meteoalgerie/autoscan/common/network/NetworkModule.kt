package com.meteoalgerie.autoscan.common.network

import com.meteoalgerie.autoscan.common.database.PreferenceStorage
import dagger.Module
import dagger.Provides
import de.timroes.axmlrpc.XMLRPCClient
import java.net.URL
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideXmlrpcClient(storage: PreferenceStorage): XMLRPCClient {
        val path = "${storage.serverUrl}/$PATH_BASE/$PATH_OBJECT"
        return XMLRPCClient(URL(path))
    }
}