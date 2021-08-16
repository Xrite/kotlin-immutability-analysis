package test.test.dependencies

import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.MutableReason

data class Error(
    val reason: Any?,
    val debug: List<Any?> = listOf()
) : Dependency() {
    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(MutableReason.Error)
}
