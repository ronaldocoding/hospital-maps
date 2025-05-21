package br.com.hospitalmaps.di

import br.com.hospitalmaps.data.repository.HospitalRepository
import br.com.hospitalmaps.data.repository.UserLocationRepository
import br.com.hospitalmaps.presentation.locationpermission.viewmodel.LocationPermissionViewModel
import br.com.hospitalmaps.presentation.home.viewmodel.HomeViewModel
import br.com.hospitalmaps.presentation.main.viewmodel.MainViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val hospitalMapsModule = module {
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    single { Places.createClient(androidContext()) }
    factory { UserLocationRepository(locationProviderClient = get(), context = androidContext()) }
    factoryOf(::HospitalRepository)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LocationPermissionViewModel)
    viewModelOf(::MainViewModel)
}
