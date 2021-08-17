package test.test.reasons.immutable

import test.test.reasons.ImmutableReason

object ImmutableByAssumption : ImmutableReason() {
    override fun isByAssumption() = true
    override val csvData = object : CSVData {
        override val reason = "Assumption"
    }
}
