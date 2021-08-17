package test.test.reasons.shallow_immutable

import test.test.reasons.ShallowImmutableReason

class SealedSubclassShallowImmutable(val type: Type, val name: String) : ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when(type) {
                Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION -> "sealed subclass shallow immutable (assumption)"
                Type.SHALLOW_IMMUTABLE -> "sealed subclass shallow immutable"
            }
        override val info: String
            get() = name
    }
    enum class Type {
        SHALLOW_IMMUTABLE_BY_ASSUMPTION,
        SHALLOW_IMMUTABLE,
    }
}
