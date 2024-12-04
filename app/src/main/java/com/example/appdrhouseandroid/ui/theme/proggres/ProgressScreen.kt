package com.example.appdrhouseandroid.ui.theme.proggres
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalDrink  // Instead of WaterDrop
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.appdrhouseandroid.data.network.GoalResponse
import com.example.appdrhouseandroid.data.network.ProgressResponse
import com.patrykandpatryk.vico.core.entry.FloatEntry
import com.patrykandpatryk.vico.compose.chart.line.lineChart
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.core.entry.entryModelOf
import com.example.appdrhouseandroid.ui.theme.Set_Goals.GoalSettingScreenViewModel
import com.patrykandpatryk.vico.core.chart.line.LineChart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.PathEffect
@Composable
fun ProgressScreen(
    progressViewModel: ProgressViewModel,
    goalViewModel: GoalSettingScreenViewModel
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("USER_ID", null)

    val progressState by progressViewModel.progressState.collectAsState()
    val progressHistoryState by progressViewModel.progressHistoryState.collectAsState()
    val goals by goalViewModel.goals.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Today's Progress", "History")

    LaunchedEffect(userId) {
        if (userId != null) {
            Log.d("ProgressScreen", "Fetching goals for userId: $userId")
            goalViewModel.fetchGoals(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Daily Progress Tracker",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Track your wellness goals",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Tab Section
        TabRow(
            selectedTabIndex = selectedTab,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when {
            goals == null -> LoadingState()
            goals?.isEmpty() == true -> NoGoalsFound { /* Handle navigation */ }
            else -> {
                goals?.firstOrNull()?.let { currentGoal ->
                    if (currentGoal.id == null) {
                        ErrorState(userId, goalViewModel)
                    } else {
                        LaunchedEffect(currentGoal.id) {
                            progressViewModel.refreshProgress(currentGoal.id)
                        }

                        // Content based on selected tab
                        when (selectedTab) {
                            0 -> EnhancedTodayProgress(
                                progressState = progressState,
                                currentGoal = currentGoal,
                                onUpdateProgress = { steps, water, sleep, coffee, workout ->
                                    progressViewModel.addProgress(
                                        goalId = currentGoal.id,
                                        steps = steps,
                                        water = water,
                                        sleep = sleep,
                                        coffee = coffee,
                                        workout = workout
                                    )
                                }
                            )
                            1 -> HistoryContent(
                                progressHistoryState = progressHistoryState,
                                currentGoal = currentGoal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorState(userId: String?, goalViewModel: GoalSettingScreenViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 8.dp)
        )

        Text(
            text = "Unable to load progress tracker",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Please check your connection and try again",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = {
                userId?.let { goalViewModel.fetchGoals(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text("Retry")
        }
    }
}
@Composable
fun EnhancedTodayProgress(
    progressState: ProgressViewModel.ProgressUIState,
    currentGoal: GoalResponse,
    onUpdateProgress: (Int?, Int?, Int?, Int?, Int?) -> Unit
) {
    var steps by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }
    var sleep by remember { mutableStateOf("") }
    var coffee by remember { mutableStateOf("") }
    var workout by remember { mutableStateOf("") }

    LaunchedEffect(progressState) {
        if (progressState is ProgressViewModel.ProgressUIState.Success) {
            val progress = progressState.progress
            steps = progress.steps?.toString() ?: ""
            water = progress.water?.toString() ?: ""
            sleep = progress.sleepHours?.toString() ?: ""
            coffee = progress.coffeeCups?.toString() ?: ""
            workout = progress.workout?.toString() ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        EnhancedProgressInput(
            label = "Steps",
            value = steps,
            onValueChange = { steps = it },
            target = currentGoal.steps,
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        EnhancedProgressInput(
            label = "Water (glasses)",
            value = water,
            onValueChange = { water = it },
            target = currentGoal.water,
            icon = Icons.Default.LocalDrink,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        EnhancedProgressInput(
            label = "Sleep (hours)",
            value = sleep,
            onValueChange = { sleep = it },
            target = currentGoal.sleepHours,
            icon = Icons.Default.Bedtime,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        EnhancedProgressInput(
            label = "Coffee (cups)",
            value = coffee,
            onValueChange = { coffee = it },
            target = currentGoal.coffeeCups,
            icon = Icons.Default.Coffee,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        EnhancedProgressInput(
            label = "Workout (minutes)",
            value = workout,
            onValueChange = { workout = it },
            target = currentGoal.workout,
            icon = Icons.Default.FitnessCenter,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onUpdateProgress(
                    steps.toIntOrNull(),
                    water.toIntOrNull(),
                    sleep.toIntOrNull(),
                    coffee.toIntOrNull(),
                    workout.toIntOrNull()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )
                Text("Update Progress", fontSize = 16.sp)
            }
        }
    }
}
@Composable
fun MetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    target: Int,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*$"))) {
                        onValueChange(newValue)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            value.toIntOrNull()?.let { currentValue ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$currentValue/$target",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    LinearProgressIndicator(
                        progress = (currentValue.toFloat() / target).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .padding(top = 4.dp),
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryContent(
    progressHistoryState: ProgressViewModel.ProgressHistoryState,
    currentGoal: GoalResponse
) {
    when (progressHistoryState) {
        is ProgressViewModel.ProgressHistoryState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ProgressViewModel.ProgressHistoryState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Charts Section
                item {
                    ProgressCharts(
                        history = progressHistoryState.history,
                        goal = currentGoal
                    )
                }

                // Detailed History Cards
                items(progressHistoryState.history) { progress ->
                    HistoryCard(progress = progress, goal = currentGoal)
                }
            }
        }
        is ProgressViewModel.ProgressHistoryState.Error -> {
            ErrorMessage(message = progressHistoryState.message)
        }

        ProgressViewModel.ProgressHistoryState.Initial -> TODO()
    }
}

@Composable
fun ProgressCharts(
    history: List<ProgressResponse>,
    goal: GoalResponse
) {
    val metrics = listOf(
        MetricData("Steps", history.map { it.steps ?: 0 }, goal.steps, MaterialTheme.colorScheme.primary),
        MetricData("Water", history.map { it.water ?: 0 }, goal.water, MaterialTheme.colorScheme.secondary),
        MetricData("Sleep", history.map { it.sleepHours ?: 0 }, goal.sleepHours, MaterialTheme.colorScheme.tertiary),
        MetricData("Coffee", history.map { it.coffeeCups ?: 0 }, goal.coffeeCups, MaterialTheme.colorScheme.error),
        MetricData("Workout", history.map { it.workout ?: 0 }, goal.workout, MaterialTheme.colorScheme.primaryContainer)
    )

    var selectedMetric by remember { mutableStateOf(metrics.first()) }
    var animationProgress by remember { mutableStateOf(0f) }

    // Animate chart entry
    LaunchedEffect(selectedMetric) {
        animationProgress = 0f
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animationProgress = value
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Metric Selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(metrics) { metric ->
                FilterChip(
                    selected = metric == selectedMetric,
                    onClick = { selectedMetric = metric },
                    label = { Text(metric.name) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (metric.name) {
                                "Steps" -> Icons.AutoMirrored.Filled.DirectionsWalk
                                "Water" -> Icons.Default.LocalDrink
                                "Sleep" -> Icons.Default.Bedtime
                                "Coffee" -> Icons.Default.Coffee
                                "Workout" -> Icons.Default.FitnessCenter
                                else -> Icons.Default.Category
                            },
                            contentDescription = metric.name
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = metric.color.copy(alpha = 0.2f),
                        selectedLabelColor = metric.color,
                        selectedLeadingIconColor = metric.color
                    )
                )
            }
        }

        // Animated Chart Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Chart Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedMetric.name} Progression",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Target: ${selectedMetric.target}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Advanced Animated Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(top = 16.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val values = selectedMetric.values
                        val width = size.width
                        val height = size.height
                        val maxValue = (values.maxOrNull() ?: 1).coerceAtLeast(selectedMetric.target)
                        val targetLine = selectedMetric.target

                        // Gradient Background
                        val gradientBrush = Brush.verticalGradient(
                            colors = listOf(
                                selectedMetric.color.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                        drawRect(
                            brush = gradientBrush,
                            size = size
                        )

                        // Grid Lines
                        val gridPaint = Paint().apply {
                            color = Color.LightGray.toArgb()
                            strokeWidth = 1f
                            style = Paint.Style.STROKE
                        }
                        drawContext.canvas.nativeCanvas.apply {
                            // Horizontal grid lines
                            for (i in 1..4) {
                                val y = height * (i / 5f)
                                drawLine(0f, y, width, y, gridPaint)
                            }

                            // Vertical grid lines
                            val gridSpacing = width / (values.size + 1)
                            for (i in 1..values.size) {
                                val x = gridSpacing * i
                                drawLine(x, 0f, x, height, gridPaint)
                            }
                        }

                        // Target Line with Gradient
                        val targetY = height - (targetLine.toFloat() / maxValue * height)
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    selectedMetric.color.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            ),
                            start = Offset(0f, targetY),
                            end = Offset(width, targetY),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        // Animated Line with Gradient Fill
                        val scaledValues = values.map { it.toFloat() / maxValue * height }
                        val path = Path()
                        val fillPath = Path()

                        scaledValues.forEachIndexed { index, value ->
                            val x = index.toFloat() / (values.size - 1) * width
                            val y = height - (value * animationProgress)

                            if (index == 0) {
                                path.moveTo(x, y)
                                fillPath.moveTo(x, height)
                                fillPath.lineTo(x, y)
                            } else {
                                path.lineTo(x, y)
                                fillPath.lineTo(x, y)
                            }

                            if (index == scaledValues.lastIndex) {
                                fillPath.lineTo(x, height)
                                fillPath.close()
                            }
                        }

                        // Gradient fill for the area under the line
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    selectedMetric.color.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )

                        // Main line
                        drawPath(
                            path = path,
                            color = selectedMetric.color,
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )

                        // Animated Points with Glow Effect
                        scaledValues.forEachIndexed { index, value ->
                            val x = index.toFloat() / (values.size - 1) * width
                            val y = height - (value * animationProgress)

                            // Glow effect
                            drawCircle(
                                color = selectedMetric.color.copy(alpha = 0.3f),
                                radius = 12f * animationProgress,
                                center = Offset(x, y)
                            )

                            // Point
                            drawCircle(
                                color = selectedMetric.color,
                                radius = 8f * animationProgress,
                                center = Offset(x, y)
                            )
                        }
                    }
                }

                // Date Labels with Subtle Styling
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    history.take(selectedMetric.values.size).forEachIndexed { index, progress ->
                        Text(
                            text = progress.date.substringBefore('T'),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Detailed Analytics Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${selectedMetric.name} Detailed Analytics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val values = selectedMetric.values
                val average = values.average()
                val max = values.maxOrNull() ?: 0
                val min = values.minOrNull() ?: 0
                val progress = (average / selectedMetric.target * 100).coerceIn(0.0, 100.0)

                // Animated Progress Indicators
                values.forEachIndexed { index, value ->
                    AnimatedProgressBar(
                        progress = (value.toFloat() / selectedMetric.target).coerceIn(0f, 1f),
                        label = "Day ${index + 1}",
                        color = selectedMetric.color,
                        value = value,
                        target = selectedMetric.target
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Statistics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "Average",
                        value = String.format("%.1f", average),
                        color = selectedMetric.color
                    )
                    StatItem(
                        label = "Best Day",
                        value = max.toString(),
                        color = selectedMetric.color
                    )
                    StatItem(
                        label = "Worst Day",
                        value = min.toString(),
                        color = selectedMetric.color
                    )
                    StatItem(
                        label = "Progress",
                        value = "${progress.toInt()}%",
                        color = selectedMetric.color
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedProgressBar(
    progress: Float,
    label: String,
    color: Color,
    value: Int,
    target: Int
) {
    var animatedProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(progress) {
        animate(
            initialValue = 0f,
            targetValue = progress,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animatedProgress = value
        }
    }

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$value/$target",
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }

        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.3f)
        )
    }
}
@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class MetricData(
    val name: String,
    val values: List<Int>,
    val target: Int,
    val color: Color
)

@Composable
fun HistoryCard(
    progress: ProgressResponse,
    goal: GoalResponse
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = progress.date,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HistoryMetric(
                current = progress.steps ?: 0,
                target = goal.steps,
                label = "Steps",
                color = MaterialTheme.colorScheme.primary
            )
            HistoryMetric(
                current = progress.water ?: 0,
                target = goal.water,
                label = "Water",
                color = MaterialTheme.colorScheme.secondary
            )
            HistoryMetric(
                current = progress.sleepHours ?: 0,
                target = goal.sleepHours,
                label = "Sleep",
                color = MaterialTheme.colorScheme.tertiary
            )
            HistoryMetric(
                current = progress.coffeeCups ?: 0,
                target = goal.coffeeCups,
                label = "Coffee",
                color = MaterialTheme.colorScheme.error
            )
            HistoryMetric(
                current = progress.workout ?: 0,
                target = goal.workout,
                label = "Workout",
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun HistoryMetric(
    current: Int,
    target: Int,
    label: String,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$current/$target",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        LinearProgressIndicator(
            progress = (current.toFloat() / target).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .padding(top = 4.dp),
            color = color
        )
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}
@Composable
fun EnhancedProgressInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    target: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var animatedProgress by remember { mutableStateOf(0f) }
    val currentProgress = value.toFloatOrNull()?.div(target) ?: 0f

    LaunchedEffect(currentProgress) {
        animate(
            initialValue = animatedProgress,
            targetValue = currentProgress,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        ) { value, _ -> animatedProgress = value }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Show less" else "Show more"
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*$"))) {
                                onValueChange(newValue)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = color,
                            focusedLabelColor = color
                        ),
                        label = { Text("Enter value") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${value.ifEmpty { "0" }}/$target",
                        style = MaterialTheme.typography.bodyMedium,
                        color = color
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color.copy(alpha = 0.12f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .background(color)
                    )
                }

                if (expanded) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}% of daily goal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun NoGoalsFound(onSetGoalsClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No goals found",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = onSetGoalsClick) {
            Text("Set Your Goals")
        }
    }
}