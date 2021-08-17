package test.test.reasons.conditionally_deeply_immutable

import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import test.test.reasons.ConditionallyDeeplyImmutableReason

class ValPropertyConditionallyDeeplyImmutable(val byAssumption: Boolean, val isParameter: Boolean) :
    ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val valType = if (isParameter) "(parameter)" else ""
                val assumption = if (byAssumption) "(assumption)" else ""
                return "val $valType refers to conditionally deeply immutable type $assumption".collapseSpaces()
            }
    }
}
