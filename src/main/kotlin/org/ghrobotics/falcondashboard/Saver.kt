package org.ghrobotics.falcondashboard

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.stage.FileChooser
import org.ghrobotics.falcondashboard.generator.GeneratorView
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import tornadofx.getValue
import tornadofx.setValue
import java.io.File
import java.io.FileReader
import java.io.FileWriter


object Saver {
    var lock = false
    val reversed = SimpleBooleanProperty(false)
    val startVelocity = SimpleDoubleProperty(0.0)
    val endVelocity = SimpleDoubleProperty(0.0)
    private var defaultValues: String? = null
    val lastSaveLoadFileProperty = SimpleObjectProperty<File>()
    var lastSaveLoadFile: File? by lastSaveLoadFileProperty


    private val gson = GsonBuilder().registerTypeAdapter<Saver> {
        write {

            beginArray()
            value(it.reversed.value)
            value(it.startVelocity.value)
            value(it.endVelocity.value)
            value(WaypointUtil.serializeWaypoints(GeneratorView.waypoints))
            endArray()
        }
        read {
            lock = true
            beginArray()
            reversed.set(nextBoolean())
            startVelocity.set(nextDouble())
            endVelocity.set(nextDouble())
            lock = false
            GeneratorView.waypoints.setAll(WaypointUtil.deserializeWaypoints(nextString()))
            endArray()
            Saver
        }
    }.create()!!

    init {
        Settings.lastLoadFileLocation.value?.let {
            lastSaveLoadFile = File(Settings.lastLoadFileLocation.value)
            loadFromFile(lastSaveLoadFile!!)
        } ?: run {
            defaultValues = gson.toJson(Saver)
        }
    }

    fun load() {
        val file = chooseFile(
            "Load File",
            arrayOf(FileChooser.ExtensionFilter("falcon dashboard save file", "*.fds")),
            op = {
                Settings.lastLoadFileLocation.value?.let {
                    initialDirectory = File(it).parentFile
                }
            }
        ).firstOrNull() ?: return
        lastSaveLoadFile = file
        Settings.lastLoadFileLocation.set(file.absolutePath)
        loadFromFile(file)
    }

    private fun loadFromFile(file: File) {
        if (file.exists()) {
            FileReader(file).use {
                gson.fromJson<Saver>(it)
            }
        }
    }

    fun save(): Boolean {
        val file = chooseFile(
            "save",
            arrayOf(FileChooser.ExtensionFilter("Falcon dashboard save file", "*.fds")),
            FileChooserMode.Save,
            op = { initialDirectory = File(Settings.lastLoadFileLocation.value).parentFile }
        ).firstOrNull() ?: return false
        saveFile(file)
        lastSaveLoadFile = file
        Settings.lastLoadFileLocation.set(file.absolutePath)
        return true
    }

    private fun saveFile(file: File) {
        FileWriter(file).use {
            it.write(gson.toJson(Saver))
        }
    }

    fun saveCurrentFile(): Boolean {
        lastSaveLoadFile?.let {
            saveFile(it)
        } ?: return save()
        return true
    }

    fun hasChanged(): Boolean {
        val file = lastSaveLoadFile
        return if (file != null) {
            if (file.exists()) file.readText() != gson.toJson(Saver) else {
                file.delete(); defaultValues != gson.toJson(Saver)
            }
        } else defaultValues != gson.toJson(Saver)
    }
}
