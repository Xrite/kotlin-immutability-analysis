package test.test.reasons.mutable

import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import test.test.reasons.MutableReason

class VarProperty(val isParameter: Boolean, val name: String) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val varType = if (isParameter) "(parameter)" else ""
                return "var $varType".collapseSpaces()
            }
        override val info: String
            get() = name
    }
}
