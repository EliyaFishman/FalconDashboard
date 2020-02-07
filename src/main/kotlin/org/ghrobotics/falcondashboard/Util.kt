package org.ghrobotics.falcondashboard

import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil
import javafx.application.Platform
import javafx.beans.property.DoublePropertyBase
import javafx.beans.property.ReadOnlyObjectPropertyBase
import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.stage.FileChooser
import javafx.util.converter.NumberStringConverter
import kfoenix.jfxtextfield
import org.ghrobotics.falcondashboard.generator.GeneratorView
import tornadofx.*
import java.io.File
import java.io.FileWriter

fun Parent.createNumericalEntry(name: String, property: DoublePropertyBase) = hbox {
    paddingAll = 5
    jfxtextfield {
        bind(property, converter = NumberStringConverter())
        prefWidth = 40.0
        minWidth = 40.0
    }
    text("    $name") { alignment = Pos.CENTER_LEFT }
}

fun ui(block: () -> Unit) {
    Platform.runLater(block)
}

fun <R, T> mapprop(receiver: ReadOnlyProperty<R>, getter: ReadOnlyProperty<R>.() -> T): ReadOnlyProperty<T> =
    object : ReadOnlyObjectPropertyBase<T>() {
        override fun getName() = receiver.name
        override fun getBean() = receiver.bean

        init {
            receiver.onChange {
                fireValueChangedEvent()
            }
        }

        override fun get() = getter.invoke(receiver)
    }

fun exportPath() {
    val file = chooseFile(
        "Save json", arrayOf(
            FileChooser.ExtensionFilter(
                "json path file", "*.wpilib.json"
            )
        ),
        FileChooserMode.Save,
        op = {
            File(Settings.lastSaveFileLocation.value).parentFile.let {
                if (it.isDirectory)
                    initialDirectory = it
            }
            Saver.lastSaveLoadFile?.let {
                initialFileName = it.nameWithoutExtension
            }
        }
    ).firstOrNull() ?: return
    FileWriter(file).use {
        it.write(TrajectoryUtil.serializeTrajectory(GeneratorView.trajectory.value))
    }
    Settings.lastSaveFileLocation.set(file.absolutePath)
}