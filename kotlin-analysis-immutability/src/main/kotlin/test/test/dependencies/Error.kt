package test.test.dependencies

import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext
import test.test.reasons.mutable.Error

data class Error(
    val reason: Any?,
    val debug: List<Any?> = listOf()
) : Dependency() {
    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(Error)
}
