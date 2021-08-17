package test.test.reasons.mutable

import test.test.reasons.MutableReason

class ParentTypeMutable(val type: Type, val parentType: String) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when (type) {
                Type.MUTABLE_BY_ASSUMPTION -> "Parent type mutable (assumption)"
                Type.UNKNOWN -> "Parent type unknown"
                Type.MUTABLE -> "Parent type mutable"
            }
        override val info: String
            get() = parentType
    }
    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
    }
}
