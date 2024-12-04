package com.example.appdrhouseandroid.ui.theme.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.appdrhouseandroid.data.network.GoalResponse
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

enum class GoalMetricType {
    STEPS, WATER, SLEEP, COFFEE, WORKOUT
}

class GoalReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val goalId = inputData.getString("goal_id") ?: return Result.failure()
        val metricType = inputData.getString("metric_type")?.let {
            GoalMetricType.valueOf(it)
        } ?: return Result.failure()
        val targetValue = inputData.getInt("target_value", 0)

        createNotificationChannel()
        showMetricReminder(metricType, targetValue)

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Health Goals Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Individual reminders for your health goals"
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showMetricReminder(metricType: GoalMetricType, targetValue: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val (title, message) = when (metricType) {
            GoalMetricType.STEPS -> Pair(
                "Steps Goal Reminder",
                "Time to move! Your goal is $targetValue steps today"
            )
            GoalMetricType.WATER -> Pair(
                "Hydration Reminder",
                "Remember to drink water! Goal: $targetValue glasses today"
            )
            GoalMetricType.SLEEP -> Pair(
                "Sleep Schedule Reminder",
                "Aim for $targetValue hours of sleep tonight"
            )
            GoalMetricType.COFFEE -> Pair(
                "Coffee Intake Reminder",
                "Keep track of your coffee: limit is $targetValue cups"
            )
            GoalMetricType.WORKOUT -> Pair(
                "Workout Reminder",
                "Time for exercise! Goal: $targetValue minutes today"
            )
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Use metric type in notification ID to keep notifications separate
        val notificationId = "${System.currentTimeMillis()}_${metricType.name}".hashCode()
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val CHANNEL_ID = "health_goals_reminder"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun scheduleGoalReminder(
    context: Context,
    goal: GoalResponse,
    metricType: GoalMetricType,
    time: LocalTime
) {
    val workManager = WorkManager.getInstance(context)
    val goalId = goal.id ?: return

    val targetValue = when (metricType) {
        GoalMetricType.STEPS -> goal.steps
        GoalMetricType.WATER -> goal.water
        GoalMetricType.SLEEP -> goal.sleepHours
        GoalMetricType.COFFEE -> goal.coffeeCups
        GoalMetricType.WORKOUT -> goal.workout
    }

    val inputData = workDataOf(
        "goal_id" to goalId,
        "metric_type" to metricType.name,
        "target_value" to targetValue
    )

    val workRequest = PeriodicWorkRequestBuilder<GoalReminderWorker>(24, TimeUnit.HOURS)
        .setInputData(inputData)
        .setInitialDelay(Duration.ofMillis(calculateInitialDelay(time)))
        .addTag("${goalId}_${metricType.name.lowercase()}")
        .build()

    // Use unique work name for each metric type
    workManager.enqueueUniquePeriodicWork(
        "goal_reminder_${goalId}_${metricType.name.lowercase()}",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateInitialDelay(reminderTime: LocalTime): Long {
    val now = LocalDateTime.now()
    var reminderDateTime = LocalDateTime.of(now.toLocalDate(), reminderTime)

    if (reminderDateTime.isBefore(now)) {
        reminderDateTime = reminderDateTime.plusDays(1)
    }

    return Duration.between(now, reminderDateTime).toMillis()
}

fun cancelGoalReminder(context: Context, goalId: String, metricType: GoalMetric) {
    val workManager = WorkManager.getInstance(context)
    workManager.cancelUniqueWork("goal_reminder_${goalId}_${metricType.name.lowercase()}")
}

// Convenience function to cancel all reminders for a goal
fun cancelAllGoalReminders(context: Context, goalId: String) {
    val workManager = WorkManager.getInstance(context)
    GoalMetricType.values().forEach { metricType ->
        workManager.cancelUniqueWork("goal_reminder_${goalId}_${metricType.name.lowercase()}")
    }
}