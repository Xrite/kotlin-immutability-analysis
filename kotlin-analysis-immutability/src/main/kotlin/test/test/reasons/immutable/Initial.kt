package test.test.reasons.immutable

import test.test.reasons.ImmutableReason

object Initial : ImmutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Initial"
    }
}
