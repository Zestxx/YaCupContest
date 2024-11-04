package com.zestxx.yacupcontest.composnents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zestxx.yacupcontest.AddNewClick
import com.zestxx.yacupcontest.DeleteClick
import com.zestxx.yacupcontest.LayersClick
import com.zestxx.yacupcontest.PlayClick
import com.zestxx.yacupcontest.R
import com.zestxx.yacupcontest.RedoClick
import com.zestxx.yacupcontest.StopClick
import com.zestxx.yacupcontest.UiAction
import com.zestxx.yacupcontest.UndoClick
import com.zestxx.yacupcontest.ui.theme.Colors

@Composable
fun ActionsHeader(
    modifier: Modifier = Modifier,
    state: ActionHeaderState,
    onAction: (UiAction) -> Unit
) {
    Box(modifier) {
        val itemsModifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1F)

        AnimatedVisibility(state.isVisible) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                horizontalArrangement = spacedBy(8.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_undo),
                    contentDescription = "Undo",
                    modifier = itemsModifier.clickable { onAction.invoke(UndoClick) },
                    tint = if (state.isUndoEnabled) Colors.White else Colors.Gray,
                )
                Icon(
                    painterResource(R.drawable.ic_redo),
                    contentDescription = "Redo",
                    modifier = itemsModifier.clickable { onAction.invoke(RedoClick) },
                    tint = if (state.isRedoEnabled) Colors.White else Colors.Gray
                )
            }
        }

        AnimatedVisibility(
            state.isVisible,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .align(Alignment.Center),
        ) {
            Row(horizontalArrangement = spacedBy(16.dp)) {
                Icon(
                    painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete",
                    modifier = itemsModifier.clickable { onAction.invoke(DeleteClick) },
                    tint = Colors.White
                )
                Icon(
                    painterResource(R.drawable.ic_add_new),
                    contentDescription = "Add",
                    modifier = itemsModifier.clickable { onAction.invoke(AddNewClick) },
                    tint = Colors.White

                )
                Icon(
                    painterResource(R.drawable.ic_layers),
                    contentDescription = "Layers",
                    modifier = itemsModifier.clickable { onAction.invoke(LayersClick) },
                    tint = Colors.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .align(Alignment.TopEnd),
            horizontalArrangement = spacedBy(8.dp)
        ) {
            AnimatedVisibility(
                state.isVisible,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut(),
            ) {
                Icon(
                    painterResource(R.drawable.ic_play),
                    contentDescription = "Play",
                    modifier = itemsModifier.clickable(
                        onClick = { onAction.invoke(PlayClick) },
                        enabled = state.isPlayEnabled
                    ),
                    tint = if (state.isPlayEnabled) Colors.White else Colors.Gray
                )
            }

            Icon(
                painterResource(R.drawable.ic_stop),
                contentDescription = "Stop",
                modifier = itemsModifier.clickable(
                    onClick = { onAction.invoke(StopClick) },
                    enabled = state.isStopEnabled
                ),
                tint = if (state.isStopEnabled) Colors.White else Colors.Gray
            )
        }
    }
}

@Stable
data class ActionHeaderState(
    val isUndoEnabled: Boolean,
    val isRedoEnabled: Boolean,
    val isPlayEnabled: Boolean,
    val isStopEnabled: Boolean,
    val isVisible: Boolean = true
)

@Preview
@Composable
private fun ActionsHeaderPreview() {
    ActionsHeader(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        state = ActionHeaderState(
            true,
            false,
            true,
            false
        ),
        onAction = {}
    )
}