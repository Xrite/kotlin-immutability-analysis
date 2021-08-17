package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.shallow_immutable.SealedSubclassShallowImmutable
import test.test.reasons.conditionally_deeply_immutable.SealedSubclassConditionallyDeeplyImmutable
import test.test.reasons.mutable.SealedSubclassMutable

class SealedSubclass(
    val descriptor: ClassifierDescriptor,
    val type: KotlinType,
    val debug: List<Any?> = listOf()
) : Dependency() {
    companion object {
        fun fromDescriptor(desc: ClassifierDescriptor): Dependency =
            SealedSubclass(desc, desc.defaultType)
    }

    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty {
        return when (val result = resolve(type)) {
            is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                val reason = when (result.reason) {
                    ImmutabilityMap.Result.ConditionallyDeeplyImmutable.Reason.ASSUMPTION -> SealedSubclassConditionallyDeeplyImmutable(
                        SealedSubclassConditionallyDeeplyImmutable.Type.CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION, descriptor.toString())
                    ImmutabilityMap.Result.ConditionallyDeeplyImmutable.Reason.RESOLVED -> SealedSubclassConditionallyDeeplyImmutable(
                        SealedSubclassConditionallyDeeplyImmutable.Type.CONDITIONALLY_DEEPLY_IMMUTABLE, descriptor.toString())
                }
                return ImmutabilityProperty.ConditionallyDeeplyImmutable(result.conditions, reason)
            }
            is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
            is ImmutabilityMap.Result.Mutable -> {
                val reason = when (result.reason) {
                    ImmutabilityMap.Result.Mutable.Reason.ASSUMPTION -> SealedSubclassMutable(SealedSubclassMutable.Type.MUTABLE_BY_ASSUMPTION, descriptor.toString())
                    ImmutabilityMap.Result.Mutable.Reason.UNKNOWN -> SealedSubclassMutable(SealedSubclassMutable.Type.UNKNOWN, descriptor.toString())
                    ImmutabilityMap.Result.Mutable.Reason.RESOLVED -> SealedSubclassMutable(SealedSubclassMutable.Type.MUTABLE, descriptor.toString())
                }
                return ImmutabilityProperty.Mutable(reason)
            }
            is ImmutabilityMap.Result.ShallowImmutable -> {
                val reason = when (result.reason) {
                    ImmutabilityMap.Result.ShallowImmutable.Reason.ASSUMPTION -> SealedSubclassShallowImmutable(
                        SealedSubclassShallowImmutable.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION, descriptor.toString())
                    ImmutabilityMap.Result.ShallowImmutable.Reason.RESOLVED -> SealedSubclassShallowImmutable(
                        SealedSubclassShallowImmutable.Type.SHALLOW_IMMUTABLE, descriptor.toString())
                }
                return ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
