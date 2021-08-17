package test.test.reasons.mutable

import test.test.reasons.MutableReason

object MutableByAssumption : MutableReason() {
    override fun isByAssumption(): Boolean = true
    override val csvData = object : CSVData {
        override val reason = "Assumption"
    }
}
