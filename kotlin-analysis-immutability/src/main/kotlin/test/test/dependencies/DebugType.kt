package test.test.dependencies

import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty

data class DebugType(val debug: List<Any?>) : Dependency() {
    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
        ImmutabilityProperty.Immutable()
}
