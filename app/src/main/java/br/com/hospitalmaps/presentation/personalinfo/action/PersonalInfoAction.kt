package br.com.hospitalmaps.presentation.personalinfo.action

sealed class PersonalInfoAction {
    data object OnInitialized : PersonalInfoAction()
    data object OnEditMedicinesClicked : PersonalInfoAction()
    data object OnEditAllergiesClicked : PersonalInfoAction()
    data object OnEditDiseasesClicked : PersonalInfoAction()
    data object OnBackClicked : PersonalInfoAction()
}

