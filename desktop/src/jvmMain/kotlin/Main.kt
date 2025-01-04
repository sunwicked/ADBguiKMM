import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(1200.dp, 800.dp) // Initial window size
    )
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "ADB GUI Desktop",
        state = windowState,
        resizable = true
    ) {
        App()
    }
} 