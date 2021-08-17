package test.test.reasons.mutable

import test.test.reasons.MutableReason

object Error : MutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Error"
    }
}
