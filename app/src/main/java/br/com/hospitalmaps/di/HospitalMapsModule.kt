package br.com.hospitalmaps.di

import br.com.hospitalmaps.data.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val hospitalMapsModule = module {
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    factory { LocationRepository(locationProviderClient = get(), context = androidContext()) }
}
