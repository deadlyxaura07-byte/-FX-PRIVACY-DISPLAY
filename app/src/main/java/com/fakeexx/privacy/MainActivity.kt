package com.fakeexx.privacy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

private val Ink = Color(0xFF08090B)
private val Panel = Color(0xFF15171B)
private val Accent = Color(0xFFC6A66B)

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent {
   FxPrivacyScreen(
    overlayAllowed = { Settings.canDrawOverlays(this) },
    onGrant = { if (!Settings.canDrawOverlays(this)) startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) },
    onToggle = { active, amount ->
     if (active) {
      if (Settings.canDrawOverlays(this)) ContextCompat.startForegroundService(this, Intent(this, PrivacyOverlayService::class.java).putExtra("intensity", amount))
      else startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
     } else stopService(Intent(this, PrivacyOverlayService::class.java))
    }
   )
  }
 }
}

@Composable
private fun FxPrivacyScreen(overlayAllowed: () -> Boolean, onGrant: () -> Unit, onToggle: (Boolean, Float) -> Unit) {
 var enabled by remember { mutableStateOf(false) }
 var intensity by remember { mutableFloatStateOf(0.42f) }
 var permission by remember { mutableStateOf(overlayAllowed()) }
 LaunchedEffect(Unit) { permission = overlayAllowed() }
 MaterialTheme(colorScheme = darkColorScheme(background = Ink, surface = Panel, primary = Accent)) {
  Surface(Modifier.fillMaxSize(), color = Ink) {
   Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(22.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
     Column(Modifier.weight(1f)) {
      Text("FAKEEXX / FX GROUP", color = Accent, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
      Text("PRIVACY DISPLAY", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
     }
     Surface(color = Panel, shape = RoundedCornerShape(14.dp)) { Icon(Icons.Default.Shield, null, tint = Accent, modifier = Modifier.padding(13.dp).size(25.dp)) }
    }
    Spacer(Modifier.height(30.dp))
    Surface(color = Panel, shape = RoundedCornerShape(26.dp), modifier = Modifier.fillMaxWidth()) {
     Column(Modifier.padding(22.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
       Column(Modifier.weight(1f)) {
        Text("SCREEN SHIELD", color = Color.LightGray, fontSize = 11.sp, letterSpacing = 1.8.sp)
        Text(if (enabled) "Protection active" else "Protection is off", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
       }
       Switch(checked = enabled, onCheckedChange = { enabled = it; onToggle(it, intensity) })
      }
      Spacer(Modifier.height(20.dp))
      Box(Modifier.fillMaxWidth().height(118.dp).background(Color(0xFF090A0C), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
       Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.VisibilityOff, null, tint = Accent, modifier = Modifier.size(30.dp))
        Spacer(Modifier.height(8.dp))
        Text("FX", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 5.sp)
        Text("DISPLAY PRIVACY LAYER", color = Color.Gray, fontSize = 9.sp, letterSpacing = 2.sp)
       }
      }
      Spacer(Modifier.height(18.dp))
      Text("Overlay intensity  ·  ${(intensity * 100).toInt()}%", color = Color.White, fontSize = 13.sp)
      Slider(value = intensity, onValueChange = { intensity = it; if (enabled) onToggle(true, it) }, valueRange = 0.05f..0.85f)
      Text("Higher intensity makes the screen darker. This is a software overlay, not a true side-angle hardware filter.", color = Color.Gray, fontSize = 11.sp, lineHeight = 16.sp)
     }
    }
    Spacer(Modifier.height(18.dp))
    Surface(color = Panel, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
     Column(Modifier.padding(19.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
       Icon(Icons.Default.Security, null, tint = Accent)
       Spacer(Modifier.width(10.dp))
       Text("Required permission", color = Color.White, fontWeight = FontWeight.SemiBold)
      }
      Spacer(Modifier.height(8.dp))
      Text(if (permission) "Display over other apps is allowed." else "Allow FX Privacy to draw over other apps to show the privacy layer.", color = Color.Gray, fontSize = 12.sp)
      if (!permission) { Spacer(Modifier.height(12.dp)); Button(onClick = onGrant, colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Ink)) { Text("Grant overlay access") } }
     }
    }
    Spacer(Modifier.weight(1f))
    Text("FX PRIVACY  •  VERSION 1.0.0", color = Color.DarkGray, fontSize = 10.sp, letterSpacing = 1.4.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
   }
  }
 }
}