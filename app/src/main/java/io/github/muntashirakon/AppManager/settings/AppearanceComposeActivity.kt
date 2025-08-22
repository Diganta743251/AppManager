// SPDX-License-Identifier: GPL-3.0-or-later

package io.github.muntashirakon.AppManager.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.github.muntashirakon.AppManager.BaseActivity
import io.github.muntashirakon.AppManager.R
import io.github.muntashirakon.AppManager.ui.theme.AppTheme

class AppearanceComposeActivity : BaseActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onAuthenticated(savedInstanceState: Bundle?) {
        setContent {
            AppTheme(
                useDynamicColor = true,
                pureBlack = Prefs.Appearance.isPureBlackTheme()
            ) {
                AppearanceScreen(
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    onBack: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(getCurrentTheme()) }
    var useDynamicColors by remember { mutableStateOf(Prefs.Appearance.isFollowSystemTheme()) }
    var usePureBlack by remember { mutableStateOf(Prefs.Appearance.isPureBlackTheme()) }
    var useCustomAccent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DeviceGuard Pro - Appearance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Selection Section
            item {
                PreferenceSection(
                    title = "Theme",
                    icon = Icons.Default.Palette
                ) {
                    ThemeSelectionCard(
                        selectedTheme = selectedTheme,
                        onThemeSelected = { theme ->
                            selectedTheme = theme
                            applyTheme(theme)
                        }
                    )
                }
            }

            // Dynamic Colors Section
            item {
                PreferenceSection(
                    title = "Colors",
                    icon = Icons.Default.ColorLens
                ) {
                    SwitchPreferenceCard(
                        title = "Dynamic Colors",
                        subtitle = "Use colors from your wallpaper",
                        checked = useDynamicColors,
                        onCheckedChange = { 
                            useDynamicColors = it
                            Prefs.Appearance.setFollowSystemTheme(it)
                        }
                    )
                    
                    if (selectedTheme == ThemeMode.DARK) {
                        SwitchPreferenceCard(
                            title = "Pure Black Theme",
                            subtitle = "Use pure black for AMOLED displays",
                            checked = usePureBlack,
                            onCheckedChange = { 
                                usePureBlack = it
                                Prefs.Appearance.setPureBlackTheme(it)
                            }
                        )
                    }
                }
            }

            // DeviceGuard Pro Branding Section
            item {
                PreferenceSection(
                    title = "DeviceGuard Pro",
                    icon = Icons.Default.Security
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Modern App Management",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Experience the next generation of Android app management with DeviceGuard Pro's modern interface and enhanced security features.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreferenceSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        content()
    }
}

@Composable
fun ThemeSelectionCard(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(16.dp)
        ) {
            ThemeMode.values().forEach { theme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (theme == selectedTheme),
                            onClick = { onThemeSelected(theme) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (theme == selectedTheme),
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = theme.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SwitchPreferenceCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

enum class ThemeMode(val displayName: String, val description: String) {
    LIGHT("Light", "Always use light theme"),
    DARK("Dark", "Always use dark theme"),
    SYSTEM("Follow System", "Use system theme setting")
}

private fun getCurrentTheme(): ThemeMode {
    return when {
        Prefs.Appearance.isFollowSystemTheme() -> ThemeMode.SYSTEM
        Prefs.Appearance.isDarkTheme() -> ThemeMode.DARK
        else -> ThemeMode.LIGHT
    }
}

private fun applyTheme(theme: ThemeMode) {
    when (theme) {
        ThemeMode.LIGHT -> {
            Prefs.Appearance.setFollowSystemTheme(false)
            Prefs.Appearance.setDarkTheme(false)
        }
        ThemeMode.DARK -> {
            Prefs.Appearance.setFollowSystemTheme(false)
            Prefs.Appearance.setDarkTheme(true)
        }
        ThemeMode.SYSTEM -> {
            Prefs.Appearance.setFollowSystemTheme(true)
        }
    }
}