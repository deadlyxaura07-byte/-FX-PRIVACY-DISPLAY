package com.fakeexx.privacy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

private val FxBlack = Color(0xFF070809)
private val FxPanel = Color(0xFF111316)
private val FxPanel2 = Color(0xFF191C20)
private val FxGold = Color(0xFFC9AA72)
private val FxMuted = Color(0xFF858990)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FxPrivacyScreen(
                overlayAllowed = { Settings.canDrawOverlays(this) },
                onGrant = {
                    if (!Settings.canDrawOverlays(this)) {
                        startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                    }
                },
                onToggle = { active, amount ->
                    if (active) {
                        if (Settings.canDrawOverlays(this)) {
                            ContextCompat.startForegroundService(
                                this,
                                Intent(this, PrivacyOverlayService::class.java).putExtra("intensity", amount)
                            )
                        } else {
                            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                        }
                    } else stopService(Intent(this, PrivacyOverlayService::class.java))
                }
            )
        }
    }
}

@Composable
private fun FxPrivacyScreen(
    overlayAllowed: () -> Boolean,
    onGrant: () -> Unit,
    onToggle: (Boolean, Float) -> Unit
) {
    var enabled by remember { mutableStateOf(false) }
    var intensity by remember { mutableFloatStateOf(0.42f) }
    var permission by remember { mutableStateOf(overlayAllowed()) }
    var entered by remember { mutableStateOf(false) }
    val intensityLabel = (intensity * 100).toInt()
    val switchGlow by animateFloatAsState(if (enabled) 1f else 0.25f, tween(650), label = "switchGlow")

    LaunchedEffect(Unit) {
        permission = overlayAllowed()
        entered = true
    }

    MaterialTheme(colorScheme = darkColorScheme(
        background = FxBlack, surface = FxPanel, primary = FxGold,
        onPrimary = FxBlack, onSurface = Color.White
    )) {
        Box(Modifier.fillMaxSize().background(FxBlack)) {
            AmbientBackground()
            Column(
                Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                    .verticalScroll(rememberScrollState()).padding(horizontal = 22.dp)
            ) {
                Spacer(Modifier.height(17.dp))
                AnimatedVisibility(
                    visible = entered,
                    enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { -24 }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("FAKEEXX  /  FX GROUP", color = FxGold, fontSize = 10.sp,
                                letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text("Privacy display", color = Color.White, fontSize = 27.sp,
                                fontWeight = FontWeight.SemiBold, letterSpacing = (-0.8).sp)
                        }
                        Box(
                            Modifier.size(46.dp).clip(RoundedCornerShape(15.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF28231A), FxPanel)))
                                .border(1.dp, FxGold.copy(alpha = 0.35f), RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("FX", color = FxGold, fontSize = 17.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))
                AnimatedVisibility(
                    visible = entered,
                    enter = fadeIn(tween(850, delayMillis = 100)) + scaleIn(tween(850, delayMillis = 100), initialScale = 0.96f)
                ) {
                    Column {
                        ShieldVisual(active = enabled, glow = switchGlow)
                        Spacer(Modifier.height(22.dp))
                        Surface(
                            color = FxPanel.copy(alpha = 0.96f),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.055f), RoundedCornerShape(25.dp))
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("SCREEN SHIELD", color = FxMuted, fontSize = 10.sp,
                                            letterSpacing = 2.2.sp, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(6.dp))
                                        AnimatedContent(targetState = enabled, label = "statusText") { active ->
                                            Text(
                                                if (active) "Protection active" else "Your screen, your space",
                                                color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = enabled,
                                        onCheckedChange = {
                                            enabled = it
                                            onToggle(it, intensity)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = FxBlack,
                                            checkedTrackColor = FxGold,
                                            uncheckedThumbColor = Color.LightGray,
                                            uncheckedTrackColor = Color(0xFF34383D)
                                        )
                                    )
                                }
                                Spacer(Modifier.height(22.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Privacy intensity", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Spacer(Modifier.weight(1f))
                                    Text("$intensityLabel%", color = FxGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = intensity,
                                    onValueChange = {
                                        intensity = it
                                        if (enabled) onToggle(true, it)
                                    },
                                    valueRange = 0.05f..0.85f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = FxGold, activeTrackColor = FxGold,
                                        inactiveTrackColor = Color(0xFF34383D)
                                    )
                                )
                                Text(
                                    "Adjust how dark the software privacy layer appears.",
                                    color = FxMuted, fontSize = 11.sp, lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(15.dp))
                AnimatedVisibility(visible = entered, enter = fadeIn(tween(900, delayMillis = 250)) + slideInVertically(tween(700)) { 30 }) {
                    Surface(
                        color = FxPanel.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(22.dp))
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(34.dp).clip(CircleShape).background(FxGold.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Security, null, tint = FxGold, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.width(11.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Overlay permission", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(if (permission) "Access granted" else "One-time setup required", color = FxMuted, fontSize = 11.sp)
                                }
                                Icon(if (permission) Icons.Default.CheckCircle else Icons.Default.ChevronRight,
                                    null, tint = if (permission) FxGold else FxMuted)
                            }
                            AnimatedVisibility(visible = !permission) {
                                Column {
                                    Spacer(Modifier.height(14.dp))
                                    Text("Android needs your permission to place the dimming layer above other apps.", color = FxMuted, fontSize = 12.sp, lineHeight = 18.sp)
                                    Spacer(Modifier.height(13.dp))
                                    Button(
                                        onClick = onGrant,
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FxGold, contentColor = FxBlack)
                                    ) {
                                        Text("Allow overlay access", fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(8.dp))
                                        Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(19.dp))
                Text(
                    "A SOFTWARE DIMMING LAYER — NOT A HARDWARE SIDE-VIEW FILTER.",
                    color = Color(0xFF686D73), fontSize = 9.sp, letterSpacing = 1.1.sp,
                    lineHeight = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(18.dp))
                Text("FX PRIVACY  •  BUILT FOR YOUR SPACE", color = Color(0xFF555A60),
                    fontSize = 9.sp, letterSpacing = 1.8.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(22.dp))
            }
        }
    }
}

@Composable
private fun ShieldVisual(active: Boolean, glow: Float) {
    val transition = rememberInfiniteTransition(label = "shieldMotion")
    val rotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(18000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "ringRotation"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.94f, targetValue = 1.035f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
        label = "breathing"
    )
    val pulse by animateFloatAsState(if (active) 1f else 0.55f, tween(700), label = "pulse")

    Box(
        Modifier.fillMaxWidth().height(208.dp).clip(RoundedCornerShape(27.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF191A19), Color(0xFF0D0F11), Color(0xFF11100D))))
            .border(1.dp, FxGold.copy(alpha = 0.18f), RoundedCornerShape(27.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(listOf(FxGold.copy(alpha = 0.16f * pulse), Color.Transparent), center = Offset(size.width * 0.5f, size.height * 0.47f), radius = size.minDimension * 0.48f),
                radius = size.minDimension * 0.48f,
                center = Offset(size.width * 0.5f, size.height * 0.47f)
            )
            drawCircle(Color.White.copy(alpha = 0.035f), radius = size.minDimension * 0.39f, center = Offset(size.width * 0.5f, size.height * 0.47f), style = Stroke(width = 1.dp.toPx()))
        }
        Box(Modifier.size(142.dp).graphicsLayer { rotationZ = rotation }.border(1.dp, FxGold.copy(alpha = 0.34f), CircleShape), contentAlignment = Alignment.TopCenter) {
            Box(Modifier.padding(top = 2.dp).size(7.dp).clip(CircleShape).background(FxGold))
        }
        Box(
            Modifier.size(112.dp).graphicsLayer { scaleX = breathe; scaleY = breathe }
                .shadow(28.dp, CircleShape, ambientColor = FxGold.copy(alpha = 0.16f), spotColor = FxGold.copy(alpha = 0.25f))
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF343027), Color(0xFF171715), Color(0xFF0A0B0C))))
                .border(1.dp, FxGold.copy(alpha = 0.55f * glow), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Shield, null, tint = FxGold.copy(alpha = 0.95f), modifier = Modifier.size(45.dp))
        }
        Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 17.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (active) "SHIELD ENGAGED" else "FX DISPLAY SHIELD", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.2.sp)
            Spacer(Modifier.height(4.dp))
            Text(if (active) "Your overlay is running" else "A quieter layer of privacy", color = FxMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun AmbientBackground() {
    val transition = rememberInfiniteTransition(label = "ambient")
    val drift by transition.animateFloat(
        initialValue = -0.08f, targetValue = 0.12f,
        animationSpec = infiniteRepeatable(tween(6500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ambientDrift"
    )
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(listOf(FxGold.copy(alpha = 0.075f), Color.Transparent), center = Offset(size.width * (0.88f + drift), size.height * 0.12f), radius = size.width * 0.72f),
            radius = size.width * 0.72f,
            center = Offset(size.width * (0.88f + drift), size.height * 0.12f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFF59616B).copy(alpha = 0.045f), Color.Transparent), center = Offset(size.width * 0.02f, size.height * 0.72f), radius = size.width * 0.62f),
            radius = size.width * 0.62f,
            center = Offset(size.width * 0.02f, size.height * 0.72f)
        )
    }
}
