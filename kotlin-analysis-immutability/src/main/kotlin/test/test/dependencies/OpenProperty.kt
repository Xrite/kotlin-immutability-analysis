package test.test.dependencies

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty

class OpenProperty(
    val desc: VariableDescriptor,
    val debug: List<Any?> = listOf()
) : Dependency() {
    companion object {
        fun fromDescriptor(desc: VariableDescriptor): Dependency =
            OpenProperty(desc)
    }

    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(test.test.reasons.mutable.OpenProperty(desc.toString()))
}
