package test.test.dependencies

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext

class OpenProperty(
    val desc: VariableDescriptor,
    val debug: List<Any?> = listOf()
) : Dependency() {
    companion object {
        fun fromDescriptor(desc: VariableDescriptor): Dependency =
            OpenProperty(desc)
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(test.test.reasons.mutable.OpenProperty(desc.toString()))
}
