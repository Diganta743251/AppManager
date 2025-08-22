// SPDX-License-Identifier: GPL-3.0-or-later

package io.github.muntashirakon.AppManager.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.muntashirakon.AppManager.BaseActivity
import io.github.muntashirakon.AppManager.R
import io.github.muntashirakon.AppManager.details.AppDetailsActivity
import io.github.muntashirakon.AppManager.main.ApplicationItem
import io.github.muntashirakon.AppManager.main.MainViewModel
import io.github.muntashirakon.AppManager.settings.Prefs
import io.github.muntashirakon.AppManager.ui.theme.AppTheme
import io.github.muntashirakon.AppManager.users.Users

class MainComposeActivity : BaseActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onAuthenticated(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        
        setContent {
            AppTheme(
                useDynamicColor = true,
                pureBlack = Prefs.Appearance.isPureBlackTheme()
            ) {
                MainScreen(
                    viewModel = viewModel,
                    onAppClick = { appItem ->
                        val intent = Intent(this, AppDetailsActivity::class.java).apply {
                            putExtra(AppDetailsActivity.EXTRA_PACKAGE_NAME, appItem.packageName)
                            putExtra(AppDetailsActivity.EXTRA_USER_HANDLE, Users.myUserId())
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onAppClick: (ApplicationItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var sortMode by remember { mutableStateOf(SortMode.NAME) }
    var showSystemApps by remember { mutableStateOf(false) }
    
    // Observe app list from ViewModel
    val appList by viewModel.applicationItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Filter and sort apps
    val filteredApps = remember(appList, searchQuery, showSystemApps, sortMode) {
        appList
            .filter { app ->
                if (!showSystemApps && app.isSystem) false
                else app.label.contains(searchQuery, ignoreCase = true) || 
                     app.packageName.contains(searchQuery, ignoreCase = true)
            }
            .sortedWith { a, b ->
                when (sortMode) {
                    SortMode.NAME -> a.label.compareTo(b.label, ignoreCase = true)
                    SortMode.PACKAGE_NAME -> a.packageName.compareTo(b.packageName)
                    SortMode.LAST_UPDATE -> b.lastUpdateTime.compareTo(a.lastUpdateTime)
                    SortMode.SIZE -> b.size.compareTo(a.size)
                }
            }
    }

    Scaffold(
        topBar = {
            DeviceGuardTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearchActive = isSearchActive,
                onSearchActiveChange = { isSearchActive = it },
                viewMode = viewMode,
                onViewModeChange = { viewMode = it },
                sortMode = sortMode,
                onSortModeChange = { sortMode = it },
                showSystemApps = showSystemApps,
                onShowSystemAppsChange = { showSystemApps = it }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Quick actions */ },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Quick Actions") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                LoadingScreen()
            } else {
                when (viewMode) {
                    ViewMode.LIST -> {
                        AppListView(
                            apps = filteredApps,
                            onAppClick = onAppClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    ViewMode.GRID -> {
                        AppGridView(
                            apps = filteredApps,
                            onAppClick = onAppClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceGuardTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    viewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit,
    sortMode: SortMode,
    onSortModeChange: (SortMode) -> Unit,
    showSystemApps: Boolean,
    onShowSystemAppsChange: (Boolean) -> Unit
) {
    var showSortMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { 
            if (!isSearchActive) {
                Text(
                    "DeviceGuard Pro",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            if (isSearchActive) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = { /* Handle search */ },
                    active = isSearchActive,
                    onActiveChange = onSearchActiveChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search apps...") }
                ) {
                    // Search suggestions can go here
                }
            } else {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                
                IconButton(onClick = { 
                    onViewModeChange(
                        if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                    )
                }) {
                    Icon(
                        if (viewMode == ViewMode.LIST) Icons.Default.GridView else Icons.Default.List,
                        contentDescription = "Toggle view"
                    )
                }
                
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort")
                }
                
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortMode.values().forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.displayName) },
                            onClick = {
                                onSortModeChange(mode)
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (sortMode == mode) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Show System Apps") },
                        onClick = { onShowSystemAppsChange(!showSystemApps) },
                        leadingIcon = {
                            if (showSystemApps) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun AppListView(
    apps: List<ApplicationItem>,
    onAppClick: (ApplicationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(apps, key = { it.packageName }) { app ->
            AppListItem(
                app = app,
                onClick = { onAppClick(app) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
fun AppGridView(
    apps: List<ApplicationItem>,
    onAppClick: (ApplicationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(160.dp),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(apps, key = { it.packageName }) { app ->
            AppGridItem(
                app = app,
                onClick = { onAppClick(app) }
            )
        }
    }
}

@Composable
fun AppListItem(
    app: ApplicationItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(app.icon)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // App Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (app.isSystem) {
                        AssistChip(
                            onClick = { },
                            label = { Text("System", style = MaterialTheme.typography.labelSmall) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        )
                    }
                    Text(
                        text = app.versionName ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Status indicators
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (app.isDisabled) {
                    Icon(
                        Icons.Default.Block,
                        contentDescription = "Disabled",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (app.hasBackup) {
                    Icon(
                        Icons.Default.Backup,
                        contentDescription = "Has backup",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AppGridItem(
    app: ApplicationItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(app.icon)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = app.label,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = app.versionName ?: "Unknown",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (app.isSystem) {
                AssistChip(
                    onClick = { },
                    label = { Text("System", style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading apps...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

enum class ViewMode {
    LIST, GRID
}

enum class SortMode(val displayName: String) {
    NAME("Name"),
    PACKAGE_NAME("Package Name"),
    LAST_UPDATE("Last Update"),
    SIZE("Size")
}