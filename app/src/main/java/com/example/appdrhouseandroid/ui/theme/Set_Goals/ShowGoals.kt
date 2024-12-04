package com.example.appdrhouseandroid.ui.theme.Set_Goals

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appdrhouseandroid.R
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalContext


data class UserGoals(
    val stepsGoal: Float,
    val stepsProgress: Float,
    val waterGoal: Float,
    val waterProgress: Float,
    val sleepGoal: Float,
    val sleepProgress: Float,
    val coffeeGoal: Float,
    val coffeeProgress: Float,
    val workoutGoal: Float,
    val workoutProgress: Float
)

@Composable
fun ShowGoalsScreen(
    viewModel: GoalsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.error != null -> {


            }
            else -> {




            }
        }
    }
}

@Composable
private fun GoalsContent(
    userGoals: UserGoals,
    onProgressUpdate: (String, Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Daily Goals",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        GoalDisplayCard(
            title = "Daily Step Goal",
            value = userGoals.stepsProgress,
            goal = userGoals.stepsGoal,
            unit = "steps",
            iconRes = R.drawable.ic_steps1,
            onIncrement = { onProgressUpdate("steps", userGoals.stepsProgress + 1000f) }
        )

        GoalDisplayCard(
            title = "Water Intake Goal",
            value = userGoals.waterProgress,
            goal = userGoals.waterGoal,
            unit = "liters",
            iconRes = R.drawable.ic_water,
            onIncrement = { onProgressUpdate("water", userGoals.waterProgress + 0.5f) }
        )

        // Similar updates for sleep, coffee, and workout cards...
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Retry")
        }
    }
}

@Composable
fun GoalDisplayCard(
    title: String,
    value: Float,
    goal: Float,
    unit: String,
    iconRes: Int,
    onIncrement: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            ) {
                AnimatedCircularProgress(
                    progress = (value / goal).coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                )
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${value.toInt()} / ${goal.toInt()} $unit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Button(
                    onClick = onIncrement,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "Add $unit")
                }
            }
        }
    }
}
@Composable
fun AnimatedCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color,
    strokeWidth: Float = 12f, // Increased stroke width for a more prominent circle
    backgroundColor: Color = color.copy(alpha = 0.2f) // Background circle with transparency
) {
    val animatedProgress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Animate progress on value change
    LaunchedEffect(progress) {
        scope.launch {
            animatedProgress.animateTo(
                targetValue = progress,
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing) // Smooth transition
            )
        }
    }

    // Use the correct Canvas from androidx.compose.ui.graphics
    Canvas(modifier = modifier) {
        val canvasSize = size.minDimension
        val center = Offset(size.width / 2, size.height / 2)

        // Draw background circle with a softer look
        drawCircle(
            color = backgroundColor,
            radius = canvasSize / 2,
            style = Stroke(width = strokeWidth)
        )

        // Draw animated arc for progress
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = animatedProgress.value * 360f, // Control the sweep angle for animation
            useCenter = false,
            style = Stroke(width = strokeWidth),
        )
    }
}



