package test.test.dependencies

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext

data class VarProperty(
    val desc: PropertyDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(desc: PropertyDescriptor): Dependency =
            VarProperty(desc, desc.type, listOf(desc.type.arguments))
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(test.test.reasons.mutable.VarProperty(desc))
}
