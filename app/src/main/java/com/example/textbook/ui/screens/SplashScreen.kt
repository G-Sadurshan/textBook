package com.example.textbook.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val transitionState = remember { MutableTransitionState(false) }
    
    LaunchedEffect(Unit) {
        transitionState.targetState = true
        delay(2500)
        onTimeout()
    }

    val transition = updateTransition(transitionState, label = "SplashTransition")
    
    val logoScale by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy) },
        label = "LogoScale"
    ) { if (it) 1f else 0.5f }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(1000) },
        label = "Alpha"
    ) { if (it) 1f else 0f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3B82F6), Color(0xFF7C3AED))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background elements
        BackgroundDecorations()

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Main Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(alpha),
                contentAlignment = Alignment.Center
            ) {
                // Simplification of the logo for splash
                Icon(
                    Icons.Rounded.MenuBook,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    Icons.Rounded.Code,
                    contentDescription = null,
                    tint = Color(0xFF06B6D4),
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "TEXTBOOK",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.alpha(alpha)
            )
            
            Text(
                text = "Modern Text Editor\nwith Incremental Version Control",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.alpha(alpha)
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Sequential feature icons
            Row(
                modifier = Modifier.alpha(alpha),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SplashFeatureItem("Smart Edit", Icons.Rounded.Edit)
                SplashFeatureItem("Kotlin", Icons.Rounded.Code)
                SplashFeatureItem("Markdown", Icons.Default.Description)
                SplashFeatureItem("History", Icons.Rounded.History)
            }
        }
    }
}

@Composable
fun BackgroundDecorations() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = 300f,
            center = Offset(size.width * 0.8f, size.height * 0.2f)
        )
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.05f),
            radius = 200f,
            center = Offset(size.width * 0.2f, size.height * 0.8f)
        )
    }
}

@Composable
fun SplashFeatureItem(label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
