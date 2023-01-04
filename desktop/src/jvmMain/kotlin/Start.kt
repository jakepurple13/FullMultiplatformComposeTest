import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.programmersbox.common.UIShow

fun main() = if (false) application {
    Window(onCloseRequest = ::exitApplication) {
        UIShow()
    }
} else mains()