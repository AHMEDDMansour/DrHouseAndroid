package com.example.appdrhouseandroid.ui.theme.WaterReminder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthReminder() {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }

    // Track active reminders and their intervals
    val reminderStates = remember {
        HealthReminderWorker.ReminderType.values().associate { type ->
            type to mutableStateOf(ReminderState(isActive = false, interval = 15L))
        }
    }

    // Observe work states for each reminder type
    HealthReminderWorker.ReminderType.values().forEach { reminderType ->
        val workInfos by workManager
            .getWorkInfosForUniqueWorkLiveData(reminderType.name)
            .observeAsState(listOf())

        reminderStates[reminderType]?.value = reminderStates[reminderType]?.value?.copy(
            isActive = workInfos.any { !it.state.isFinished }
        ) ?: ReminderState(isActive = false, interval = 15L)
    }

    var expandedReminderType by remember { mutableStateOf<HealthReminderWorker.ReminderType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Health Reminders",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(HealthReminderWorker.ReminderType.values()) { reminderType ->
                val reminderState = reminderStates[reminderType]?.value ?: ReminderState()
                ReminderCard(
                    reminderType = reminderType,
                    reminderState = reminderState,
                    isExpanded = expandedReminderType == reminderType,
                    onExpandedChange = { expanded ->
                        expandedReminderType = if (expanded) reminderType else null
                    },
                    onIntervalChange = { newInterval ->
                        reminderStates[reminderType]?.value = reminderState.copy(interval = newInterval)
                        if (reminderState.isActive) {
                            // Reschedule with new interval if active
                            HealthReminderWorker.scheduleReminder(context, reminderType, newInterval)
                        }
                    },
                    onToggle = { interval ->
                        if (reminderState.isActive) {
                            HealthReminderWorker.cancelReminder(context, reminderType)
                        } else {
                            HealthReminderWorker.scheduleReminder(context, reminderType, interval)
                        }
                    }
                )
            }
        }

        Button(
            onClick = { HealthReminderWorker.cancelAllReminders(context) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Stop All Reminders")
        }
    }
}

data class ReminderState(
    val isActive: Boolean = false,
    val interval: Long = 15L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderCard(
    reminderType: HealthReminderWorker.ReminderType,
    reminderState: ReminderState,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onIntervalChange: (Long) -> Unit,
    onToggle: (Long) -> Unit
) {
    var showIntervalDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        onClick = { onExpandedChange(!isExpanded) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getReminderTypeDisplay(reminderType),
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = reminderState.isActive,
                    onCheckedChange = { onToggle(reminderState.interval) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (reminderState.isActive) {
                        "Active - Every ${reminderState.interval} minutes"
                    } else {
                        "Inactive"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(onClick = { showIntervalDialog = true }) {
                    Text("Change Interval")
                }
            }
        }
    }

    if (showIntervalDialog) {
        IntervalSelectorDialog(
            currentInterval = reminderState.interval,
            onIntervalSelected = {
                onIntervalChange(it)
                showIntervalDialog = false
            },
            onDismiss = { showIntervalDialog = false }
        )
    }
}

@Composable
private fun IntervalSelectorDialog(
    currentInterval: Long,
    onIntervalSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val intervals = listOf(15L, 30L, 45L, 60L, 90L, 120L, 180L, 240L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Interval") },
        text = {
            LazyColumn {
                items(intervals) { interval ->
                    TextButton(
                        onClick = { onIntervalSelected(interval) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "$interval minutes",
                            color = if (interval == currentInterval)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getReminderTypeDisplay(reminderType: HealthReminderWorker.ReminderType): String {
    return when (reminderType) {
        HealthReminderWorker.ReminderType.WATER -> "💧 Water Reminder"
        HealthReminderWorker.ReminderType.STEPS -> "👣 Steps Reminder"
        HealthReminderWorker.ReminderType.SLEEP -> "😴 Sleep Reminder"
        HealthReminderWorker.ReminderType.COFFEE -> "☕ Coffee Tracker"
        HealthReminderWorker.ReminderType.WORKOUT -> "💪 Workout Reminder"
    }
}