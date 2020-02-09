package org.ghrobotics.falcondashboard

import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import kfoenix.jfxtabpane
import org.ghrobotics.falcondashboard.generator.GeneratorView
import org.ghrobotics.falcondashboard.livevisualizer.LiveVisualizerView
import tornadofx.*

class MainView : View("FRC 5190 Falcon Dashboard") {
    override val root: Parent = hbox {
        jfxtabpane {
            addEventHandler(KeyEvent.KEY_PRESSED) {
                when {
                    it.code == KeyCode.S && it.isControlDown -> {
                        when {
                            it.isAltDown -> exportPath()
                            it.isShiftDown -> Saver.save()
                            else -> Saver.saveCurrentFile()
                        }
                    }
                    it.code == KeyCode.O && it.isControlDown -> Saver.load()
                    it.code == KeyCode.G && it.isControlDown -> FalconDashboard.trajectory =
                        TrajectoryUtil.serializeTrajectory(GeneratorView.trajectory.value)
                    it.code == KeyCode.F4 -> FX.primaryStage.let { stage -> stage.isFullScreen = !stage.isFullScreen }
                }
            }
            stylesheets += resources["/AppStyle.css"]
            prefHeight = 705.0
            prefWidth = 1550.0

            tab("Generator") {
                this += GeneratorView()
                isClosable = false
            }
            tab("Live Visualizer") {
                this += LiveVisualizerView()
                isClosable = false
            }
        }
    }

    init {
        FX.primaryStage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        FX.primaryStage.fullScreenExitHint = "Press F4 to exit"
    }
}