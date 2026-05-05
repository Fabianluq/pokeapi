package com.example.pokeapi.ui.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokeapi.domain.model.PokemonDetail
import com.example.pokeapi.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Pick hero color from first type, fallback to PokeRed
    val heroColor = uiState.detail?.types?.firstOrNull()
        ?.let { typeColor(it) } ?: PokeRed

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PokeBackground)
    ) {
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PokeRed, strokeWidth = 3.dp)
                }
            }

            uiState.errorMessage != null -> {
                ErrorState(
                    message = uiState.errorMessage!!,
                    isOnline = uiState.isOnline,
                    onBack = onNavigateBack
                )
            }

            uiState.detail != null -> {
                DetailContent(
                    detail = uiState.detail!!,
                    heroColor = heroColor,
                    isOnline = uiState.isOnline,
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main content
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DetailContent(
    detail: PokemonDetail,
    heroColor: Color,
    isOnline: Boolean,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Hero Section ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            heroColor.copy(alpha = 0.9f),
                            heroColor.copy(alpha = 0.6f),
                            PokeBackground
                        )
                    )
                )
        ) {
            // Pokéball watermark
            Text(
                text = "◎",
                fontSize = 220.sp,
                color = Color.White.copy(alpha = 0.06f),
                modifier = Modifier.align(Alignment.CenterEnd).offset(x = 40.dp)
            )

            // Back button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .padding(top = 48.dp, start = 12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .size(44.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            // Offline badge
            if (!isOnline) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 52.dp, end = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xDD1A0A00))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📡", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Offline", color = PokeYellow, style = MaterialTheme.typography.labelSmall)
                }
            }

            // Sprite
            AsyncImage(
                model = detail.imageUrl,
                contentDescription = detail.name,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 32.dp),
                contentScale = ContentScale.Fit
            )
        }

        // ── Info Cards ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Number + Name
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "#${detail.id.toString().padStart(3, '0')}",
                    style = MaterialTheme.typography.labelLarge.copy(color = heroColor, letterSpacing = 2.sp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = detail.name.capitalize(Locale.current),
                    style = MaterialTheme.typography.headlineLarge.copy(color = TextPrimary),
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Types Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                detail.types.forEach { type ->
                    val tc = typeColor(type)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(tc.copy(alpha = 0.2f))
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = type.capitalize(Locale.current),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = tc,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Stats Card
            InfoCard(title = "Medidas") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBadge(
                        icon = "📏",
                        value = "${detail.height / 10f} m",
                        label = "Altura"
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(TextHint.copy(alpha = 0.3f))
                    )
                    StatBadge(
                        icon = "⚖️",
                        value = "${detail.weight / 10f} kg",
                        label = "Peso"
                    )
                }
            }

            // Pokémon ID Card
            InfoCard(title = "Info") {
                DetailRow(label = "N.º Pokédex", value = "#${detail.id}")
                Spacer(modifier = Modifier.height(6.dp))
                DetailRow(
                    label = "Tipos",
                    value = detail.types.joinToString(" / ") {
                        it.capitalize(Locale.current)
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable sub-composables
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(PokeSurface)
            .padding(20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextSecondary,
                letterSpacing = 1.sp
            ),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun StatBadge(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Error State
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ErrorState(message: String, isOnline: Boolean, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚠️", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (!isOnline) "Sin conexión a internet" else "Error al cargar",
            style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = PokeRed)
        ) {
            Text("Volver")
        }
    }
}
