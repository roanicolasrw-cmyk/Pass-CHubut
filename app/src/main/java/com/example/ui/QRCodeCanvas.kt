package com.example.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun QRCodeCanvas(
    data: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    qrColor: Color = MaterialTheme.colorScheme.onSurface,
    showLaser: Boolean = true
) {
    // Laser scanning animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserYProgress by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = this.size.width
            val gridSize = 19 // 19x19 grid
            val cellSize = canvasSize / gridSize

            // Generate deterministic matrix based on data hash
            val hash = data.hashCode()
            val seedRandom = java.util.Random(hash.toLong())

            val matrix = Array(gridSize) { x ->
                BooleanArray(gridSize) { y ->
                    // Always true or false according to some deterministic pattern
                    seedRandom.nextBoolean()
                }
            }

            // Draw Finder Patterns (The 3 eyes in top-left, top-right, and bottom-left)
            // Each finder pattern is 7x7 cells
            fun applyFinderPattern(offsetX: Int, offsetY: Int) {
                for (dx in 0 until 7) {
                    for (dy in 0 until 7) {
                        val isOuterBorder = dx == 0 || dx == 6 || dy == 0 || dy == 6
                        val isInnerCenter = dx in 2..4 && dy in 2..4
                        val isWhiteRing = (dx == 1 || dx == 5 || dy == 1 || dy == 5) && !isInnerCenter

                        val px = offsetX + dx
                        val py = offsetY + dy

                        if (px in 0 until gridSize && py in 0 until gridSize) {
                            matrix[px][py] = isOuterBorder || isInnerCenter
                        }
                    }
                }
            }

            // Apply the three eyes
            applyFinderPattern(0, 0) // Top Left
            applyFinderPattern(gridSize - 7, 0) // Top Right
            applyFinderPattern(0, gridSize - 7) // Bottom Left

            // Draw the matrix
            for (x in 0 until gridSize) {
                for (y in 0 until gridSize) {
                    if (matrix[x][y]) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(x * cellSize, y * cellSize),
                            size = Size(cellSize + 0.5f, cellSize + 0.5f) // Minor bleed prevention overlap
                        )
                    }
                }
            }

            // Draw alignment small eye if large enough
            val ax = gridSize - 5
            val ay = gridSize - 5
            drawRect(
                color = qrColor,
                topLeft = Offset(ax * cellSize, ay * cellSize),
                size = Size(cellSize * 3, cellSize * 3),
                style = Stroke(width = cellSize)
            )
            drawRect(
                color = qrColor,
                topLeft = Offset((ax + 1) * cellSize, (ay + 1) * cellSize),
                size = Size(cellSize, cellSize)
            )

            // Draw the laser line if animated
            if (showLaser) {
                val laserY = laserYProgress * canvasSize
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, laserY),
                    end = Offset(canvasSize, laserY),
                    strokeWidth = 3.dp.toPx()
                )
                // Draw a soft glowing gradient aura below/above the laser
                drawRect(
                    color = Color.Red.copy(alpha = 0.15f),
                    topLeft = Offset(0f, laserY - 10.dp.toPx()),
                    size = Size(canvasSize, 20.dp.toPx())
                )
            }
        }
    }
}
