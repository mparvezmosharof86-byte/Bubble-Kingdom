package com.example.game.engine

import com.example.game.model.BubbleColor
import com.example.game.model.BubbleType
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class GameBubble(
    val id: Long,
    val color: BubbleColor,
    val type: BubbleType = BubbleType.NORMAL,
    val petId: String? = null
)

data class GridCoord(val row: Int, val col: Int)

data class TrajectoryPoint(val x: Float, val y: Float)

data class TrajectoryResult(
    val points: List<TrajectoryPoint>,
    val targetCell: GridCoord?,
    val hitBubble: Boolean
)

class HexGrid(
    val columnsEven: Int = 8,
    val columnsOdd: Int = 7,
    val maxRows: Int = 18
) {
    // 2D Array representing the board [row][col]
    val cells: Array<Array<GameBubble?>> = Array(maxRows) { row ->
        val cols = if (row % 2 == 0) columnsEven else columnsOdd
        arrayOfNulls(cols)
    }

    fun getColsForRow(row: Int): Int = if (row % 2 == 0) columnsEven else columnsOdd

    fun isValidCoord(row: Int, col: Int): Boolean {
        if (row < 0 || row >= maxRows) return false
        val cols = getColsForRow(row)
        return col in 0 until cols
    }

    fun getBubble(row: Int, col: Int): GameBubble? {
        return if (isValidCoord(row, col)) cells[row][col] else null
    }

    fun setBubble(row: Int, col: Int, bubble: GameBubble?) {
        if (isValidCoord(row, col)) {
            cells[row][col] = bubble
        }
    }

    fun clear() {
        for (r in 0 until maxRows) {
            for (c in 0 until getColsForRow(r)) {
                cells[r][c] = null
            }
        }
    }

    /**
     * Returns the 6 hexagonal neighbors of (row, col)
     */
    fun getNeighbors(row: Int, col: Int): List<GridCoord> {
        val neighbors = mutableListOf<GridCoord>()
        val isEven = (row % 2 == 0)

        val potential = if (isEven) {
            listOf(
                GridCoord(row, col - 1),     // Left
                GridCoord(row, col + 1),     // Right
                GridCoord(row - 1, col - 1), // Up-Left
                GridCoord(row - 1, col),     // Up-Right
                GridCoord(row + 1, col - 1), // Down-Left
                GridCoord(row + 1, col)      // Down-Right
            )
        } else {
            listOf(
                GridCoord(row, col - 1),     // Left
                GridCoord(row, col + 1),     // Right
                GridCoord(row - 1, col),     // Up-Left
                GridCoord(row - 1, col + 1), // Up-Right
                GridCoord(row + 1, col),     // Down-Left
                GridCoord(row + 1, col + 1)  // Down-Right
            )
        }

        for (coord in potential) {
            if (isValidCoord(coord.row, coord.col)) {
                neighbors.add(coord)
            }
        }
        return neighbors
    }

    /**
     * Converts grid (row, col) to 2D center coordinates (x, y) based on bubble radius R
     */
    fun getCenter(row: Int, col: Int, bubbleRadius: Float, boardWidth: Float): Pair<Float, Float> {
        val rowHeight = bubbleRadius * sqrt(3.0f)
        val y = bubbleRadius + row * rowHeight

        val x = if (row % 2 == 0) {
            // Even row: 8 bubbles
            bubbleRadius + col * (2f * bubbleRadius)
        } else {
            // Odd row: 7 bubbles indented by bubbleRadius
            2f * bubbleRadius + col * (2f * bubbleRadius)
        }
        return Pair(x, y)
    }

    /**
     * Breadth-First-Search to find all matching connected same-color bubbles
     */
    fun findConnectedMatches(startRow: Int, startCol: Int): Set<GridCoord> {
        val startBubble = getBubble(startRow, startCol) ?: return emptySet()
        val matchColor = startBubble.color

        val visited = mutableSetOf<GridCoord>()
        val queue = ArrayDeque<GridCoord>()
        val startCoord = GridCoord(startRow, startCol)

        queue.add(startCoord)
        visited.add(startCoord)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (neighbor in getNeighbors(current.row, current.col)) {
                val nBubble = getBubble(neighbor.row, neighbor.col)
                if (nBubble != null && !visited.contains(neighbor)) {
                    // Match if same color or if rainbow
                    val isMatch = (nBubble.color == matchColor) ||
                            (nBubble.type == BubbleType.RAINBOW) ||
                            (startBubble.type == BubbleType.RAINBOW)
                    if (isMatch && nBubble.type != BubbleType.STONE) {
                        visited.add(neighbor)
                        queue.add(neighbor)
                    }
                }
            }
        }
        return visited
    }

    /**
     * Detects bubbles that have lost connection to the top ceiling (row 0).
     * These floating detached bubbles drop with gravity!
     */
    fun findDetachedBubbles(): List<Pair<GridCoord, GameBubble>> {
        val connectedToCeiling = mutableSetOf<GridCoord>()
        val queue = ArrayDeque<GridCoord>()

        // 1. All bubbles in row 0 are anchored to the ceiling
        for (c in 0 until getColsForRow(0)) {
            if (cells[0][c] != null) {
                val coord = GridCoord(0, c)
                connectedToCeiling.add(coord)
                queue.add(coord)
            }
        }

        // 2. BFS to find all reachable bubbles from ceiling
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (neighbor in getNeighbors(current.row, current.col)) {
                if (getBubble(neighbor.row, neighbor.col) != null && !connectedToCeiling.contains(neighbor)) {
                    connectedToCeiling.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }

        // 3. Any non-null bubble not in connectedToCeiling is detached!
        val detached = mutableListOf<Pair<GridCoord, GameBubble>>()
        for (r in 0 until maxRows) {
            for (c in 0 until getColsForRow(r)) {
                val bubble = cells[r][c]
                if (bubble != null) {
                    val coord = GridCoord(r, c)
                    if (!connectedToCeiling.contains(coord)) {
                        detached.add(Pair(coord, bubble))
                    }
                }
            }
        }
        return detached
    }

    /**
     * Finds the closest empty hex grid cell to attach a colliding projectile
     */
    fun findBestAttachmentCell(
        impactX: Float,
        impactY: Float,
        bubbleRadius: Float,
        boardWidth: Float
    ): GridCoord? {
        var bestCoord: GridCoord? = null
        var minDistance = Float.MAX_VALUE

        val rowHeight = bubbleRadius * sqrt(3.0f)
        val estimatedRow = ((impactY - bubbleRadius) / rowHeight).toInt().coerceIn(0, maxRows - 1)

        // Check rows near estimatedRow
        val startR = (estimatedRow - 2).coerceAtLeast(0)
        val endR = (estimatedRow + 2).coerceAtMost(maxRows - 1)

        for (r in startR..endR) {
            for (c in 0 until getColsForRow(r)) {
                if (cells[r][c] == null) {
                    // Check if adjacent to ceiling (row 0) or adjacent to an existing bubble
                    val hasNeighbor = (r == 0) || getNeighbors(r, c).any { getBubble(it.row, it.col) != null }
                    if (hasNeighbor) {
                        val (cx, cy) = getCenter(r, c, bubbleRadius, boardWidth)
                        val dx = impactX - cx
                        val dy = impactY - cy
                        val distSq = dx * dx + dy * dy
                        if (distSq < minDistance) {
                            minDistance = distSq
                            bestCoord = GridCoord(r, c)
                        }
                    }
                }
            }
        }

        if (bestCoord == null) {
            // Failsafe: find closest slot in row 0 (ceiling)
            for (c in 0 until getColsForRow(0)) {
                if (cells[0][c] == null) {
                    val (cx, cy) = getCenter(0, c, bubbleRadius, boardWidth)
                    val dx = impactX - cx
                    val dy = impactY - cy
                    val distSq = dx * dx + dy * dy
                    if (distSq < minDistance) {
                        minDistance = distSq
                        bestCoord = GridCoord(0, c)
                    }
                }
            }
        }

        return bestCoord
    }

    /**
     * Simulates trajectory aiming ray with wall reflections and collision detection
     */
    fun calculateTrajectory(
        startX: Float,
        startY: Float,
        angleRadians: Float,
        bubbleRadius: Float,
        boardWidth: Float,
        maxBounces: Int = 2
    ): TrajectoryResult {
        val points = mutableListOf<TrajectoryPoint>()
        points.add(TrajectoryPoint(startX, startY))

        var curX = startX
        var curY = startY
        var dirX = cos(angleRadians)
        var dirY = sin(angleRadians)

        val step = bubbleRadius * 0.4f
        val leftWall = bubbleRadius
        val rightWall = boardWidth - bubbleRadius
        val topWall = bubbleRadius

        var bounces = 0
        var targetCell: GridCoord? = null
        var hitBubble = false

        // Step forward up to 800 steps
        for (i in 0..800) {
            curX += dirX * step
            curY += dirY * step

            // Left Wall bounce
            if (curX <= leftWall) {
                curX = leftWall
                dirX = -dirX
                bounces++
                points.add(TrajectoryPoint(curX, curY))
                if (bounces > maxBounces) break
            }
            // Right Wall bounce
            else if (curX >= rightWall) {
                curX = rightWall
                dirX = -dirX
                bounces++
                points.add(TrajectoryPoint(curX, curY))
                if (bounces > maxBounces) break
            }

            // Top ceiling hit
            if (curY <= topWall) {
                curY = topWall
                points.add(TrajectoryPoint(curX, curY))
                targetCell = findBestAttachmentCell(curX, curY, bubbleRadius, boardWidth)
                break
            }

            // Check collision with existing grid bubbles
            val collision = checkCollisionWithGrid(curX, curY, bubbleRadius, boardWidth)
            if (collision != null) {
                hitBubble = true
                points.add(TrajectoryPoint(curX, curY))
                // Attach at the closest valid neighbor cell
                targetCell = findBestAttachmentCell(curX, curY, bubbleRadius, boardWidth)
                break
            }
        }

        if (points.size == 1) {
            points.add(TrajectoryPoint(curX, curY))
        }

        if (targetCell == null && points.isNotEmpty()) {
            val lastPoint = points.last()
            targetCell = findBestAttachmentCell(lastPoint.x, lastPoint.y, bubbleRadius, boardWidth)
        }

        return TrajectoryResult(points, targetCell, hitBubble)
    }

    private fun checkCollisionWithGrid(
        x: Float,
        y: Float,
        bubbleRadius: Float,
        boardWidth: Float
    ): GridCoord? {
        val collisionDistSq = (1.85f * bubbleRadius) * (1.85f * bubbleRadius)

        for (r in 0 until maxRows) {
            for (c in 0 until getColsForRow(r)) {
                if (cells[r][c] != null) {
                    val (cx, cy) = getCenter(r, c, bubbleRadius, boardWidth)
                    val dx = x - cx
                    val dy = y - cy
                    if ((dx * dx + dy * dy) <= collisionDistSq) {
                        return GridCoord(r, c)
                    }
                }
            }
        }
        return null
    }

    /**
     * Checks if board has no bubbles left (victory!)
     */
    fun isBoardClear(): Boolean {
        for (r in 0 until maxRows) {
            for (c in 0 until getColsForRow(r)) {
                if (cells[r][c] != null) return false
            }
        }
        return true
    }

    /**
     * Counts remaining pets on board
     */
    fun countRemainingPets(): Int {
        var count = 0
        for (r in 0 until maxRows) {
            for (c in 0 until getColsForRow(r)) {
                if (cells[r][c]?.type == BubbleType.PET) {
                    count++
                }
            }
        }
        return count
    }

    /**
     * Gets lowest populated row index
     */
    fun getLowestRow(): Int {
        for (r in maxRows - 1 downTo 0) {
            for (c in 0 until getColsForRow(r)) {
                if (cells[r][c] != null) return r
            }
        }
        return -1
    }
}
