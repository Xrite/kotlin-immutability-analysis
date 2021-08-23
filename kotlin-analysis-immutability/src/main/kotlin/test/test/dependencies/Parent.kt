package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext
import test.test.ImmutabilityWithContext.Result.*
import test.test.reasons.conditionally_deeply_immutable.ParentTypeConditionallyDeeplyImmutable
import test.test.reasons.mutable.ParentTypeMutable
import test.test.reasons.shallow_immutable.ParentTypeShallowImmutable

data class Parent(
    val descriptor: ClassifierDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) :
    Dependency() {
    companion object {
        fun fromKotlinType(kotlinType: KotlinType): Dependency =
            kotlinType.constructor.declarationDescriptor?.let { Parent(it, kotlinType) }
                ?: Error("Parent doesn't have ClassifierDescriptor, type: $kotlinType")
    }

    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        val parent = descriptor.toString()
        return when (val status = immutability.resolveType(type)) {
            is ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> ParentTypeConditionallyDeeplyImmutable(
                        true
                    )
                    ConditionallyDeeplyImmutable.Reason.RESOLVED -> ParentTypeConditionallyDeeplyImmutable(
                        false
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is Immutable -> ImmutabilityProperty.Immutable()
            is Mutable -> {
                val reason = when (status.reason) {
                    Mutable.Reason.ASSUMPTION -> ParentTypeMutable(
                        ParentTypeMutable.Type.MUTABLE_BY_ASSUMPTION,
                        parent
                    )
                    Mutable.Reason.UNKNOWN -> ParentTypeMutable(
                        ParentTypeMutable.Type.UNKNOWN,
                        parent
                    )
                    Mutable.Reason.RESOLVED -> ParentTypeMutable(
                        ParentTypeMutable.Type.MUTABLE,
                        parent
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
            is ShallowImmutable -> {
                val reason = when (status.reason) {
                    ShallowImmutable.Reason.ASSUMPTION -> ParentTypeShallowImmutable(
                        true,
                        parent
                    )
                    ShallowImmutable.Reason.RESOLVED -> ParentTypeShallowImmutable(
                        false,
                        parent
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
