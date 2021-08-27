package test.test.reasons.mutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class VarProperty(val isParameter: Boolean, val variableDescriptor: VariableDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val varType = if (isParameter) "(parameter)" else ""
                return "var $varType".collapseSpaces()
            }
        override val info: String
            get() = json {
                obj(*variableDescriptor.basicInfo)
            }.toJsonString(true)
    }
}
