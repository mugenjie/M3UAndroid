package com.m3u.features.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.m3u.ui.components.basic.M3URow
import com.m3u.ui.local.LocalSpacing
import com.m3u.ui.local.LocalTheme

@Composable
internal fun SubscriptionItem(
    label: String,
    number: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    var elevation by remember { mutableStateOf(spacing.none) }
    Card(
        modifier = modifier
            .focusable()
            .onFocusChanged { state ->
                elevation = if (state.isFocused) {
                    spacing.small
                } else {
                    spacing.none
                }
            }
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ),
        shape = RoundedCornerShape(spacing.medium),
        backgroundColor = LocalTheme.current.surface,
        contentColor = LocalTheme.current.onSurface,
        elevation = elevation
    ) {
        M3URow(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(color = LocalTheme.current.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    color = LocalTheme.current.onPrimary,
                    text = number.toString(),
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    modifier = Modifier.padding(
                        start = spacing.small,
                        end = spacing.small,
                        bottom = 2.dp,
                    ),
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}