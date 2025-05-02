package br.com.hospitalmaps.application

import android.app.Application
import br.com.hospitalmaps.BuildConfig
import br.com.hospitalmaps.di.hospitalMapsModule
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HospitalMapsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initializeWithNewPlacesApiEnabled(applicationContext, BuildConfig.MAPS_PLATFORM_API_KEY)
        startKoin {
            androidLogger()
            androidContext(this@HospitalMapsApplication)
            modules(hospitalMapsModule)
        }
    }
}