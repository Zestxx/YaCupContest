package com.zestxx.yacupcontest.composnents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zestxx.yacupcontest.ColorSelected
import com.zestxx.yacupcontest.FullPalletClick
import com.zestxx.yacupcontest.R
import com.zestxx.yacupcontest.UiAction
import com.zestxx.yacupcontest.ui.theme.Colors


@Composable
fun ColorPalette(
    state: PaletteState,
    onAction: (UiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom
    ) {
        AnimatedVisibility(
            state.isFullPaletteVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            ColorFullPalette(
                colors = palette,
                selectedColor = state.selectedColor,
                onAction = onAction
            )
        }
        Spacer(Modifier.height(8.dp))
        ColorShortPalette(
            colors = shortPalette,
            paletteState = state,
            onAction = onAction
        )
    }
}

@Composable
fun ColorFullPalette(
    colors: List<Color>,
    selectedColor: Color,
    onAction: (UiAction) -> Unit,
    modifier: Modifier = Modifier,
    cellSize: Dp = 32.dp,
    spacingVertical: Dp = 8.dp
) {
    val rows = colors.chunked(5)
    Column(
        modifier = modifier
            .background(Colors.Gray.copy(alpha = 0.5F), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        rows.forEach { rowColors ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacingVertical),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                rowColors.forEach { color ->
                    val borderColor = if (color.value == selectedColor.value) {
                        Colors.LimeGreen
                    } else {
                        color
                    }
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .background(color = color, shape = CircleShape)
                            .border(width = 2.dp, borderColor, shape = CircleShape)
                            .clickable(
                                onClick = { onAction.invoke(ColorSelected(color)) },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ColorShortPalette(
    colors: List<Color>,
    paletteState: PaletteState,
    onAction: (UiAction) -> Unit,
    modifier: Modifier = Modifier,
    cellSize: Dp = 32.dp,
    spacingVertical: Dp = 8.dp
) {
    Column(
        modifier = modifier
            .background(Colors.Gray.copy(alpha = 0.5F), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacingVertical),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Icon(
                painterResource(R.drawable.ic_palette),
                contentDescription = "Palette",
                tint = if (paletteState.isFullPaletteVisible) Colors.LimeGreen else Colors.White,
                modifier = Modifier
                    .clickable(
                        onClick = { onAction.invoke(FullPalletClick) },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
            )

            colors.forEach { color ->
                val borderColor = if (
                    !paletteState.isFullPaletteVisible &&
                    color.value == paletteState.selectedColor.value
                ) {
                    Colors.LimeGreen
                } else {
                    color
                }
                Box(
                    modifier = Modifier
                        .size(cellSize)
                        .clickable(
                            onClick = { onAction.invoke(ColorSelected(color)) },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                        .background(color = color, shape = CircleShape)
                        .border(width = 2.dp, borderColor, shape = CircleShape)
                )
            }
        }
    }
}

@Stable
data class PaletteState(
    val selectedColor: Color,
    val isFullPaletteVisible: Boolean,
)

@Preview
@Composable
private fun PalettePreview() {
//    ColorFullPalette(
//        palette,
//        onColorSelected = {},
//        modifier = Modifier
//            .wrapContentWidth()
//            .wrapContentHeight()
//    )
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ColorShortPalette(
            shortPalette,
            PaletteState(palette[2], false),
            {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private val shortPalette = listOf(
    Color(0xFFFFFFFF),
    Color(0xFFFF3D00),
    Color(0xFF000000),
    Color(0xFF1565C0),
)

private val palette = listOf(
    Color(0xFFFFFECC),
    Color(0xFF00C9FB),
    Color(0xFF4D21B2),
    Color(0xFF73A8FC),
    Color(0xFF75BB41),
    Color(0xFF94E4FD),
    Color(0xFF9747FF),
    Color(0xFFA8DB10),
    Color(0xFFB18CFE),
    Color(0xFFCCF3FF),
    Color(0xFFDC0057),
    Color(0xFFED746C),
    Color(0xFFEDCAFF),
    Color(0xFFF3ED00),
    Color(0xFFF8D3E3),
    Color(0xFFFA9A46),
    Color(0xFFFB66A4),
    Color(0xFFFC7600),
    Color(0xFFFF95D5),
    Color(0xFFFFD1A9),
    Color(0xFF4E7A25),
    Color(0xFF9D234C),
    Color(0xFFFF3D00),
    Color(0xFF641580),
    Color(0xFF1565C0),
)
