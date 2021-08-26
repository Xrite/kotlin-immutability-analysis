package test.test.reasons.mutable

import test.test.reasons.MutableReason

class PropertyDelegateMutable(val type: Type, val property: String) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when (type) {
                Type.MUTABLE_BY_ASSUMPTION -> "Property delegate mutable (assumption)"
                Type.UNKNOWN -> "Property delegate unknown"
                Type.MUTABLE -> "Property delegate mutable"
                Type.SHALLOW_IMMUTABLE -> "Property delegate shallow immutable"
                Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION -> "Property delegate shallow immutable (assumption)"
            }
        override val info: String
            get() = property
    }

    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
        SHALLOW_IMMUTABLE,
        SHALLOW_IMMUTABLE_BY_ASSUMPTION
    }
}
