package com.example.textbook.vcs

import com.github.difflib.DiffUtils
import com.github.difflib.patch.Patch
import com.github.difflib.UnifiedDiffUtils
import java.util.*

object VersionControlManager {

    /**
     * Generates a unified diff patch string between two versions of text.
     */
    fun createDelta(original: String, revised: String, fileName: String): String {
        val originalLines = original.lines()
        val revisedLines = revised.lines()
        val patch: Patch<String> = DiffUtils.diff(originalLines, revisedLines)
        val unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(fileName, fileName, originalLines, patch, 0)
        return unifiedDiff.joinToString("\n")
    }

    /**
     * Applies a unified diff patch to a base text to restore a version.
     */
    fun applyDelta(base: String, delta: String): String {
        val baseLines = base.lines()
        val patch = UnifiedDiffUtils.parseUnifiedDiff(delta.lines())
        val revisedLines = DiffUtils.patch(baseLines, patch)
        return revisedLines.joinToString("\n")
    }

    /**
     * Counts additions and deletions in a patch.
     */
    fun getDiffStats(delta: String): Pair<Int, Int> {
        var added = 0
        var removed = 0
        delta.lines().forEach { line ->
            if (line.startsWith("+") && !line.startsWith("+++")) added++
            if (line.startsWith("-") && !line.startsWith("---")) removed++
        }
        return Pair(added, removed)
    }
}
