package de.alexin.freelanelab

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun OverlayPointUi(label: String, lineStart: Boolean, onMove: (dx: Int, dy: Int) -> Unit) {
    FreeLaneLabTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!lineStart) {
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 10.dp)
                        .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.7f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 10.dp)
                        .background(Color.Green.copy(alpha = 0.7f))
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!lineStart) {
                    Box(
                        modifier = Modifier
                            .size(width = 10.dp, height = 2.dp)
                            .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.7f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 10.dp, height = 2.dp)
                            .background(Color.Green.copy(alpha = 0.7f))
                    )
                }
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                onMove(
                                    dragAmount.x.toInt(),
                                    dragAmount.y.toInt()
                                )
                            }
                        },
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(text = label, color = MaterialTheme.colorScheme.onPrimary)
                }
                if (!lineStart) {
                    Box(
                        modifier = Modifier
                            .size(width = 10.dp, height = 2.dp)
                            .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.7f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 10.dp, height = 2.dp)
                            .background(Color.Green.copy(alpha = 0.7f))
                    )
                }
            }
            if (!lineStart) {
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 10.dp)
                        .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.7f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 10.dp)
                        .background(Color.Green.copy(alpha = 0.7f))
                )
            }
        }
    }
}