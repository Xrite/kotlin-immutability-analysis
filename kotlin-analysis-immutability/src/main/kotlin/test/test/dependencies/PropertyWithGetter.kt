package test.test.dependencies

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext

data class PropertyWithGetter(
    val desc: VariableDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(desc: VariableDescriptor): Dependency =
            PropertyWithGetter(desc, desc.type)
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(test.test.reasons.mutable.PropertyWithGetter)
}
