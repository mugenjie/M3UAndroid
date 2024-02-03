package com.m3u.features.setting

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.AnimatedPane
import androidx.compose.material3.adaptive.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.calculateListDetailPaneScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.m3u.core.architecture.pref.LocalPref
import com.m3u.core.util.basic.title
import com.m3u.data.database.model.ColorPack
import com.m3u.data.database.model.Stream
import com.m3u.features.setting.components.CanvasBottomSheet
import com.m3u.features.setting.fragments.AppearanceFragment
import com.m3u.features.setting.fragments.SubscriptionsFragment
import com.m3u.features.setting.fragments.preferences.PreferencesFragment
import com.m3u.i18n.R.string
import com.m3u.material.ktx.isTelevision
import com.m3u.material.model.LocalHazeState
import com.m3u.ui.DestinationEvent
import com.m3u.ui.EventBus
import com.m3u.ui.EventHandler
import com.m3u.ui.ResumeEvent
import com.m3u.ui.helper.LocalHelper
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock

@Composable
fun SettingRoute(
    resume: ResumeEvent,
    contentPadding: PaddingValues,
    navigateToAbout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val tv = isTelevision()

    val title = stringResource(string.ui_title_setting)
    val controller = LocalSoftwareKeyboardController.current

    val state by viewModel.state.collectAsStateWithLifecycle()
    val packs by viewModel.packs.collectAsStateWithLifecycle()
    val banneds by viewModel.banneds.collectAsStateWithLifecycle()
    val backingUpOrRestoring by viewModel.backingUpOrRestoring.collectAsStateWithLifecycle()

    val helper = LocalHelper.current

    val sheetState = rememberModalBottomSheetState()
    var colorInt: Int? by remember { mutableStateOf(null) }
    var isDark: Boolean? by remember { mutableStateOf(null) }

    val createDocumentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/*")) { uri ->
            uri?: return@rememberLauncherForActivityResult
            viewModel.backup(uri)
        }
    val openDocumentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?: return@rememberLauncherForActivityResult
            viewModel.restore(uri)
        }

    val backup = {
        val filename = "Backup_${Clock.System.now().nanosecondsOfSecond}.txt"
        createDocumentLauncher.launch(filename)
    }
    val restore = {
        openDocumentLauncher.launch(arrayOf("text/*"))
    }

    EventHandler(resume) {
        helper.deep = 0
        helper.title = title.title()
        helper.actions = persistentListOf()
    }

    Box {
        SettingScreen(
            contentPadding = contentPadding,
            versionName = state.versionName,
            versionCode = state.versionCode,
            title = state.title,
            url = state.url,
            backingUpOrRestoring = backingUpOrRestoring,
            uriWrapper = rememberUriWrapper(state.uri),
            banneds = banneds,
            onTitle = { viewModel.onEvent(SettingEvent.OnTitle(it)) },
            onUrl = { viewModel.onEvent(SettingEvent.OnUrl(it)) },
            onSubscribe = {
                controller?.hide()
                viewModel.onEvent(SettingEvent.Subscribe)
            },
            onBanned = { viewModel.onEvent(SettingEvent.OnBanned(it)) },
            navigateToAbout = navigateToAbout,
            localStorage = state.localStorage,
            onLocalStorage = { viewModel.onEvent(SettingEvent.OnLocalStorage) },
            openDocument = { viewModel.onEvent(SettingEvent.OpenDocument(it)) },
            backup = backup,
            restore = restore,
            onClipboard = { viewModel.onClipboard(it) },
            packs = packs,
            openColorCanvas = { c, i ->
                colorInt = c
                isDark = i
            },
            modifier = modifier.fillMaxSize()
        )
        if (!tv) {
            CanvasBottomSheet(
                sheetState = sheetState,
                colorInt = colorInt,
                isDark = isDark,
                onDismissRequest = {
                    colorInt = null
                    isDark = null
                }
            )
        }
    }
}

@Composable
private fun SettingScreen(
    contentPadding: PaddingValues,
    versionName: String,
    versionCode: Int,
    title: String,
    url: String,
    uriWrapper: UriWrapper,
    backingUpOrRestoring: Boolean,
    onTitle: (String) -> Unit,
    onUrl: (String) -> Unit,
    onSubscribe: () -> Unit,
    banneds: ImmutableList<Stream>,
    onBanned: (Int) -> Unit,
    navigateToAbout: () -> Unit,
    localStorage: Boolean,
    onLocalStorage: () -> Unit,
    openDocument: (Uri) -> Unit,
    backup: () -> Unit,
    restore: () -> Unit,
    onClipboard: (String) -> Unit,
    packs: ImmutableList<ColorPack>,
    openColorCanvas: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val helper = LocalHelper.current
    val pref = LocalPref.current

    val defaultTitle = stringResource(string.ui_title_setting)
    val playlistTitle = stringResource(string.feat_setting_playlist_management)
    val appearanceTitle = stringResource(string.feat_setting_appearance)

    val colorArgb = pref.colorArgb

    var fragment: DestinationEvent.Setting by remember {
        mutableStateOf(DestinationEvent.Setting.Default)
    }

    EventHandler(EventBus.setting) {
        fragment = it
    }

    LaunchedEffect(fragment) {
        helper.title = when (fragment) {
            DestinationEvent.Setting.Default -> defaultTitle
            DestinationEvent.Setting.Playlists -> playlistTitle
            DestinationEvent.Setting.Appearance -> appearanceTitle
        }.title()
        helper.deep = when (fragment) {
            DestinationEvent.Setting.Default -> 0
            else -> 1
        }
    }

    val currentPaneDestination by remember {
        derivedStateOf {
            when (fragment) {
                DestinationEvent.Setting.Default -> ListDetailPaneScaffoldRole.List
                else -> ListDetailPaneScaffoldRole.Detail
            }
        }
    }
    val scaffoldState = calculateListDetailPaneScaffoldState(
        currentPaneDestination = currentPaneDestination
    )

    ListDetailPaneScaffold(
        scaffoldState = scaffoldState,
        windowInsets = WindowInsets(0),
        listPane = {
            PreferencesFragment(
                fragment = fragment,
                contentPadding = contentPadding,
                versionName = versionName,
                versionCode = versionCode,
                navigateToPlaylistManagement = {
                    fragment = DestinationEvent.Setting.Playlists
                },
                navigateToThemeSelector = {
                    fragment = DestinationEvent.Setting.Appearance
                },
                navigateToAbout = navigateToAbout,
                modifier = Modifier.fillMaxSize()
            )
        },
        detailPane = {
            if (fragment != DestinationEvent.Setting.Default) {
                AnimatedPane(Modifier) {
                    when (fragment) {
                        DestinationEvent.Setting.Playlists -> {
                            SubscriptionsFragment(
                                contentPadding = contentPadding,
                                title = title,
                                url = url,
                                uriWrapper = uriWrapper,
                                backingUpOrRestoring = backingUpOrRestoring,
                                banneds = banneds,
                                onBanned = onBanned,
                                onTitle = onTitle,
                                onUrl = onUrl,
                                onSubscribe = onSubscribe,
                                localStorage = localStorage,
                                onLocalStorage = onLocalStorage,
                                openDocument = openDocument,
                                onClipboard = onClipboard,
                                backup = backup,
                                restore = restore,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        DestinationEvent.Setting.Appearance -> {
                            AppearanceFragment(
                                packs = packs,
                                colorArgb = colorArgb,
                                openColorCanvas = openColorCanvas,
                                contentPadding = contentPadding
                            )
                        }

                        else -> {}
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .haze(
                LocalHazeState.current,
                HazeDefaults.style(MaterialTheme.colorScheme.surface)
            )
            .testTag("feature:setting")
    )

    BackHandler(fragment != DestinationEvent.Setting.Default) {
        fragment = DestinationEvent.Setting.Default
    }
}
