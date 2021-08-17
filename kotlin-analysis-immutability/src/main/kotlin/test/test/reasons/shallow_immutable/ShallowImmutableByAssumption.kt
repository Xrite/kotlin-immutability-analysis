package test.test.reasons.shallow_immutable

import test.test.reasons.ShallowImmutableReason

object ShallowImmutableByAssumption : ShallowImmutableReason() {
    override fun isByAssumption(): Boolean = true
    override val csvData = object : CSVData {
        override val reason = "Assumption"
    }
}
