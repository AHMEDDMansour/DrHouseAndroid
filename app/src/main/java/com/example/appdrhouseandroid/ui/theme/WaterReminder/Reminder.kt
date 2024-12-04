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

    // Track active reminders
    val reminderStates = remember {
        HealthReminderWorker.ReminderType.values().associate { type ->
            type to mutableStateOf(false)
        }
    }

    // Observe work states for each reminder type
    HealthReminderWorker.ReminderType.values().forEach { reminderType ->
        val workInfos by workManager
            .getWorkInfosForUniqueWorkLiveData(reminderType.name)
            .observeAsState(listOf())

        reminderStates[reminderType]?.value = workInfos.any { !it.state.isFinished }
    }

    var selectedInterval by remember { mutableStateOf(15L) }
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
                ReminderCard(
                    reminderType = reminderType,
                    isActive = reminderStates[reminderType]?.value ?: false,
                    interval = selectedInterval,
                    onIntervalChange = { selectedInterval = it },
                    isExpanded = expandedReminderType == reminderType,
                    onExpandedChange = { expanded ->
                        expandedReminderType = if (expanded) reminderType else null
                    },
                    onToggle = { interval ->
                        if (reminderStates[reminderType]?.value == true) {
                            HealthReminderWorker.cancelReminder(context, reminderType)
                        } else {
                            HealthReminderWorker.scheduleReminder(
                                context,
                                reminderType,
                                interval
                            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderCard(
    reminderType: HealthReminderWorker.ReminderType,
    isActive: Boolean,
    interval: Long,
    onIntervalChange: (Long) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onToggle: (Long) -> Unit
) {
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
                    checked = isActive,
                    onCheckedChange = { onToggle(interval) }
                )
            }

            if (isExpanded && !isActive) {
                IntervalSelector(
                    interval = interval,
                    onIntervalChange = onIntervalChange
                )
            }

            Text(
                text = if (isActive) {
                    "Active - Reminding every $interval minutes"
                } else {
                    "Inactive"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntervalSelector(
    interval: Long,
    onIntervalChange: (Long) -> Unit
) {
    val intervals = listOf(15L, 30L, 60L, 120L, 240L)

    ExposedDropdownMenuBox(
        expanded = false,
        onExpandedChange = {}
    ) {
        OutlinedTextField(
            value = "$interval minutes",
            onValueChange = {},
            readOnly = true,
            label = { Text("Reminder Interval") },
            modifier = Modifier.menuAnchor()
        )

        DropdownMenu(
            expanded = false,
            onDismissRequest = {}
        ) {
            intervals.forEach { intervalOption ->
                DropdownMenuItem(
                    text = { Text("$intervalOption minutes") },
                    onClick = { onIntervalChange(intervalOption) }
                )
            }
        }
    }
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