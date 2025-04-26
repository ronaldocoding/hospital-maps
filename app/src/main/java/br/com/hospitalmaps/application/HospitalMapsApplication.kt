package br.com.hospitalmaps.application

import android.app.Application
import br.com.hospitalmaps.di.hospitalMapsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HospitalMapsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@HospitalMapsApplication)
            modules(hospitalMapsModule)
        }
    }
}