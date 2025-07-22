package com.ruyue.flymelauncher

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ruyue.flymelauncher.GlobalBlurController

private const val PREFS_NAME = "blur_settings_prefs"
private const val KEY_BLUR_RADIUS = "blur_radius"
private const val KEY_FORWARD_DURATION = "forward_duration"
private const val KEY_REVERSE_DURATION = "reverse_duration"
private const val KEY_OVERLAY_DURATION = "overlay_duration"
private const val KEY_BLACK_VATE = "black_vate"

public class BlurSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//，默认
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val initialBlur = prefs.getFloat(KEY_BLUR_RADIUS, 90f)
        val initialForward = prefs.getLong(KEY_FORWARD_DURATION, 310L).toFloat()
        val initialReverse = prefs.getLong(KEY_REVERSE_DURATION, 100L).toFloat()
        val initialOverlay = prefs.getLong(KEY_OVERLAY_DURATION, 2000L).toFloat()
        val initialBlack = prefs.getFloat(KEY_BLACK_VATE, 0.5f)

        setContent {
            MaterialTheme(colorScheme = blueWhiteColorScheme(this)) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    BlurSettingsScreen(
                        initialBlur,
                        initialForward,
                        initialReverse,
                        initialOverlay,
                        initialBlack
                    ) { key, value ->
                        with(prefs.edit()) {
                            when (key) {
                                KEY_BLUR_RADIUS -> putFloat(KEY_BLUR_RADIUS, value)
                                KEY_BLACK_VATE -> putFloat(KEY_BLACK_VATE, value)
                                KEY_FORWARD_DURATION -> putLong(KEY_FORWARD_DURATION, value.toLong())
                                KEY_REVERSE_DURATION -> putLong(KEY_REVERSE_DURATION, value.toLong())
                                KEY_OVERLAY_DURATION -> putLong(KEY_OVERLAY_DURATION, value.toLong())
                            }
                            apply()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BlurSettingsScreen(
    initialBlur: Float,
    initialForward: Float,
    initialReverse: Float,
    initialOverlay: Float,
    initialBlack: Float,
    onValuePersist: (String, Float) -> Unit
) {
    var blurRadius by rememberSaveable { mutableStateOf(initialBlur) }
    var forwardDuration by rememberSaveable { mutableStateOf(initialForward) }
    var reverseDuration by rememberSaveable { mutableStateOf(initialReverse) }
    var overlayDuration by rememberSaveable { mutableStateOf(initialOverlay) }
    var blackVate by rememberSaveable { mutableStateOf(initialBlack) }

    LaunchedEffect(Unit) {
        GlobalBlurController.setMaxBlurRadius(blurRadius)
        GlobalBlurController.setForwardDuration(forwardDuration.toLong())
        GlobalBlurController.setReverseDuration(reverseDuration.toLong())
        GlobalBlurController.setOverlayDuration(overlayDuration.toLong())
        GlobalBlurController.setBlackVate(blackVate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "模糊参数设置",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.Start)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SettingItem(
                    label = "模糊半径",
                    value = blurRadius,
                    range = 0f..200f,
                    suffix = "px"
                ) {
                    blurRadius = it
                    GlobalBlurController.setMaxBlurRadius(it)
                    onValuePersist(KEY_BLUR_RADIUS, it)
                }

                SettingItem(
                    label = "正向动画",
                    value = forwardDuration,
                    range = 0f..1000f,
                    suffix = "ms"
                ) {
                    forwardDuration = it
                    GlobalBlurController.setForwardDuration(it.toLong())
                    onValuePersist(KEY_FORWARD_DURATION, it)
                }

                SettingItem(
                    label = "反向动画",
                    value = reverseDuration,
                    range = 0f..1000f,
                    suffix = "ms"
                ) {
                    reverseDuration = it
                    GlobalBlurController.setReverseDuration(it.toLong())
                    onValuePersist(KEY_REVERSE_DURATION, it)
                }

                SettingItem(
                    label = "遮罩延迟",
                    value = overlayDuration,
                    range = 0f..5000f,
                    suffix = "ms"
                ) {
                    overlayDuration = it
                    GlobalBlurController.setOverlayDuration(it.toLong())
                    onValuePersist(KEY_OVERLAY_DURATION, it)
                }

                SettingItem(
                    label = "黑色遮罩强度",
                    value = blackVate,
                    range = 0f..1f,
                    valueFormatter = { "${(it * 100).toInt()}%" }
                ) {
                    blackVate = it
                    GlobalBlurController.setBlackVate(it)
                    onValuePersist(KEY_BLACK_VATE, it)
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    suffix: String = "",
    valueFormatter: (Float) -> String = { "${it.toInt()}$suffix" },
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = valueFormatter(value),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = when (range.endInclusive - range.start) {
                in 0f..1f -> 10
                else -> 0
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = Color(0xFFE0E0E0)
            )
        )
    }

    if (label != "遮罩透明度") {
        Divider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = Color(0xFFE0E0E0)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BlurSettingsPreview() {
    MaterialTheme(colorScheme = blueWhiteColorScheme(LocalContext.current)) {
        BlurSettingsScreen(90f, 310f, 100f, 2000f, 0.5f) { _, _ -> }
    }
}

private fun blueWhiteColorScheme(context: Context): ColorScheme {
    return lightColorScheme().copy(
        background = Color.White,
        surface = Color.White,
        surfaceVariant = Color(0xFFF5F5F5),
        onSurface = Color.Black,
        onSurfaceVariant = Color(0xFF666666),
        primary = Color(0xFF6200EE),
        outline = Color(0xFFCCCCCC),
        outlineVariant = Color(0xFFCCCCCC)
    )
}