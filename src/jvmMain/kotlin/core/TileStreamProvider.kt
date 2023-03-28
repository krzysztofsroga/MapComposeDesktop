package core

import java.io.InputStream

/**
 * The tile provider is user-provided to the MapView. It must be supplied as part of the configuration
 * of MapCompose.
 *
 * MapCompose leverages bitmap pooling to reduce the pressure on the garbage collector. However,
 * there's no tile caching by default - this is an implementation detail of the supplied
 * [TileStreamProvider].
 *
 * If [getTileStream] returns null, the tile is simply ignored by the tile processing machinery.
 */
fun interface TileStreamProvider { //TODO can I make it so I can return a stateflow? 🤔
    suspend fun getTileStream(row: Int, col: Int, zoomLvl: Int): InputStream?
}