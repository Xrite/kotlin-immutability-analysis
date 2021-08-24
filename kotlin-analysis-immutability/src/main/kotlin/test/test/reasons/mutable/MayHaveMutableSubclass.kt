package test.test.reasons.mutable

import test.test.reasons.MutableReason

class MayHaveMutableSubclass : MutableReason() {
    override val csvData: CSVData
        get() = object : CSVData {
            override val reason: String
                get() = "May have mutable subclass"

        }
}
