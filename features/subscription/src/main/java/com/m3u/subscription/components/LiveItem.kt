package com.m3u.subscription.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.m3u.data.entity.Live
import com.m3u.subscription.R
import com.m3u.ui.components.M3UImage
import com.m3u.ui.components.basic.M3UColumn
import com.m3u.ui.local.LocalSpacing
import com.m3u.ui.local.LocalTheme
import java.net.URI

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LiveItem(
    live: Live,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scheme = remember(live) {
        URI(live.url).scheme
            .orEmpty()
            .ifEmpty { context.getString(R.string.scheme_unknown) }
            .uppercase()
    }
    Card(
        shape = RectangleShape,
        backgroundColor = LocalTheme.current.surface,
        contentColor = LocalTheme.current.onSurface,
        modifier = modifier
            .focusable()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        M3UColumn {
            M3UImage(
                model = live.cover,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4 / 3f)
            )
            Text(
                text = live.title,
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LocalSpacing.current.extraSmall)
            ) {
                SchemeIcon(scheme = scheme)
                CompositionLocalProvider(
                    LocalContentAlpha provides 0.6f
                ) {
                    Text(
                        text = live.url,
                        maxLines = 1,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
