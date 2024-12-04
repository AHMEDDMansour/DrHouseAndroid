package com.example.appdrhouseandroid.ui.theme.reminder

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.*
import com.example.appdrhouseandroid.ui.theme.Set_Goals.GoalSettingScreenViewModel
import com.example.appdrhouseandroid.data.network.GoalResponse
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Duration
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalReminder(viewModel: GoalSettingScreenViewModel) {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }
    val goals = viewModel.goals.collectAsState().value

    var selectedGoal by remember { mutableStateOf<GoalResponse?>(null) }
    var selectedMetric by remember { mutableStateOf<GoalMetric?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoalReminderHeader()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            goals?.let { goalsList ->
                items(goalsList) { goal ->
                    GoalCard(
                        goal = goal,
                        workManager = workManager,
                        onMetricTimeClick = { metric ->
                            selectedGoal = goal
                            selectedMetric = metric
                            showTimePicker = true
                        }
                    )
                }
            } ?: item {
                LoadingIndicator()
            }
        }
    }

    if (showTimePicker) {
        ReminderTimePicker(
            onTimeSelected = { time ->
                selectedGoal?.let { goal ->
                    selectedMetric?.let { metric ->
                        scheduleGoalReminder(context, goal, metric, time)
                    }
                }
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
private fun GoalReminderHeader() {
    Text(
        text = "Health Goals Reminders",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(16.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun GoalCard(
    goal: GoalResponse,
    workManager: WorkManager,
    onMetricTimeClick: (GoalMetric) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Health Goals",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            MetricsList(goal, workManager, onMetricTimeClick)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MetricsList(
    goal: GoalResponse,
    workManager: WorkManager,
    onMetricTimeClick: (GoalMetric) -> Unit
) {
    val metrics = listOf(
        MetricInfo(GoalMetric.STEPS, Icons.Default.DirectionsWalk, "${goal.steps} steps"),
        MetricInfo(GoalMetric.WATER, Icons.Default.WaterDrop, "${goal.water} glasses"),
        MetricInfo(GoalMetric.SLEEP, Icons.Default.Bedtime, "${goal.sleepHours} hours"),
        MetricInfo(GoalMetric.COFFEE, Icons.Default.Coffee, "${goal.coffeeCups} cups"),
        MetricInfo(GoalMetric.WORKOUT, Icons.Default.FitnessCenter, "${goal.workout} minutes")
    )

    metrics.forEach { metricInfo ->
        MetricReminderRow(
            metricInfo = metricInfo,
            goalId = "${goal.id}_${metricInfo.metric.name.lowercase()}",
            workManager = workManager,
            onTimeClick = { onMetricTimeClick(metricInfo.metric) }
        )
    }
}

@Composable
private fun MetricReminderRow(
    metricInfo: MetricInfo,
    goalId: String,
    workManager: WorkManager,
    onTimeClick: () -> Unit
) {
    val workInfos by workManager
        .getWorkInfosByTagLiveData(goalId)
        .observeAsState(listOf())

    val isReminderActive = workInfos.any { !it.state.isFinished }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = metricInfo.icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Column {
                Text(
                    text = metricInfo.metric.toString().capitalize(),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = metricInfo.value,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            TextButton(
                onClick = onTimeClick,
                enabled = !isReminderActive
            ) {
                Text(if (isReminderActive) "Scheduled" else "Set Time")
            }
            Switch(
                checked = isReminderActive,
                onCheckedChange = { checked ->
                    if (checked) {
                        onTimeClick()
                    } else {
                        cancelGoalReminder(context, goalId,metricInfo.metric)
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ReminderTimePicker(
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(8) }
    var minute by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Reminder Time") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TimePickerWheel(
                        value = hour,
                        onValueChange = { hour = it },
                        range = 0..23,
                        label = "Hour"
                    )
                    Text(":", modifier = Modifier.padding(horizontal = 8.dp))
                    TimePickerWheel(
                        value = minute,
                        onValueChange = { minute = it },
                        range = 0..59,
                        label = "Minute"
                    )
                }

                Text(
                    text = LocalTime.of(hour, minute).format(
                        DateTimeFormatter.ofPattern("hh:mm a")
                    ),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onTimeSelected(LocalTime.of(hour, minute)) }
            ) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun TimePickerWheel(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        NumberPicker(
            value = value,
            onValueChange = onValueChange,
            range = range
        )
    }
}

private data class MetricInfo(
    val metric: GoalMetric,
    val icon: ImageVector,
    val value: String
)

// The existing NumberPicker composable remains the same
@Composable
private fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Column {
        IconButton(onClick = {
            if (value < range.last) onValueChange(value + 1)
        }) {
            Icon(Icons.Default.KeyboardArrowUp, "Increase")
        }

        Text(
            text = String.format("%02d", value),
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.headlineMedium
        )

        IconButton(onClick = {
            if (value > range.first) onValueChange(value - 1)
        }) {
            Icon(Icons.Default.KeyboardArrowDown, "Decrease")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalReminderItem(
    goal: GoalResponse,
    context: Context,
    workManager: WorkManager,
    onTimeClick: (GoalMetric) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Set Individual Reminders:",
                style = MaterialTheme.typography.titleMedium
            )

            // Separate reminder controls for each metric
            MetricReminderRow(
                metricName = "Steps",
                value = "${goal.steps} steps",
                goalId = "${goal.id}_steps",
                workManager = workManager,
                context = context,
                onTimeClick = { onTimeClick(GoalMetric.STEPS) }
            )

            MetricReminderRow(
                metricName = "Water",
                value = "${goal.water} glasses",
                goalId = "${goal.id}_water",
                workManager = workManager,
                context = context,
                onTimeClick = { onTimeClick(GoalMetric.WATER) }
            )

            MetricReminderRow(
                metricName = "Sleep",
                value = "${goal.sleepHours} hours",
                goalId = "${goal.id}_sleep",
                workManager = workManager,
                context = context,
                onTimeClick = { onTimeClick(GoalMetric.SLEEP) }
            )

            MetricReminderRow(
                metricName = "Coffee",
                value = "${goal.coffeeCups} cups",
                goalId = "${goal.id}_coffee",
                workManager = workManager,
                context = context,
                onTimeClick = { onTimeClick(GoalMetric.COFFEE) }
            )

            MetricReminderRow(
                metricName = "Workout",
                value = "${goal.workout} minutes",
                goalId = "${goal.id}_workout",
                workManager = workManager,
                context = context,
                onTimeClick = { onTimeClick(GoalMetric.WORKOUT) }
            )
        }
    }
}

@Composable
private fun MetricReminderRow(
    metricName: String,
    value: String,
    goalId: String,
    workManager: WorkManager,
    context: Context,
    onTimeClick: () -> Unit
) {
    val workInfos by workManager
        .getWorkInfosByTagLiveData(goalId)
        .observeAsState(listOf())

    val isReminderActive = workInfos.any { !it.state.isFinished }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(metricName)
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onTimeClick) {
                Text("Set Time")
            }
            Switch(
                checked = isReminderActive,
                onCheckedChange = { checked ->
                    if (checked) {
                        onTimeClick()
                    } else {
                    }
                }
            )
        }
    }
}

enum class GoalMetric {
    STEPS, WATER, SLEEP, COFFEE, WORKOUT
}

// Modified schedule function to handle individual metrics
@RequiresApi(Build.VERSION_CODES.O)
fun scheduleGoalReminder(
    context: Context,
    goal: GoalResponse,
    metric: GoalMetric,
    time: LocalTime
) {
    val workManager = WorkManager.getInstance(context)
    val goalId = goal.id ?: return

    val metricData = when (metric) {
        GoalMetric.STEPS -> workDataOf(
            "goal_id" to "${goalId}_steps",
            "metric_type" to "steps",
            "target_value" to goal.steps
        )
        GoalMetric.WATER -> workDataOf(
            "goal_id" to "${goalId}_water",
            "metric_type" to "water",
            "target_value" to goal.water
        )
        GoalMetric.SLEEP -> workDataOf(
            "goal_id" to "${goalId}_sleep",
            "metric_type" to "sleep",
            "target_value" to goal.sleepHours
        )
        GoalMetric.COFFEE -> workDataOf(
            "goal_id" to "${goalId}_coffee",
            "metric_type" to "coffee",
            "target_value" to goal.coffeeCups
        )
        GoalMetric.WORKOUT -> workDataOf(
            "goal_id" to "${goalId}_workout",
            "metric_type" to "workout",
            "target_value" to goal.workout
        )
    }

    val workRequest = PeriodicWorkRequestBuilder<GoalReminderWorker>(24, TimeUnit.HOURS)
        .setInputData(metricData)
        .setInitialDelay(Duration.ofMillis(calculateInitialDelay(time)))
        .addTag("${goalId}_${metric.name.lowercase()}")
        .build()

    workManager.enqueueUniquePeriodicWork(
        "goal_reminder_${goalId}_${metric.name.lowercase()}",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}