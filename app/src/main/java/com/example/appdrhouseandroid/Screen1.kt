package com.example.appdrhouseandroid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomBottomNavBar(
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit = {}
) {
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Color(0xFFFF5F7E),
                RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
    ) {
        // Elevated circle background
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItems.entries.forEachIndexed { index, item ->
                if (index == 2) {
                    Spacer(modifier = Modifier.width(70.dp))
                } else {
                    BottomNavItem(
                        item = item,
                        isSelected = selectedIndex == index,
                        actualIndex = index,
                        onItemClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onItemSelected(index)
                        }
                    )
                }
            }
        }

        // Animated Central FAB
        var fabScale by remember { mutableStateOf(1f) }
        val scaleAnimation = remember { Animatable(1f) }

        LaunchedEffect(fabScale) {
            scaleAnimation.animateTo(
                targetValue = fabScale,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        FloatingActionButton(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                fabScale = 0.8f
                onItemSelected(2)
            },
            containerColor = Color(0xFFFF5F7E),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(60.dp)
                .offset(y = (-20).dp)
                .scale(scaleAnimation.value)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Calendar",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    item: NavItems,
    isSelected: Boolean,
    actualIndex: Int,
    onItemClick: (Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = 24.dp),
                onClick = { onItemClick(actualIndex) }
            )
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )

        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = item.label,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

enum class NavItems(val icon: ImageVector, val label: String) {
    STATISTICS(Icons.Default.Tune, "Statistics"),
    DISCOVER(Icons.Default.LocationOn, "Discover"),
    CALENDAR(Icons.Default.CalendarToday, "Calendar"),
    CHAT(Icons.Default.Chat, "Chat"),
    PROFILE(Icons.Default.Person, "Profile")
}

@Preview(showBackground = true, backgroundColor = 0xFF2D2D2D)
@Composable
fun PreviewCustomBottomNavBar() {
    var selectedIndex by remember { mutableStateOf(0) }
    CustomBottomNavBar(
        selectedIndex = selectedIndex,
        onItemSelected = { selectedIndex = it }
    )
}