package test.test.reasons.conditionally_deeply_immutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import test.test.reasons.ConditionallyDeeplyImmutableReason

class SealedSubclassConditionallyDeeplyImmutable(val type: Type, val classifierDescriptor: ClassifierDescriptor) : ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when(type) {
                Type.CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION -> "sealed subclass conditionally deeply immutable (assumption)"
                Type.CONDITIONALLY_DEEPLY_IMMUTABLE -> "sealed subclass conditionally deeply immutable"
            }
        override val info: String
            get() = json {
                obj(
                    "descriptor" to classifierDescriptor.toString()
                )
            }.toJsonString(true)
    }
    enum class Type {
        CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION,
        CONDITIONALLY_DEEPLY_IMMUTABLE,
    }
}
