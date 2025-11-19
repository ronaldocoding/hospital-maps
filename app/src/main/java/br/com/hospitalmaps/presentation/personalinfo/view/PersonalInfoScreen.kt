package br.com.hospitalmaps.presentation.personalinfo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.hospitalmaps.presentation.personalinfo.action.PersonalInfoAction
import br.com.hospitalmaps.presentation.personalinfo.event.PersonalInfoEvent
import br.com.hospitalmaps.presentation.personalinfo.state.Allergy
import br.com.hospitalmaps.presentation.personalinfo.state.Disease
import br.com.hospitalmaps.presentation.personalinfo.state.Medicine
import br.com.hospitalmaps.presentation.personalinfo.state.PersonalInfoUiState
import br.com.hospitalmaps.presentation.personalinfo.viewmodel.PersonalInfoViewModel
import br.com.hospitalmaps.shared.utils.ObserveAsEvents
import br.com.hospitalmaps.ui.theme.HospitalMapsAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PersonalInfoScreen(
    onBackClick: () -> Unit = {},
    onEditMedicinesClick: () -> Unit = {},
    onEditAllergiesClick: () -> Unit = {},
    onEditDiseasesClick: () -> Unit = {}
) {
    val viewModel: PersonalInfoViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isInitialized = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (isInitialized.value.not()) viewModel.onAction(PersonalInfoAction.OnInitialized)
        isInitialized.value = true
    }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            PersonalInfoEvent.NavigateBack -> onBackClick()
            PersonalInfoEvent.NavigateToEditMedicines -> onEditMedicinesClick()
            PersonalInfoEvent.NavigateToEditAllergies -> onEditAllergiesClick()
            PersonalInfoEvent.NavigateToEditDiseases -> onEditDiseasesClick()
            is PersonalInfoEvent.ShowError -> {
                // TODO: Show error message
            }
        }
    }

    when (uiState) {
        is PersonalInfoUiState.Idle -> Unit
        is PersonalInfoUiState.Content -> {
            val model = (uiState as PersonalInfoUiState.Content).uiModel
            PersonalInfoScreenContent(
                medicines = model.medicines,
                allergies = model.allergies,
                diseases = model.diseases,
                onEditMedicinesClick = { viewModel.onAction(PersonalInfoAction.OnEditMedicinesClicked) },
                onEditAllergiesClick = { viewModel.onAction(PersonalInfoAction.OnEditAllergiesClicked) },
                onEditDiseasesClick = { viewModel.onAction(PersonalInfoAction.OnEditDiseasesClicked) },
                onBackClick = { viewModel.onAction(PersonalInfoAction.OnBackClicked) }
            )
        }

        is PersonalInfoUiState.Error -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalInfoScreenContent(
    medicines: List<Medicine>,
    allergies: List<Allergy>,
    diseases: List<Disease>,
    onEditMedicinesClick: () -> Unit,
    onEditAllergiesClick: () -> Unit,
    onEditDiseasesClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Informações Pessoais",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Medicines Section
            item {
                InfoSection(
                    title = "💊 Medicamentos",
                    description = "${medicines.size} medicamento(s)",
                    onEditClick = onEditMedicinesClick
                )
            }

            if (medicines.isNotEmpty()) {
                items(medicines) { medicine ->
                    MedicineCard(medicine = medicine)
                }
            } else {
                item {
                    EmptyStateCard(
                        text = "Nenhum medicamento registrado",
                        icon = "🔍"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Allergies Section
            item {
                InfoSection(
                    title = "⚠️ Alergias",
                    description = "${allergies.size} alergia(s)",
                    onEditClick = onEditAllergiesClick
                )
            }

            if (allergies.isNotEmpty()) {
                items(allergies) { allergy ->
                    AllergyCard(allergy = allergy)
                }
            } else {
                item {
                    EmptyStateCard(
                        text = "Nenhuma alergia registrada",
                        icon = "✓"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Diseases Section
            item {
                InfoSection(
                    title = "🏥 Doenças",
                    description = "${diseases.size} doença(s)",
                    onEditClick = onEditDiseasesClick
                )
            }

            if (diseases.isNotEmpty()) {
                items(diseases) { disease ->
                    DiseaseCard(disease = disease)
                }
            } else {
                item {
                    EmptyStateCard(
                        text = "Nenhuma doença registrada",
                        icon = "🌟"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    description: String,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = onEditClick,
            modifier = Modifier.height(40.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Editar",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun MedicineCard(medicine: Medicine) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💊",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Dose: ${medicine.dosage}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = "Frequência: ${medicine.frequency}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun AllergyCard(allergy: Allergy) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (allergy.severity) {
                "Grave" -> MaterialTheme.colorScheme.errorContainer
                "Moderada" -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.tertiaryContainer
            },
            contentColor = when (allergy.severity) {
                "Grave" -> MaterialTheme.colorScheme.onErrorContainer
                "Moderada" -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onTertiaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = when (allergy.severity) {
                            "Grave" -> MaterialTheme.colorScheme.error
                            "Moderada" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        },
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = allergy.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Severidade:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = allergy.severity,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DiseaseCard(disease: Disease) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏥",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = disease.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = when (disease.status) {
                                    "Ativo" -> MaterialTheme.colorScheme.error
                                    "Controlada" -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.tertiary
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = disease.status,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    text: String,
    icon: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun PersonalInfoScreenPreview() {
    HospitalMapsAppTheme {
        PersonalInfoScreen()
    }
}

