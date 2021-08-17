package test.test.reasons.shallow_immutable

import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import test.test.reasons.ShallowImmutableReason

class ValPropertyShallowImmutable(val type: Type, val isParameter: Boolean, val property: String) : ShallowImmutableReason() {
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
            get() = property
    }

    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        SHALLOW_IMMUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
        SHALLOW_IMMUTABLE
    }
}
