package br.com.hospitalmaps.presentation.personalinfo.state

data class Medicine(
    val id: String,
    val name: String,
    val dosage: String,
    val frequency: String,
)

data class Allergy(
    val id: String,
    val name: String,
    val severity: String, // Leve, Moderada, Grave
)

data class Disease(
    val id: String,
    val name: String,
    val status: String, // Ativo, Controlado, Curado
)

data class PersonalInfoUiModel(
    val medicines: List<Medicine> = emptyList(),
    val allergies: List<Allergy> = emptyList(),
    val diseases: List<Disease> = emptyList(),
    val isLoading: Boolean = false,
)

sealed class PersonalInfoUiState {
    data object Idle : PersonalInfoUiState()

    data class Content(val uiModel: PersonalInfoUiModel) : PersonalInfoUiState()

    data object Error : PersonalInfoUiState()
}

// Fake data
fun getFakePersonalInfo(): PersonalInfoUiModel {
    return PersonalInfoUiModel(
        medicines = listOf(
            Medicine(
                id = "1",
                name = "Aspirina",
                dosage = "500mg",
                frequency = "2x ao dia"
            ),
            Medicine(
                id = "2",
                name = "Ibuprofeno",
                dosage = "400mg",
                frequency = "A cada 8 horas"
            ),
            Medicine(
                id = "3",
                name = "Paracetamol",
                dosage = "750mg",
                frequency = "3x ao dia"
            ),
            Medicine(
                id = "4",
                name = "Metformina",
                dosage = "850mg",
                frequency = "2x ao dia"
            ),
        ),
        allergies = listOf(
            Allergy(
                id = "1",
                name = "Penicilina",
                severity = "Grave"
            ),
            Allergy(
                id = "2",
                name = "Amendoim",
                severity = "Moderada"
            ),
            Allergy(
                id = "3",
                name = "Frutos do mar",
                severity = "Leve"
            ),
        ),
        diseases = listOf(
            Disease(
                id = "1",
                name = "Hipertensão",
                status = "Controlada"
            ),
            Disease(
                id = "2",
                name = "Diabetes tipo 2",
                status = "Ativo"
            ),
            Disease(
                id = "3",
                name = "Asma",
                status = "Controlada"
            ),
        )
    )
}

