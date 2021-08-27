package test.test.reasons.shallow_immutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import test.test.reasons.ShallowImmutableReason

class ParentTypeShallowImmutable(val byAssumption: Boolean, val classifierDescriptor: ClassifierDescriptor) : ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Parent type shallow immutable"
        override val info: String
            get() = json {
                obj(
                    "descriptor" to classifierDescriptor.toString()
                )
            }.toJsonString(true)
    }
}
