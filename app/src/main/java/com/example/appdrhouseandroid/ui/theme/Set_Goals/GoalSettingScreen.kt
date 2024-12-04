package com.example.appdrhouseandroid.ui.theme.Set_Goals

import android.content.Context
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.appdrhouseandroid.data.network.AddGoalDto

data class HealthRecommendation(
    val min: Float,
    val max: Float,
    val warningMessage: String,
    val recommendationMessage: String
)

@Composable
fun GoalSettingScreen(onGoalsSaved: () -> Unit,viewModel: GoalSettingScreenViewModel) {
    var stepsGoal by remember { mutableStateOf(10000f) }
    var waterGoal by remember { mutableStateOf(2.5f) }
    var sleepGoal by remember { mutableStateOf(8f) }
    var coffeeGoal by remember { mutableStateOf(3f) }
    var workoutGoal by remember { mutableStateOf(30f) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("USER_ID", null)
    Log.d("GoalSettingScreen", "User ID: $userId")
    if (userId == null) {
        Text("User not logged in!")
        return
    }
    // Health recommendations for each goal
    val recommendations = mapOf(
        "water" to HealthRecommendation(
            min = 2f,
            max = 4f,
            warningMessage = "Drinking too little or too much water can be harmful",
            recommendationMessage = "Recommended: 2-4 liters per day"
        ),
        "sleep" to HealthRecommendation(
            min = 7f,
            max = 9f,
            warningMessage = "Inadequate or excessive sleep can affect your health",
            recommendationMessage = "Recommended: 7-9 hours for adults"
        ),
        "coffee" to HealthRecommendation(
            min = 0f,
            max = 4f,
            warningMessage = "High caffeine intake can cause anxiety and sleep issues",
            recommendationMessage = "Recommended: Up to 4 cups per day"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set Your Daily Goals",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier.padding(vertical = 24.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            HealthAwareGoalCard(
                title = "Water Intake",
                icon = Icons.Default.WaterDrop,
                value = waterGoal,
                valueRange = 0.5f..5f,
                unit = "liters",
                onValueChange = { waterGoal = it },
                steps = 9,
                formatValue = { String.format("%.1f", it) },
                recommendation = recommendations["water"]!!,
                color = MaterialTheme.colorScheme.secondaryContainer
            )

            HealthAwareGoalCard(
                title = "Sleep Duration",
                icon = Icons.Default.Bedtime,
                value = sleepGoal,
                valueRange = 4f..12f,
                unit = "hours",
                onValueChange = { sleepGoal = it },
                steps = 16,
                formatValue = { String.format("%.1f", it) },
                recommendation = recommendations["sleep"]!!,
                color = MaterialTheme.colorScheme.tertiaryContainer
            )

            HealthAwareGoalCard(
                title = "Coffee Limit",
                icon = Icons.Default.Coffee,
                value = coffeeGoal,
                valueRange = 0f..10f,
                unit = "cups",
                onValueChange = { coffeeGoal = it },
                steps = 10,
                formatValue = { it.roundToInt().toString() },
                recommendation = recommendations["coffee"]!!,
                color = MaterialTheme.colorScheme.secondaryContainer
            )

            // Regular goal cards for steps and workout
            GoalCard(
                title = "Daily Steps",
                icon = Icons.Default.DirectionsWalk,
                value = stepsGoal,
                valueRange = 1000f..20000f,
                unit = "steps",
                onValueChange = { stepsGoal = it },
                steps = 19,
                formatValue = { it.roundToInt().toString() },
                color = MaterialTheme.colorScheme.primaryContainer
            )

            GoalCard(
                title = "Workout Duration",
                icon = Icons.Default.FitnessCenter,
                value = workoutGoal,
                valueRange = 10f..120f,
                unit = "minutes",
                onValueChange = { workoutGoal = it },
                steps = 22,
                formatValue = { it.roundToInt().toString() },
                color = MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
    onClick = {
        if (userId != null) {
            val addGoalRequest = AddGoalDto(
                steps = stepsGoal.toInt(),
                water = waterGoal.toInt(),
                sleepHours = sleepGoal.toInt(),
                coffeeCups = coffeeGoal.toInt(),
                workout = workoutGoal.toInt()
            )
            viewModel.addGoal(userId, addGoalRequest)
        }
    },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Save Goals",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HealthAwareGoalCard(
    title: String,
    icon: ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String,
    onValueChange: (Float) -> Unit,
    steps: Int,
    formatValue: (Float) -> String,
    recommendation: HealthRecommendation,
    color: Color
) {
    val isWithinRecommended = value in recommendation.min..recommendation.max
    val cardColor = if (isWithinRecommended) {
        color
    } else {
        MaterialTheme.colorScheme.errorContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isWithinRecommended)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Text(
                    text = "${formatValue(value)} $unit",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (!isWithinRecommended)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = if (isWithinRecommended)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    activeTrackColor = if (isWithinRecommended)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    inactiveTrackColor = if (isWithinRecommended)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                    else
                        MaterialTheme.colorScheme.error.copy(alpha = 0.24f)
                )
            )

            AnimatedVisibility(
                visible = !isWithinRecommended,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = recommendation.warningMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = recommendation.recommendationMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    title: String,
    icon: ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String,
    onValueChange: (Float) -> Unit,
    steps: Int,
    formatValue: (Float) -> String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Text(
                    text = "${formatValue(value)} $unit",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                )
            )
        }
    }
}

