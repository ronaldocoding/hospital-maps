package br.com.hospitalmaps.di

import br.com.hospitalmaps.BuildConfig
import br.com.hospitalmaps.data.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val hospitalMapsModule = module {
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    single { Places.initializeWithNewPlacesApiEnabled(androidContext(), providePlacesApiKey()) }
    single { Places.createClient(androidContext()) }
    factory { LocationRepository(locationProviderClient = get(), context = androidContext()) }
}

private fun providePlacesApiKey() = BuildConfig.PLACES_API_KEY
