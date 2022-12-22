import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.programmersbox.common.App
import com.programmersbox.common.MainApp

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MainApp()
    }
}