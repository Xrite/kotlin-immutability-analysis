package test.test.reasons.mutable

import test.test.reasons.MutableReason

object PropertyWithGetter : MutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Property with getter"
    }
}
