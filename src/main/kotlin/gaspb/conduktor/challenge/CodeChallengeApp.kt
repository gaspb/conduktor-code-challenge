package gaspb.conduktor.challenge


import gaspb.conduktor.challenge.view.AppWorkspace
import javafx.application.Application
import tornadofx.App

class CodeChallengeApp : App(AppWorkspace::class)

fun main(args: Array<String>) {
    Application.launch(CodeChallengeApp::class.java, *args)
}