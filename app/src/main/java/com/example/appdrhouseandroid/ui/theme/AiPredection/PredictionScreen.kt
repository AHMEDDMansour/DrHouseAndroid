package com.example.appdrhouseandroid.ui.theme.AiPredection

import android.graphics.Matrix.ScaleToFit
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appdrhouseandroid.data.network.PredictionResponse
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PredictionScreen(viewModel: PredictionViewModel) {
    var symptomsInput by remember { mutableStateOf("") }
    val predictionResponse by viewModel.predictionResponse.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    // Pulsing animation for the AI brain
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Sci-fi background gradient
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF121212),
            Color(0xFF1E1E1E),
            Color(0xFF2C3E50)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated AI Brain Header
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4A90E2),
                                Color(0xFF50C878)
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        color = Color(0xFF34495E),
                        shape = CircleShape
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Science,
                    contentDescription = "AI Brain",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Text(
                "Health AI",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            // Symptom Input with Sci-Fi Design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF2C3E50).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF34495E),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                TextField(
                    value = symptomsInput,
                    onValueChange = { symptomsInput = it },
                    placeholder = {
                        Text(
                            "Enter symptoms: headache, fever...",
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color(0xFF4A90E2)
                    )
                )
            }

            // Analyze Button with Futuristic Design
            Button(
                onClick = {
                    val symptoms = symptomsInput.split(",").map { it.trim() }
                    if (symptoms.isNotEmpty()) {
                        viewModel.fetchPrediction(symptoms)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        shape = RoundedCornerShape(12.dp)
                        clip = true
                    }
            ) {
                Text("Analyze Symptoms", color = Color.White)
            }

            // Animated Loading and Results
            AnimatedContent(
                targetState = isLoading to predictionResponse,
                transitionSpec = {
                    fadeIn() with fadeOut()
                }
            ) { (loading, response) ->
                when {
                    loading -> LoadingAnimation()
                    response != null -> PredictionResults(response)
                    else -> EmptyState()
                }
            }
        }
    }
}

@Composable
fun LoadingAnimation() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFF4A90E2),
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Analyzing Symptoms...",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun PredictionResults(response: PredictionResponse) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF2C3E50).copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Comprehensive Health Insights",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            ResultItem(
                icon = Icons.Filled.LocalHospital,
                label = "Predicted Disease",
                value = response.predicted_disease ?: "No specific disease detected"
            )
            ResultItem(
                icon = Icons.Filled.Biotech,
                label = "Medications",
                value = response.medications?.joinToString(", ") ?: "No specific medications"
            )
            ResultItem(
                icon = Icons.Filled.Science,
                label = "Precautions",
                value = response.precautions ?: "No specific precautions"
            )
            ResultItem(
                icon = Icons.Filled.Visibility,
                label = "Recommended Diet",
                value = response.recommended_diet?.joinToString(", ") ?: "No specific diet recommendations"
            )
            ResultItem(
                icon = Icons.Filled.Science,
                label = "Workout Suggestions",
                value = response.workout?.joinToString(", ") ?: "No specific workout recommendations"
            )
        }
    }
}

@Composable
fun ResultItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = Color(0xFF34495E).copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF4A90E2),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                label,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                value,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Text(
            "Ready to analyze your symptoms",
            color = Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}