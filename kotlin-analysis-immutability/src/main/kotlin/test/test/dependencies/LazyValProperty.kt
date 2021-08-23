package test.test.dependencies

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext

class LazyValProperty(
    val desc: VariableDescriptor,
    val type: KotlinType,
    val delegate: KtPropertyDelegate,
    val debug: List<Any?> = listOf()
) : Dependency() {
    companion object {
        fun fromDescriptor(desc: VariableDescriptor, delegate: KtPropertyDelegate): Dependency =
            LazyValProperty(desc, desc.type, delegate)
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Immutable()
}
