package test.test.reasons.conditionally_deeply_immutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import test.test.reasons.ConditionallyDeeplyImmutableReason

class PropertyDelegateConditionallyDeeplyImmutable(val type: Type, val propertyDescriptor: PropertyDescriptor) :
    ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when (type) {
                Type.CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION -> "Property delegate conditionally deeply immutable (assumption)"
                Type.CONDITIONALLY_DEEPLY_IMMUTABLE -> "Property delegate conditionally deeply immutable"
            }
        override val info: String
            get() = json {
                obj(
                    "descriptor" to propertyDescriptor.toString()
                )
            }.toJsonString(true)
    }

    enum class Type {
        CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION,
        CONDITIONALLY_DEEPLY_IMMUTABLE
    }
}
