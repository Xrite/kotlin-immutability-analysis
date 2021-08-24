package test.test.reasons.conditionally_deeply_immutable

import test.test.reasons.ConditionallyDeeplyImmutableReason

object ConditionallyDeeplyImmutableByAssumption : ConditionallyDeeplyImmutableReason() {
    override fun isByAssumption(): Boolean = true
    override val csvData = object : CSVData {
        override val reason = "Assumption"
    }
}
