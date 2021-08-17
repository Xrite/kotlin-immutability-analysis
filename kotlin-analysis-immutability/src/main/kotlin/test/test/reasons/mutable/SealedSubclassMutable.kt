package test.test.reasons.mutable

import test.test.reasons.MutableReason

class SealedSubclassMutable(val type: Type, val name: String) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when(type) {
                Type.MUTABLE_BY_ASSUMPTION -> "sealed subclass mutable (assumption)"
                Type.UNKNOWN -> "sealed subclass unknown"
                Type.MUTABLE -> "sealed subclass mutable"
            }
        override val info: String
            get() = name
    }
    enum class Type {
        MUTABLE_BY_ASSUMPTION,
        UNKNOWN,
        MUTABLE,
    }
}
