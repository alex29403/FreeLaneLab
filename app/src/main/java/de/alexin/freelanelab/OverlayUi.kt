package de.alexin.freelanelab

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun OverlayUi(onClose: () -> Unit,
              onCloseMode: (num: Int) -> Unit,
              onLine:() -> Unit,
              onCurve:() -> Unit,
              onCircle:() -> Unit,
              onDrawLine:() -> Unit,
              onDrawCurve:() -> Unit,
              onDrawCircle:() -> Unit,
              onMove: (dx: Int, dy: Int) -> Unit) {
    FreeLaneLabTheme {
        Row {
            var expanded by remember { mutableStateOf(false) }
            var mode by remember { mutableStateOf("Menu") }
            Surface(
                modifier = Modifier.pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onMove(
                            dragAmount.x.toInt(),
                            dragAmount.y.toInt()
                        )
                    }
                },
                color = MaterialTheme.colorScheme.surfaceTint,
                shape = CircleShape,
                tonalElevation = 8.dp
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    if (!expanded) {
                        Icon(
                            painter = painterResource(R.drawable.ic_expand),
                            contentDescription = "expand"
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_collapse),
                            contentDescription = "collapse"
                        )
                    }
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.width(5.dp))
                Surface(
                    shape = CircleShape,
                    tonalElevation = 8.dp
                ) {
                    Row {
                        if (mode == "Line") {
                            IconButton(onClick = {
                                onCloseMode(2)
                                mode = "Menu"
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = "close"
                                )
                            }
                            IconButton(onClick = {
                                onDrawLine()
                                mode = "Menu"
                            } ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_play),
                                    contentDescription = "Draw Line"
                                )
                            }
                        } else if (mode == "Curve") {
                            IconButton(onClick = {
                                onCloseMode(3)
                                mode = "Menu"
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = "close"
                                )
                            }
                            IconButton(onClick = {
                                onDrawCurve()
                                mode = "Menu"
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_play),
                                    contentDescription = "Draw Curve"
                                )
                            }
                        } else if (mode == "Circle") {
                            IconButton(onClick = {
                                onCloseMode(3)
                                mode = "Menu"
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = "close"
                                )
                            }
                            IconButton(onClick = {
                                onDrawCircle()
                                mode = "Menu"
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_play),
                                    contentDescription = "Draw Circle"
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                onClose()
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close_windows),
                                    contentDescription = "close"
                                )
                            }
                            IconButton(onClick = {
                                onLine()
                                mode = "Line"
                            } ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_line),
                                    contentDescription = "Draw Line"
                                )
                            }
                            IconButton(onClick = {
                                onCurve()
                                mode = "Curve"
                            } ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_curve),
                                    contentDescription = "Draw Curve"
                                )
                            }
                            IconButton(onClick = {
                                onCircle()
                                mode = "Circle"
                            } ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_circle),
                                    contentDescription = "Draw Circle"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}