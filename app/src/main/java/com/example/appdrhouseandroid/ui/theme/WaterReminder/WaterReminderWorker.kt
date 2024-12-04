import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class HealthReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setContentTitle("Health Reminder Active")
            .setContentText("Your health reminders are running")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        return ForegroundInfo(FOREGROUND_NOTIFICATION_ID, notification)
    }

    override suspend  fun doWork(): Result {
        Log.d(TAG, "doWork: Starting work")
        return try {
            val reminderType = inputData.getString(KEY_REMINDER_TYPE) ?: ReminderType.WATER.name
            showNotification(ReminderType.valueOf(reminderType))
            Log.d(TAG, "doWork: Work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: Error", e)
            Result.retry()
        }
    }

    private fun showNotification(reminderType: ReminderType) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Health Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Regular health activity reminders"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Calendar.getInstance().time)

        val (icon, title, message) = getReminderDetails(reminderType, currentTime)

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .build()

        notificationManager.notify(reminderType.ordinal, notification)
    }

    private fun getReminderDetails(reminderType: ReminderType, currentTime: String): Triple<Int, String, String> {
        return when (reminderType) {
            ReminderType.WATER -> Triple(
                android.R.drawable.ic_dialog_info,
                "💧 Water Reminder",
                "It's $currentTime - Time to drink water!"
            )
            ReminderType.STEPS -> Triple(
                android.R.drawable.ic_dialog_info,
                "👣 Steps Reminder",
                "It's $currentTime - Time to take a walk!"
            )
            ReminderType.SLEEP -> Triple(
                android.R.drawable.ic_dialog_info,
                "😴 Sleep Reminder",
                "It's $currentTime - Time to prepare for bed!"
            )
            ReminderType.COFFEE -> Triple(
                android.R.drawable.ic_dialog_info,
                "☕ Coffee Tracker",
                "It's $currentTime - Don't forget to log your coffee intake!"
            )
            ReminderType.WORKOUT -> Triple(
                android.R.drawable.ic_dialog_info,
                "💪 Workout Reminder",
                "It's $currentTime - Time for your workout!"
            )
        }
    }

    enum class ReminderType {
        WATER, STEPS, SLEEP, COFFEE, WORKOUT
    }

    companion object {
        private const val TAG = "HealthReminderWorker"
        private const val CHANNEL_ID = "health_reminder_channel"
        private const val FOREGROUND_NOTIFICATION_ID = 99
        private const val MIN_BACKOFF_MILLIS = 30000L // 30 seconds
        private const val KEY_REMINDER_TYPE = "reminder_type"

        fun scheduleReminder(
            context: Context,
            reminderType: ReminderType,
            intervalMinutes: Long = 15
        ) {
            Log.d(TAG, "Scheduling $reminderType reminder...")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build()

            val inputData = Data.Builder()
                .putString(KEY_REMINDER_TYPE, reminderType.name)
                .build()

            val periodicWork = PeriodicWorkRequestBuilder<HealthReminderWorker>(
                intervalMinutes, TimeUnit.MINUTES,
                // Add flex interval to help with battery optimization
                intervalMinutes / 4, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setInputData(workDataOf(KEY_REMINDER_TYPE to reminderType.name))
                .addTag("health_reminder")  // Add a tag for easier management
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).apply {
                // Create an immediate one-time work for first notification
                val oneTimeWork = OneTimeWorkRequestBuilder<HealthReminderWorker>()
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()

                enqueueUniqueWork(
                    "${reminderType.name}_immediate",
                    ExistingWorkPolicy.REPLACE,
                    oneTimeWork
                )

                // Schedule the periodic work
                enqueueUniquePeriodicWork(
                    reminderType.name,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    periodicWork
                )
            }

            Log.d(TAG, "$reminderType reminder scheduled successfully")
        }

        fun cancelReminder(context: Context, reminderType: ReminderType) {
            WorkManager.getInstance(context).apply {
                cancelUniqueWork(reminderType.name)
                cancelUniqueWork("${reminderType.name}_immediate")
            }
            Log.d(TAG, "$reminderType reminders cancelled")
        }

        fun cancelAllReminders(context: Context) {
            ReminderType.values().forEach { reminderType ->
                cancelReminder(context, reminderType)
            }
            Log.d(TAG, "All reminders cancelled")
        }
    }
}