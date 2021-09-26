package test.test.reasons.shallow_immutable

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import test.test.reasons.ShallowImmutableReason
import test.test.reasons.basicInfo

class OuterClassShallowImmutable(val byAssumption: Boolean, val classifierDescriptor: ClassifierDescriptor) : ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Outer class shallow immutable"
        override val info: Map<String, Any?>
            get() = mapOf(*classifierDescriptor.basicInfo)
    }
}
