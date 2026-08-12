package com.pairlink.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pairlink.app.core.designsystem.DesignTokens

/**
 * Reusable Glassmorphism TextField with glowing border when focused.
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    isUnderlineOnly: Boolean = false,
    shape: Shape = RoundedCornerShape(DesignTokens.Radius.Medium)
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) DesignTokens.Colors.SoftPink else Color.White.copy(alpha = 0.20f),
        label = "BorderColor"
    )

    val backgroundColor = if (isUnderlineOnly) {
        Color.Transparent
    } else {
        if (isFocused) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f)
    }

    val boxModifier = if (isUnderlineOnly) {
        modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .padding(vertical = 4.dp)
    } else {
        modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isFocused) 8.dp else 2.dp,
                shape = shape,
                spotColor = if (isFocused) DesignTokens.Colors.SoftPink.copy(alpha = 0.4f) else Color.Transparent
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                border = BorderStroke(
                    width = if (isFocused) 1.5.dp else 1.dp,
                    color = borderColor
                ),
                shape = shape
            )
            .onFocusChanged { isFocused = it.isFocused }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = boxModifier,
        textStyle = TextStyle(
            color = DesignTokens.Colors.TextPrimary,
            fontSize = 15.sp
        ),
        cursorBrush = SolidColor(DesignTokens.Colors.PrimaryPink),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                leadingIcon?.let {
                    it()
                    Box(modifier = Modifier.padding(end = 12.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = DesignTokens.Colors.TextSecondary.copy(alpha = 0.5f),
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }
                trailingIcon?.let {
                    Box(modifier = Modifier.padding(start = 12.dp))
                    it()
                }
            }
        }
    )
}
