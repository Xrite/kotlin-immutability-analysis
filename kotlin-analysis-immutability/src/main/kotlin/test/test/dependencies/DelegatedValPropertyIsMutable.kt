package test.test.dependencies

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext
import test.test.reasons.mutable.DelegatedProperty

data class DelegatedValPropertyIsMutable(
    val desc: VariableDescriptor,
    val type: KotlinType,
    val delegate: KtPropertyDelegate,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromDescriptor(desc: VariableDescriptor, delegate: KtPropertyDelegate): DelegatedValPropertyIsMutable =
            DelegatedValPropertyIsMutable(desc, desc.type, delegate)
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(DelegatedProperty(desc.toString()))
}
