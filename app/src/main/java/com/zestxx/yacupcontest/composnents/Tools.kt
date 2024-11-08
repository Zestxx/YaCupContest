package com.zestxx.yacupcontest.composnents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zestxx.yacupcontest.R
import com.zestxx.yacupcontest.state.ColorsClick
import com.zestxx.yacupcontest.state.EraserClick
import com.zestxx.yacupcontest.state.PencilClick
import com.zestxx.yacupcontest.state.UiAction
import com.zestxx.yacupcontest.theme.AppTheme
import com.zestxx.yacupcontest.ui.theme.Palette

@Composable
fun Tools(state: ToolsState, onAction: (UiAction) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier) {
        val itemsModifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1F)
        AnimatedVisibility(
            state.isVisible,
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.Center),
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Row(horizontalArrangement = spacedBy(8.dp)) {
                Icon(
                    painterResource(R.drawable.ic_pencil),
                    contentDescription = "Pencil",
                    modifier = itemsModifier.clickable { onAction.invoke(PencilClick) },
                    tint = if (state.selectedTool == Tool.Pencil) {
                        AppTheme.color.selectedTint
                    } else {
                        AppTheme.color.iconTint
                    }
                )
                Icon(
                    painterResource(R.drawable.ic_eraser),
                    contentDescription = "Eraser",
                    modifier = itemsModifier.clickable { onAction.invoke(EraserClick) },
                    tint = if (state.selectedTool == Tool.Eraser) {
                        AppTheme.color.selectedTint
                    } else {
                        AppTheme.color.iconTint
                    }
                )
                Box(
                    itemsModifier
                        .padding(4.dp)
                        .background(color = state.selectedColor, shape = CircleShape)
                        .border(
                            width = 1.dp,
                            color = if (state.selectedTool == Tool.Colors) {
                                AppTheme.color.selectedTint
                            } else {
                                state.selectedColor
                            },
                            shape = CircleShape
                        )
                        .clickable { onAction.invoke(ColorsClick) }
                )
            }
        }
    }
}

@Stable
data class ToolsState(
    val selectedColor: Color,
    val selectedTool: Tool = Tool.Pencil,
    val isVisible: Boolean = true
)

sealed interface Tool {
    data object Pencil : Tool
    data object Brush : Tool
    data object Eraser : Tool
    data object Shapes : Tool
    data object Colors : Tool
}

@Preview
@Composable
private fun ToolsPreview() {
    Tools(
        state = ToolsState(Palette.Orange, Tool.Shapes),
        onAction = {},
        Modifier
            .fillMaxWidth()
            .height(32.dp)
    )
}