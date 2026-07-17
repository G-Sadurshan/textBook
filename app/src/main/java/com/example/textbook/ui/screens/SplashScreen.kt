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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.8f) }
    
    LaunchedEffect(Unit) {
        // Logo and Text fade in
        alphaAnim.animateTo(1f, animationSpec = tween(1000))
        delay(1000)
        onTimeout()
    }
    
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
    }

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
        // Soft floating document illustrations and version control nodes in background
        SplashBackground()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnim.value).scale(scaleAnim.value)
        ) {
            // Large TextBook Logo
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
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
                text = "TextBook",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Modern Mobile Text Editor\nwith Incremental Version Control",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SplashBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Version control branching nodes (subtle lines)
        drawLine(
            color = Color.White.copy(alpha = 0.1f),
            start = Offset(0f, size.height * 0.3f),
            end = Offset(size.width, size.height * 0.4f),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.White.copy(alpha = 0.1f),
            start = Offset(size.width * 0.2f, size.height * 0.32f),
            end = Offset(size.width * 0.4f, size.height * 0.6f),
            strokeWidth = 2f
        )
        
        // Floating document circles (abstract)
        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = 150f,
            center = Offset(size.width * 0.8f, size.height * 0.15f)
        )
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.05f),
            radius = 100f,
            center = Offset(size.width * 0.15f, size.height * 0.85f)
        )
    }
}
