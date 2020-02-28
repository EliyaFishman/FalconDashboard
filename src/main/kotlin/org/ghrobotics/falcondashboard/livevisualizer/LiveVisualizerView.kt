package org.ghrobotics.falcondashboard.livevisualizer

import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import kfoenix.jfxbutton
import kfoenix.jfxtextfield
import org.ghrobotics.falcondashboard.Settings
import org.ghrobotics.falcondashboard.livevisualizer.charts.FieldChart
import tornadofx.*

class LiveVisualizerView : View() {
    override val root = vbox {
        hbox {
            paddingAll = 10

            text("Network Table IP:   ") {
                alignment = Pos.CENTER_LEFT
            }

            jfxtextfield(Settings.ip.value) {
                prefWidth = 100.0
                setOnKeyPressed {
                    if (it.code == KeyCode.ENTER) {
                        Settings.ip.set(this@jfxtextfield.text)
                        object : Fragment() {
                            override val root = vbox {
                                text("Please Restart Falcon Dashboard for changes to take effect.")
                            }
                        }.openModal(stageStyle = StageStyle.UTILITY)
                    }
                }
            }
            jfxbutton("Change Alliance") {
                spacing = 5.0
                prefWidth = 200.0
                action {
                    FieldChart.flipImage()
                }
            }
        }
        stackpane {
            style {
                backgroundColor = multi(Color.LIGHTGRAY)
            }
            alignment = Pos.CENTER
            vgrow = Priority.ALWAYS
            add(FieldChart)
        }
    }
}