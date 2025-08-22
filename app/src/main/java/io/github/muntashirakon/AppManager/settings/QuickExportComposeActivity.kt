package io.github.muntashirakon.AppManager.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.muntashirakon.AppManager.BaseActivity
import io.github.muntashirakon.AppManager.R
import io.github.muntashirakon.AppManager.rules.RulesTypeSelectionDialogFragment
import io.github.muntashirakon.AppManager.settings.Prefs
import io.github.muntashirakon.AppManager.ui.theme.AppTheme
import io.github.muntashirakon.AppManager.users.Users

class QuickExportComposeActivity : BaseActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onAuthenticated(savedInstanceState: Bundle?) {
        setContent {
            // Use system dark theme; pure black toggled via preference
            AppTheme(
                useDynamicColor = true,
                pureBlack = Prefs.Appearance.isPureBlackTheme()
            ) {
                QuickExportScreen(
                    onBack = { finish() },
                    onQuickExport = {
                        val fm = supportFragmentManager
                        try {
                            val dialog = RulesTypeSelectionDialogFragment
                                .newQuickExportInstance(null, null) // let fragment default to current user
                            dialog.show(fm, RulesTypeSelectionDialogFragment.TAG)
                        } catch (t: Throwable) {
                            io.github.muntashirakon.AppManager.utils.UIUtils.displayLongToast(R.string.export_failed)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickExportScreen(
    onBack: () -> Unit,
    onQuickExport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.pref_quick_export)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        QuickExportContent(padding, onQuickExport)
    }
}

@Composable
private fun QuickExportContent(
    padding: PaddingValues,
    onQuickExport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.pref_quick_export_msg),
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onQuickExport) {
            Text(text = stringResource(id = R.string.pref_quick_export))
        }
    }
}