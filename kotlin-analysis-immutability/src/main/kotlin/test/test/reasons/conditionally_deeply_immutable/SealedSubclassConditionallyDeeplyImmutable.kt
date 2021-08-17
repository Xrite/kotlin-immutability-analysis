package test.test.reasons.conditionally_deeply_immutable

import test.test.reasons.ConditionallyDeeplyImmutableReason

class SealedSubclassConditionallyDeeplyImmutable(val type: Type, val name: String) : ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when(type) {
                Type.CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION -> "sealed subclass conditionally deeply immutable (assumption)"
                Type.CONDITIONALLY_DEEPLY_IMMUTABLE -> "sealed subclass conditionally deeply immutable"
            }
        override val info: String
            get() = name
    }
    enum class Type {
        CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION,
        CONDITIONALLY_DEEPLY_IMMUTABLE,
    }
}
