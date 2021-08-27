package test.test.reasons.shallow_immutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import test.test.reasons.ShallowImmutableReason
import test.test.reasons.basicInfo

class ValPropertyShallowImmutable(val type: Type, val isParameter: Boolean, val variableDescriptor: VariableDescriptor) : ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val valType = if (isParameter) "(parameter)" else ""
                return when (type) {
                    Type.MUTABLE_BY_ASSUMPTION -> "val $valType refers to mutable type (assumption)"
                    Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION -> "val $valType refers to shallow immutable type (assumption)"
                    Type.UNKNOWN -> "val $valType refers to unknown type"
                    Type.MUTABLE -> "val $valType refers to mutable type"
                    Type.SHALLOW_IMMUTABLE -> "val $valType refers to shallow immutable type"
                }.collapseSpaces()
            }
        override val info: String
            get() = json {
                obj(*variableDescriptor.basicInfo)
            }.toJsonString(true)
    }

    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        SHALLOW_IMMUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
        SHALLOW_IMMUTABLE
    }
}
