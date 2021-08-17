package test.test.reasons.shallow_immutable

import test.test.reasons.ShallowImmutableReason

class OuterClassShallowImmutable(val byAssumption: Boolean, val outerClass: String) : ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Outer class shallow immutable"
        override val info: String
            get() = outerClass
    }
}
