package test.test.dependencies

import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext

data class DebugType(val debug: List<Any?>) : Dependency() {
    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Immutable()
}
