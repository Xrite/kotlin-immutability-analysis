package test.test.reasons

sealed class Reason {
    open fun isByAssumption(): Boolean = false
    open val csvData = object : CSVData {
        override val reason = ""
        override val info = ""
    }

    interface CSVData {
        val reason: String
        val info: String
            get() = ""
    }
}

abstract class ImmutableReason : Reason()

abstract class ShallowImmutableReason : Reason()

abstract class ConditionallyDeeplyImmutableReason : Reason()

abstract class MutableReason : Reason()
