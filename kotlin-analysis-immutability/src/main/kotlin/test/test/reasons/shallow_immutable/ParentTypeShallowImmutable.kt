package test.test.reasons.shallow_immutable

import test.test.reasons.ShallowImmutableReason

class ParentTypeShallowImmutable(val byAssumption: Boolean, val parentType: String) : ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Parent type shallow immutable"
        override val info: String
            get() = parentType
    }
}
