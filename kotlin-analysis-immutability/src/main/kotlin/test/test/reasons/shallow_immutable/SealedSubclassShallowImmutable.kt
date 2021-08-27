package test.test.reasons.shallow_immutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import test.test.reasons.ShallowImmutableReason

class SealedSubclassShallowImmutable(val type: Type, val classifierDescriptor: ClassifierDescriptor) : ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when(type) {
                Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION -> "sealed subclass shallow immutable (assumption)"
                Type.SHALLOW_IMMUTABLE -> "sealed subclass shallow immutable"
            }
        override val info: String
            get() = json {
                obj(
                    "descriptor" to classifierDescriptor.toString()
                )
            }.toJsonString(true)
    }
    enum class Type {
        SHALLOW_IMMUTABLE_BY_ASSUMPTION,
        SHALLOW_IMMUTABLE,
    }
}
