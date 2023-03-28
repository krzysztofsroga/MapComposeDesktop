package ui.markers

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import api.referentialSnapshotFlow
import api.visibleArea
import ui.state.MapState
import ui.state.markers.MarkerRenderState
import ui.state.markers.model.MarkerData
import ui.state.markers.model.RenderingStrategy
import utils.contains
import utils.dpToPx
import utils.map
import utils.throttle
import java.util.*

internal class LazyLoader(
    private val id: String,
    private val mapState: MapState,
    private val markerRenderState: MarkerRenderState,
    markersDataFlow: MutableStateFlow<List<MarkerData>>,
    private val padding: Dp,
    scope: CoroutineScope
) {
    private val referentialSnapshotFlow = mapState.referentialSnapshotFlow()
    private val job: Job

    /* Create a derived state flow from the original unique source of truth */
    private val markers = markersDataFlow.map(scope) {
        it.filter { markerData ->
            (markerData.renderingStrategy is RenderingStrategy.LazyLoading)
                    && markerData.renderingStrategy.lazyLoaderId == id
        }
    }

    init {
        job = scope.launch {
            markers.throttle(100).collectLatest {
                referentialSnapshotFlow.throttle(100).collectLatest {
                    val padding = dpToPx(padding.value).toInt()
                    val visibleArea = mapState.visibleArea(IntOffset(padding, padding))

                    /* Get the list of lazy loaded markers */
                    val markersOnMap =
                        markerRenderState.getLazyLoadedMarkers().filter { markerData ->
                            (markerData.renderingStrategy is RenderingStrategy.LazyLoading)
                                    && markerData.renderingStrategy.lazyLoaderId == id
                        }

                    val visibleMarkers = withContext(Dispatchers.Default) {
                        markers.value.filter { dataSnapshot ->
                            visibleArea.contains(dataSnapshot.x, dataSnapshot.y)
                        }
                    }

                    render(markersOnMap, visibleMarkers)
                }
            }
        }
    }

    private fun render(
        markersOnMap: List<MarkerData>,
        markers: List<MarkerData>
    ) {
        val markersById = markers.associateByTo(mutableMapOf()) { it.uuid }
        val markerIds = mutableListOf<UUID>()

        markersOnMap.forEach { markerData ->
            markerIds.add(markerData.uuid)
            val inMemory = markersById[markerData.uuid]
            if (inMemory == null) {
                markerRenderState.removeLazyLoadedMarker(markerData.id)
            } else {
                if (inMemory.x != markerData.x || inMemory.y != markerData.y) {
                    mapState.markerState.moveMarkerTo(markerData, inMemory.x, inMemory.y)
                }
            }
        }

        markersById.entries.forEach {
            if (it.key !in markerIds) {
                markerRenderState.addLazyLoadedMarker(it.value)
            }
        }
    }

    fun cancel(removeManaged: Boolean) {
        job.cancel()
        if (removeManaged) {
            markerRenderState.removeAllLazyLoadedMarkers(id)
        }
    }
}