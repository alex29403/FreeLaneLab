package de.alexin.freelanelab

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.Text
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.content.edit

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            notificationsGranted.value = isGranted
        }

    private val notificationsGranted = mutableStateOf(false)
    private val overlayGranted = mutableStateOf(false)
    private val accessibilityGranted = mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        window.setNavigationBarContrastEnforced(false)

        setContent {
            val context = LocalContext.current

            overlayGranted.value = hasOverlayPermission(context)
            notificationsGranted.value = hasNotificationPermission(context)

            val enabled = overlayGranted.value && notificationsGranted.value && accessibilityGranted.value

            val isRunning by OverlayService.isRunning.collectAsState()


            var permissions by remember { mutableStateOf(false) }

            val infoSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
            var isInfoSheetOpen by rememberSaveable {
                mutableStateOf(false)
            }

            FreeLaneLabTheme {
                if (permissions) {
                    Permissions(context, notificationsGranted.value, overlayGranted.value, accessibilityGranted.value, onClose = {
                        permissions = false
                    }, onRequestNotificationPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                        }
                    }, onRequestOverlayPermission = {
                        if (!Settings.canDrawOverlays(context)) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                "package:${context.packageName}".toUri()
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    }, onRequestAccessibilityPermission = {
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    })
                } else {

                    if (isInfoSheetOpen) {
                        ModalBottomSheet(
                            sheetState = infoSheetState,
                            onDismissRequest = { isInfoSheetOpen = false },
                            contentWindowInsets = { WindowInsets(0) },
                            modifier = Modifier.statusBarsPadding()
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 30.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                            ) {
                                item {
                                    Text(
                                        text = resources.getString(R.string.info_title),
                                        style = typography.headlineMedium,
                                        color = colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = resources.getString(R.string.info_text),
                                        style = typography.bodyLarge,
                                        color = colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                    data = "mailto:support@alexin.de".toUri()
                                                }
                                                try {
                                                    context.startActivity(intent)
                                                } catch (e: ActivityNotFoundException) {
                                                    Toast.makeText(
                                                        context,
                                                        resources.getString(R.string.info_contact_failed),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    ) {
                                        Text(
                                            text = resources.getString(R.string.info_contact),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = colorScheme.onBackground
                                        )
                                        Icon(
                                            painter = painterResource(R.drawable.ic_arrow),
                                            contentDescription = null,
                                            tint = colorScheme.onBackground
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = resources.getString(R.string.info_privacy) + " http://www.alexin.de/information/datenschutz.php",
                                        style = typography.bodySmall,
                                        color = colorScheme.tertiary
                                    )
                                    Spacer(modifier = Modifier.height(0.dp))
                                    Text(
                                        text = resources.getString(R.string.info_imprint) + " http://www.alexin.de/information/impressum.php",
                                        style = typography.bodySmall,
                                        color = colorScheme.tertiary
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Special Thanks to Justin Smith!",
                                        style = typography.bodySmall,
                                        color = colorScheme.tertiary
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val packageInfo = packageManager.getPackageInfo(packageName, 0)
                                    val versionName = packageInfo.versionName
                                    Text(
                                        text = "Free Lane Labs - $versionName",
                                        style = typography.bodySmall,
                                        color = colorScheme.tertiary,
                                    )
                                    Spacer(modifier = Modifier.height(0.dp))
                                    Text(
                                        text = "01.03.2026",
                                        style = typography.bodySmall,
                                        color = colorScheme.tertiary
                                    )
                                    Spacer(modifier = Modifier.height(0.dp))
                                    Text(
                                        text = "by Alexander Helminger - alexin.de",
                                        style = typography.bodySmall,
                                        color = colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(text = "") },
                                actions = {
                                    IconButton(onClick = { isInfoSheetOpen = true }) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_info),
                                            contentDescription = "Brush"
                                        )
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = innerPadding.calculateTopPadding() + 10.dp,
                                bottom = innerPadding.calculateBottomPadding() + 10.dp
                            ),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Text(
                                    style = typography.displayMedium,
                                    color = colorScheme.primary,
                                    text = context.getString(R.string.hello_title)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Showreel()
                                Spacer(modifier = Modifier.height(10.dp))
                                val prefs =
                                    context.getSharedPreferences("LaneLabsSpeicher", MODE_PRIVATE)
                                val safety = prefs.getBoolean("confirmedSafety", false)
                                if (!enabled || !safety) {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                        style = typography.bodyMedium,
                                        color = colorScheme.onSurface,
                                        text = context.getString(R.string.hello_text_permissions),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(
                                        onClick = {
                                            val prefs = context.getSharedPreferences(
                                                "LaneLabsSpeicher",
                                                MODE_PRIVATE
                                            )
                                            prefs.edit {
                                                putBoolean("confirmedSafety", false)
                                            }
                                            permissions = true
                                        }
                                    ) {
                                        Text(text = context.getString(R.string.hello_grant_permissions))
                                    }
                                } else {
                                    if (!isRunning) {
                                        Text(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            style = typography.bodyMedium,
                                            color = colorScheme.onSurface,
                                            text = context.getString(R.string.hello_text),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Button(
                                            onClick = {
                                                context.startForegroundService(
                                                    Intent(
                                                        context,
                                                        OverlayService::class.java
                                                    )
                                                )
                                            }
                                        ) {
                                            Text(text = "Start Overlay")
                                        }
                                    } else {
                                        Text(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            style = typography.bodyMedium,
                                            color = colorScheme.onSurface,
                                            text = context.getString(R.string.hello_text_running),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Button(
                                            onClick = {
                                                context.stopService(
                                                    Intent(context, OverlayService::class.java)
                                                )
                                            }
                                        ) {
                                            Text(text = "Stop Overlay")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPermissionStatus()
    }

    fun refreshPermissionStatus() {
        overlayGranted.value = Settings.canDrawOverlays(this)
        accessibilityGranted.value =
            isAccessibilityServiceEnabled(MyAccessibilityService::class.java)
    }
}

@Composable
fun FreeLaneLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme =
        if (darkTheme) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}