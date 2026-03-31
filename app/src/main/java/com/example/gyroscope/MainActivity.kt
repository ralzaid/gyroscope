package com.example.gyroscope

import android.content.Context
import android.hardware.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val tiltX = mutableFloatStateOf(0f)
    private val tiltY = mutableFloatStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            MaterialTheme {
                GameScreen(tiltX.floatValue, tiltY.floatValue)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        tiltX.floatValue = event?.values?.get(0) ?: 0f
        tiltY.floatValue = event?.values?.get(1) ?: 0f
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun GameScreen(tiltX: Float, tiltY: Float) {

    var x by remember { mutableStateOf(300f) }
    var y by remember { mutableStateOf(500f) }

    val radius = 40f

    val obstacles = listOf(
        Rect(100f, 300f, 900f, 320f),
        Rect(200f, 600f, 220f, 1200f),
        Rect(400f, 800f, 1000f, 820f)
    )

    LaunchedEffect(tiltX, tiltY) {
        while (true) {

            val nextX = x + (-tiltX * 5)
            val nextY = y + (tiltY * 5)

            val ball = Rect(
                nextX - radius,
                nextY - radius,
                nextX + radius,
                nextY + radius
            )

            val hit = obstacles.any { it.overlaps(ball) }

            if (!hit) {
                x = nextX.coerceIn(radius, 1000f)
                y = nextY.coerceIn(radius, 1800f)
            }

            delay(16)
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE4EC))
    ) {

        // obstacles
        obstacles.forEach {
            drawRect(
                color = Color(0xFFFF69B4),
                topLeft = Offset(it.left, it.top),
                size = Size(it.width, it.height)
            )
        }

        // ball
        drawCircle(
            color = Color(0xFFD81B60),
            radius = radius,
            center = Offset(x, y)
        )
    }
}