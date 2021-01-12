package gaspb.conduktor.challenge.view.style

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Style : Stylesheet() {
    companion object {
        val blackBorder by cssclass()

        private val black = Color.BLACK
    }

    init {
        blackBorder {
            borderColor += box(black, black, black, black)
        }
        root {
            prefHeight = 500.px
            prefWidth = 800.px
        }
    }
}