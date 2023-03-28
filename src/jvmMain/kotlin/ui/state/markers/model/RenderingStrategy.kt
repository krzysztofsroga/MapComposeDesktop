package ui.state.markers.model


sealed interface RenderingStrategy {
    object Default : RenderingStrategy
    data class Clustering(val clustererId: String) : RenderingStrategy
    data class LazyLoading(val lazyLoaderId: String) : RenderingStrategy
}