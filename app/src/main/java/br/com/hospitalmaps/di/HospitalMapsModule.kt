package br.com.hospitalmaps.di

import br.com.hospitalmaps.BuildConfig
import br.com.hospitalmaps.data.repository.HospitalRepository
import br.com.hospitalmaps.data.repository.LocationRepository
import br.com.hospitalmaps.domain.usecase.GetNearestHospitalUseCase
import br.com.hospitalmaps.view.viewmodel.MainViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val hospitalMapsModule = module {
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    single { Places.createClient(androidContext()) }
    factory { LocationRepository(locationProviderClient = get(), context = androidContext()) }
    factory { HospitalRepository(placesClient = get(), locationRepository = get()) }
    factory { GetNearestHospitalUseCase(hospitalRepository = get()) }
    viewModel { MainViewModel(getNearestHospitalUseCase = get()) }
}

private fun providePlacesApiKey() = BuildConfig.PLACES_API_KEY
