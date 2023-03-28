
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ovh.plrapps.mapcompose.testapp.features.clustering.MarkersClusteringViewModel
import ui.MapUI
import java.io.File

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var viewModel = remember {MarkersClusteringViewModel()}
    println(
        File("src/jvmMain/assets/tiles/mont_blanc").absolutePath)

    MaterialTheme {
        MapUI(state = viewModel.state)
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
