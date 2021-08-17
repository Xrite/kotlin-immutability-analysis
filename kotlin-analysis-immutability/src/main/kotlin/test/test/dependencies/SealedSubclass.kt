package test.test.dependencies

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.MutableReason
import test.test.reasons.ShallowImmutableReason

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
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.SealedSubclassConditionallyDeeplyImmutable(ConditionallyDeeplyImmutableReason.SealedSubclassConditionallyDeeplyImmutable.Type.CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION, descriptor.toString())
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("con")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.SealedSubclassConditionallyDeeplyImmutable(ConditionallyDeeplyImmutableReason.SealedSubclassConditionallyDeeplyImmutable.Type.CONDITIONALLY_DEEPLY_IMMUTABLE, descriptor.toString())
                }
                return ImmutabilityProperty.ConditionallyDeeplyImmutable(result.conditions, reason)
            }
            is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
            is ImmutabilityMap.Result.Mutable -> {
                val reason = when (result.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> MutableReason.SealedSubclassMutable(MutableReason.SealedSubclassMutable.Type.MUTABLE_BY_ASSUMPTION, descriptor.toString())
                    ImmutabilityMap.Result.Reason.UNKNOWN -> MutableReason.SealedSubclassMutable(MutableReason.SealedSubclassMutable.Type.UNKNOWN, descriptor.toString())
                    ImmutabilityMap.Result.Reason.RESOLVED -> MutableReason.SealedSubclassMutable(MutableReason.SealedSubclassMutable.Type.MUTABLE, descriptor.toString())
                }
                return ImmutabilityProperty.Mutable(reason)
            }
            is ImmutabilityMap.Result.ShallowImmutable -> {
                val reason = when (result.reason) {
                    ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.SealedSubclassShallowImmutable(ShallowImmutableReason.SealedSubclassShallowImmutable.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION, descriptor.toString())
                    ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("Shallow immutable subclass unknown")
                    ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.SealedSubclassShallowImmutable(ShallowImmutableReason.SealedSubclassShallowImmutable.Type.SHALLOW_IMMUTABLE, descriptor.toString())
                }
                return ImmutabilityProperty.ShallowImmutable(reason)
            }
        }
    }
}
