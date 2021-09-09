package test.test.reasons

sealed class Reason {
    open fun isByAssumption(): Boolean = false
    open val csvData = object : CSVData {
        override val reason = ""
    }

    interface CSVData {
        val reason: String
        val info: Map<String, Any?>
            get() = mapOf()
    }
}

abstract class ImmutableReason : Reason()

abstract class ShallowImmutableReason : Reason()

abstract class ConditionallyDeeplyImmutableReason : Reason()

abstract class MutableReason : Reason()
