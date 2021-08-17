package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.shallow_immutable.ParentTypeShallowImmutable
import test.test.reasons.conditionally_deeply_immutable.ParentTypeConditionallyDeeplyImmutable
import test.test.reasons.mutable.ParentTypeMutable

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

    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty {
        val parent = descriptor.toString()
        return when (val status = resolve(type)) {
            is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> ParentTypeConditionallyDeeplyImmutable(
                        true
                    )
                    ImmutabilityMap.Result.ConditionallyDeeplyImmutable.Reason.RESOLVED -> ParentTypeConditionallyDeeplyImmutable(
                        false
                    )
                }
                ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
            }
            is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
            is ImmutabilityMap.Result.Mutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.Mutable.Reason.ASSUMPTION -> ParentTypeMutable(
                        ParentTypeMutable.Type.MUTABLE_BY_ASSUMPTION,
                        parent
                    )
                    ImmutabilityMap.Result.Mutable.Reason.UNKNOWN -> ParentTypeMutable(
                        ParentTypeMutable.Type.UNKNOWN,
                        parent
                    )
                    ImmutabilityMap.Result.Mutable.Reason.RESOLVED -> ParentTypeMutable(
                        ParentTypeMutable.Type.MUTABLE,
                        parent
                    )
                }
                ImmutabilityProperty.Mutable(reason)
            }
            is ImmutabilityMap.Result.ShallowImmutable -> {
                val reason = when (status.reason) {
                    ImmutabilityMap.Result.ShallowImmutable.Reason.ASSUMPTION -> ParentTypeShallowImmutable(
                        true,
                        parent
                    )
                    ImmutabilityMap.Result.ShallowImmutable.Reason.RESOLVED -> ParentTypeShallowImmutable(
                        false,
                        parent
                    )
                }
                ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
