package br.com.hospitalmaps.di

import br.com.hospitalmaps.data.repository.HospitalRepository
import br.com.hospitalmaps.data.repository.LocationRepository
import br.com.hospitalmaps.domain.usecase.GetNearestHospitalUseCase
import br.com.hospitalmaps.presentation.locationpermission.viewmodel.LocationPermissionViewModel
import br.com.hospitalmaps.presentation.home.viewmodel.HomeViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val hospitalMapsModule = module {
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    single { Places.createClient(androidContext()) }
    factory { LocationRepository(locationProviderClient = get(), context = androidContext()) }
    factoryOf(::HospitalRepository)
    factoryOf(::GetNearestHospitalUseCase)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LocationPermissionViewModel)
}
