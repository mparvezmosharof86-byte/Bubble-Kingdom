package com.example

import com.example.game.engine.GameBubble
import com.example.game.engine.GridCoord
import com.example.game.engine.HexGrid
import com.example.game.model.BubbleColor
import com.example.game.model.BubbleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HexGridUnitTest {
    private lateinit var grid: HexGrid

    @Before
    fun setUp() {
        grid = HexGrid(columnsEven = 8, columnsOdd = 7, maxRows = 18)
    }

    @Test
    fun testEvenRowNeighbors() {
        // (0, 2) is on an even row
        val neighbors = grid.getNeighbors(0, 2)
        // (0, 1), (0, 3), (1, 1), (1, 2)
        assertTrue(neighbors.contains(GridCoord(0, 1)))
        assertTrue(neighbors.contains(GridCoord(0, 3)))
        assertTrue(neighbors.contains(GridCoord(1, 1)))
        assertTrue(neighbors.contains(GridCoord(1, 2)))
        assertEquals(4, neighbors.size) // Top row has no (r - 1) neighbors
    }

    @Test
    fun testOddRowNeighbors() {
        // (1, 2) is on an odd row
        val neighbors = grid.getNeighbors(1, 2)
        // Up-Left: (0, 2), Up-Right: (0, 3), Left: (1, 1), Right: (1, 3), Down-Left: (2, 2), Down-Right: (2, 3)
        assertTrue(neighbors.contains(GridCoord(0, 2)))
        assertTrue(neighbors.contains(GridCoord(0, 3)))
        assertTrue(neighbors.contains(GridCoord(1, 1)))
        assertTrue(neighbors.contains(GridCoord(1, 3)))
        assertTrue(neighbors.contains(GridCoord(2, 2)))
        assertTrue(neighbors.contains(GridCoord(2, 3)))
        assertEquals(6, neighbors.size)
    }

    @Test
    fun testConnectedMatchesGroupOfThree() {
        // Put 3 Red bubbles connected together
        grid.setBubble(0, 0, GameBubble(1, BubbleColor.RED))
        grid.setBubble(0, 1, GameBubble(2, BubbleColor.RED))
        grid.setBubble(1, 0, GameBubble(3, BubbleColor.RED))
        // Put a Blue bubble adjacent
        grid.setBubble(0, 2, GameBubble(4, BubbleColor.BLUE))

        val matches = grid.findConnectedMatches(0, 0)
        assertEquals(3, matches.size)
        assertTrue(matches.contains(GridCoord(0, 0)))
        assertTrue(matches.contains(GridCoord(0, 1)))
        assertTrue(matches.contains(GridCoord(1, 0)))
        assertFalse(matches.contains(GridCoord(0, 2)))
    }

    @Test
    fun testDetachedBubblesAreDetectedWhenAnchorRemoved() {
        // Anchor at (0, 0)
        grid.setBubble(0, 0, GameBubble(1, BubbleColor.RED))
        // Connected to anchor at (1, 0)
        grid.setBubble(1, 0, GameBubble(2, BubbleColor.BLUE))
        // Connected to (1, 0) at (2, 0)
        grid.setBubble(2, 0, GameBubble(3, BubbleColor.GREEN))

        // Initially nothing is detached
        var detached = grid.findDetachedBubbles()
        assertEquals(0, detached.size)

        // Now pop the anchor at (0, 0)
        grid.setBubble(0, 0, null)

        // (1, 0) and (2, 0) are now hanging with no path to row 0!
        detached = grid.findDetachedBubbles()
        assertEquals(2, detached.size)
        val detachedCoords = detached.map { it.first }
        assertTrue(detachedCoords.contains(GridCoord(1, 0)))
        assertTrue(detachedCoords.contains(GridCoord(2, 0)))
    }

    @Test
    fun testRainbowBubbleMatchesAdjacentColor() {
        grid.setBubble(0, 0, GameBubble(1, BubbleColor.RED))
        grid.setBubble(0, 1, GameBubble(2, BubbleColor.RED))
        // Place Rainbow bubble adjacent
        grid.setBubble(1, 0, GameBubble(3, BubbleColor.RED, type = BubbleType.RAINBOW))

        val matches = grid.findConnectedMatches(0, 0)
        assertEquals(3, matches.size)
        assertTrue(matches.contains(GridCoord(1, 0)))
    }

    @Test
    fun testTrajectoryWallBounce() {
        val result = grid.calculateTrajectory(
            startX = 360f,
            startY = 1000f,
            angleRadians = -2.8f, // aiming towards top-left
            bubbleRadius = 45f,
            boardWidth = 720f,
            maxBounces = 2
        )
        // Trajectory points should exist and hit the wall or top ceiling
        assertTrue(result.points.size >= 2)
        assertNotNull(result.targetCell)
    }
}
