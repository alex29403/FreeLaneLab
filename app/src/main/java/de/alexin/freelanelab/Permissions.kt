package de.alexin.freelanelab

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Permissions(context: Context, notificationsEnabled: Boolean, overlayEnabled: Boolean, accesibilityEnabled: Boolean, onRequestNotificationPermission: () -> Unit, onRequestOverlayPermission: () -> Unit, onRequestAccessibilityPermission: () -> Unit, onClose: () -> Unit) {
    val allEnabled = notificationsEnabled && overlayEnabled && accesibilityEnabled
    val permissionsDone = remember { mutableStateOf(false) }

    BackHandler {
        onClose()
    }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = context.getString(R.string.setup_title)) },
                    actions = {
                        IconButton(onClick = { onClose() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = "Brush"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = padding.calculateTopPadding() + 10.dp,
                    bottom = padding.calculateBottomPadding() + 10.dp
                ),
                verticalArrangement = Arrangement.Center
            ) {
                if (!permissionsDone.value) {
                    item {
                        Column {
                            Icon(
                                modifier = Modifier.size(60.dp).align(Alignment.CenterHorizontally),
                                painter = painterResource(id = R.drawable.ic_agree),
                                tint = Color.Gray,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                modifier = Modifier.padding(horizontal = 30.dp).fillMaxWidth(),
                                text = context.resources.getString(R.string.permissions_text),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Row(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = 5.dp,
                                            bottomEnd = 5.dp
                                        )
                                    )
                                    .clickable(enabled = !notificationsEnabled) {
                                        onRequestNotificationPermission()
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(15.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = context.resources.getString(R.string.permissions_notifications_title),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = context.resources.getString(R.string.permissions_notifications_text),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                                if (notificationsEnabled) {
                                    Icon(
                                        modifier = Modifier.padding(end = 8.dp).size(30.dp)
                                            .align(Alignment.CenterVertically),
                                        painter = painterResource(id = R.drawable.ic_approved),
                                        tint = Color(
                                            red = Color.Green.red * 0.7f,
                                            green = Color.Green.green * 0.7f,
                                            blue = Color.Green.blue * 0.7f,
                                            alpha = 1f
                                        ),
                                        contentDescription = null,
                                    )
                                } else {
                                    Icon(
                                        modifier = Modifier.size(40.dp)
                                            .align(Alignment.CenterVertically),
                                        painter = painterResource(id = R.drawable.ic_chevron),
                                        tint = Color.Gray,
                                        contentDescription = null,
                                    )
                                }
                            }
                            HorizontalDivider(
                                thickness = 3.dp,
                                color = MaterialTheme.colorScheme.surface
                            )
                            Row(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 5.dp,
                                            topEnd = 5.dp,
                                            bottomStart = 5.dp,
                                            bottomEnd = 5.dp
                                        )
                                    )
                                    .clickable(enabled = !overlayEnabled) {
                                        onRequestOverlayPermission()
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(15.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = context.resources.getString(R.string.permissions_overlay_title),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = context.resources.getString(R.string.permissions_overlay_text),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                                if (overlayEnabled) {
                                    Icon(
                                        modifier = Modifier.padding(end = 8.dp).size(30.dp)
                                            .align(Alignment.CenterVertically),
                                        painter = painterResource(id = R.drawable.ic_approved),
                                        tint = Color(
                                            red = Color.Green.red * 0.7f,
                                            green = Color.Green.green * 0.7f,
                                            blue = Color.Green.blue * 0.7f,
                                            alpha = 1f
                                        ),
                                        contentDescription = null,
                                    )
                                } else {
                                    Icon(
                                        modifier = Modifier.size(40.dp)
                                            .align(Alignment.CenterVertically),
                                        painter = painterResource(id = R.drawable.ic_chevron),
                                        tint = Color.Gray,
                                        contentDescription = null,
                                    )
                                }
                            }
                            HorizontalDivider(
                                thickness = 3.dp,
                                color = MaterialTheme.colorScheme.surface
                            )
                            Row(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 5.dp,
                                            topEnd = 5.dp,
                                            bottomStart = 16.dp,
                                            bottomEnd = 16.dp
                                        )
                                    )
                                    .clickable(enabled = !accesibilityEnabled) {
                                        onRequestAccessibilityPermission()
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(15.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = context.resources.getString(R.string.permissions_accessibility_title),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = context.resources.getString(R.string.permissions_accessibility_text),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                                if (accesibilityEnabled) {
                                    Icon(
                                        modifier = Modifier.padding(end = 8.dp).size(30.dp)
                                            .align(Alignment.CenterVertically),
                                        painter = painterResource(id = R.drawable.ic_approved),
                                        tint = Color(
                                            red = Color.Green.red * 0.7f,
                                            green = Color.Green.green * 0.7f,
                                            blue = Color.Green.blue * 0.7f,
                                            alpha = 1f
                                        ),
                                        contentDescription = null,
                                    )
                                } else {
                                    Icon(
                                        modifier = Modifier.size(40.dp)
                                            .align(Alignment.CenterVertically),
                                        painter = painterResource(id = R.drawable.ic_chevron),
                                        tint = Color.Gray,
                                        contentDescription = null,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                            Button(
                                enabled = allEnabled,
                                onClick = {
                                    permissionsDone.value = true
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = context.getString(R.string.permissions_confirm))
                            }
                        }
                    }
                } else {
                    item {
                        Column {
                            Icon(
                                modifier = Modifier.size(60.dp).align(Alignment.CenterHorizontally),
                                painter = painterResource(id = R.drawable.ic_warning),
                                tint = Color.Gray,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                modifier = Modifier.padding(horizontal = 30.dp).fillMaxWidth(),
                                text = context.resources.getString(R.string.warnings_text),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Row(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = 5.dp,
                                            bottomEnd = 5.dp
                                        )
                                    )
                                    .clickable(enabled = !notificationsEnabled) {
                                        onRequestNotificationPermission()
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(15.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = context.resources.getString(R.string.warnings_gestures_title),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = context.resources.getString(R.string.warnings_gestures_text),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                            HorizontalDivider(
                                thickness = 3.dp,
                                color = MaterialTheme.colorScheme.surface
                            )
                            Row(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 5.dp,
                                            topEnd = 5.dp,
                                            bottomStart = 5.dp,
                                            bottomEnd = 5.dp
                                        )
                                    )
                                    .clickable(enabled = !overlayEnabled) {
                                        onRequestOverlayPermission()
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(15.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = context.resources.getString(R.string.warnings_stop_title),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = context.resources.getString(R.string.warnings_stop_text),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                            HorizontalDivider(
                                thickness = 3.dp,
                                color = MaterialTheme.colorScheme.surface
                            )
                            Row(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 5.dp,
                                            topEnd = 5.dp,
                                            bottomStart = 16.dp,
                                            bottomEnd = 16.dp
                                        )
                                    )
                                    .clickable(enabled = !accesibilityEnabled) {
                                        onRequestAccessibilityPermission()
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(15.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = context.resources.getString(R.string.warnings_apps_title),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = context.resources.getString(R.string.warnings_apps_text),
                                        textAlign = TextAlign.Left,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                            Button(
                                enabled = allEnabled,
                                onClick = {
                                    val prefs = context.getSharedPreferences("LaneLabsSpeicher", MODE_PRIVATE)
                                    prefs.edit { putBoolean("confirmedSafety", true) }

                                    onClose()
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = context.getString(R.string.warnings_confirm))
                            }
                        }
                    }
            }
        }
    }
}