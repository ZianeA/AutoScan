package com.meteoalgerie.autoscan.data

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
        val path = "${storage.serverUrl}/${OdooService.PATH_BASE}/${OdooService.PATH_OBJECT}"
        return XMLRPCClient(URL(path))
    }
}