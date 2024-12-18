package com.example.appdrhouseandroid.ui.theme.StepCounter


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StepCounter(
    private val context: Context
) : SensorEventListener, DefaultLifecycleObserver {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private var initialSteps: Int? = null

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                if (initialSteps == null) {
                    initialSteps = it.values[0].toInt()
                }
                val currentSteps = it.values[0].toInt() - (initialSteps ?: 0)
                _steps.value = currentSteps
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for step counter
    }

    fun resetSteps() {
        initialSteps = null
        _steps.value = 0
    }
}