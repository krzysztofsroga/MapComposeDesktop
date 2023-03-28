@file:Suppress("unused")

package api

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import ui.paths.PathData
import ui.paths.PathDataBuilder
import ui.state.MapState

/**
 * Adds a path, optionally setting some properties. The default values are:
 * * [width] = 8.dp
 * * [color] = Color(0xFF448AFF)
 * * [offset] = 0
 * * [count] = number of points added to build [pathData]
 *
 * @param id The unique identifier of the path
 * @param pathData
 * @param width The width of the path, in [Dp]
 * @param color The color of the path
 * @param offset The number of points to skip from the beginning of the path
 * @param count The number of points to draw after [offset]
 */
fun MapState.addPath(
    id: String,
    pathData: PathData,
    width: Dp? = null,
    color: Color? = null,
    offset: Int? = null,
    count: Int? = null
) {
    pathState.addPath(id, pathData, width, color, offset, count)
}

/**
 * Updates some properties of a previously added path (using [addPath]).
 *
 * @param id The unique identifier of the path
 * @param pathData The points of the path. The [PathDataBuilder] which was originally used to create
 * the path can be used to build new [PathData] instances with additional points.
 * @param width The width of the path, in [Dp]
 * @param color The color of the path
 * @param offset The number of points to skip from the beginning of the path
 * @param count The number of points to draw after [offset]
 */
fun MapState.updatePath(
    id: String,
    pathData: PathData? = null,
    visible: Boolean? = null,
    width: Dp? = null,
    color: Color? = null,
    offset: Int? = null,
    count: Int? = null
) {
    pathState.updatePath(id, pathData, visible, width, color, offset, count)
}

/**
 * Removes a path.
 *
 * @param id The id of the path
 */
fun MapState.removePath(id: String) {
    pathState.removePath(id)
}

/**
 * Check whether a path was already added or not.
 *
 * @param id The id of the path
 */
fun MapState.hasPath(id: String): Boolean {
    return pathState.pathState.keys.contains(id)
}

/**
 * Get a new instance of [PathDataBuilder].
 * Adding a path is done using [addPath], which requires a [PathData] instance. A [PathData]
 * instance can only be built using a [PathDataBuilder].
 */
fun MapState.makePathDataBuilder(): PathDataBuilder {
    return PathDataBuilder(zoomPanRotateState.fullWidth, zoomPanRotateState.fullHeight)
}