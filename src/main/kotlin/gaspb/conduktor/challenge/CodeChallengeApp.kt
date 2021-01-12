package gaspb.conduktor.challenge


import gaspb.conduktor.challenge.view.AppWorkspace
import gaspb.conduktor.challenge.view.style.Style
import javafx.application.Application
import tornadofx.App

class CodeChallengeApp : App(AppWorkspace::class, Style::class)

fun main(args: Array<String>) {
    Application.launch(CodeChallengeApp::class.java, *args)
}