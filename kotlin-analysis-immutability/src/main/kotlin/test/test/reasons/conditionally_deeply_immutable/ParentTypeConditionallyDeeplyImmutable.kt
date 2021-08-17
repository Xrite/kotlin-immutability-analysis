package test.test.reasons.conditionally_deeply_immutable

import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import test.test.reasons.ConditionallyDeeplyImmutableReason

class ParentTypeConditionallyDeeplyImmutable(val byAssumption: Boolean) : ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val assumption = if (byAssumption) " (assumption)" else ""
                return "Parent type conditionally deeply immutable $assumption".collapseSpaces()
            }
    }
}
