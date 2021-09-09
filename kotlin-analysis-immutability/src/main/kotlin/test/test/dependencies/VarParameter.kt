package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext

data class VarParameter(
    val desc: ValueParameterDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(desc: ValueParameterDescriptor): Dependency =
            VarParameter(desc, desc.type, listOf(desc.type.arguments))
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(test.test.reasons.mutable.VarParameter(desc))
}
