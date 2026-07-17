package com.example.textbook.vcs

import com.github.difflib.DiffUtils
import com.github.difflib.patch.AbstractDelta
import com.github.difflib.patch.DeltaType
import com.github.difflib.patch.Patch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class SerializableDelta(
    val type: String,
    val sourcePosition: Int,
    val sourceLines: List<String>,
    val targetPosition: Int,
    val targetLines: List<String>
)

@Singleton
class DiffManager @Inject constructor() {

    fun generateDiffJson(oldContent: String, newContent: String): String {
        val oldLines = oldContent.lines()
        val newLines = newContent.lines()
        val patch = DiffUtils.diff(oldLines, newLines)
        
        val serializableDeltas = patch.deltas.map { delta ->
            SerializableDelta(
                type = delta.type.name,
                sourcePosition = delta.source.position,
                sourceLines = delta.source.lines,
                targetPosition = delta.target.position,
                targetLines = delta.target.lines
            )
        }
        return Json.encodeToString(serializableDeltas)
    }

    fun applyDiffJson(baseContent: String, diffJson: String): String {
        if (diffJson.isEmpty() || diffJson == "[]") return baseContent
        
        val deltas = Json.decodeFromString<List<SerializableDelta>>(diffJson)
        val oldLines = baseContent.lines().toMutableList()
        
        // Sorting deltas in reverse order to apply them without affecting indices of subsequent deltas
        val sortedDeltas = deltas.sortedByDescending { it.sourcePosition }
        
        for (delta in sortedDeltas) {
            when (delta.type) {
                DeltaType.INSERT.name -> {
                    oldLines.addAll(delta.sourcePosition, delta.targetLines)
                }
                DeltaType.DELETE.name -> {
                    for (i in 0 until delta.sourceLines.size) {
                        if (delta.sourcePosition < oldLines.size) {
                            oldLines.removeAt(delta.sourcePosition)
                        }
                    }
                }
                DeltaType.CHANGE.name -> {
                    for (i in 0 until delta.sourceLines.size) {
                        if (delta.sourcePosition < oldLines.size) {
                            oldLines.removeAt(delta.sourcePosition)
                        }
                    }
                    oldLines.addAll(delta.sourcePosition, delta.targetLines)
                }
            }
        }
        return oldLines.joinToString("\n")
    }
}
