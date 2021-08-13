package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.types.KotlinType
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.MutableReason
import test.test.reasons.ShallowImmutableReason

sealed class Dependency {
    data class DebugType(val debug: List<Any?>) : Dependency() {
        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            ImmutabilityProperty.Immutable()
    }

    data class ValParameter(
        val desc: ValueParameterDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: ValueParameterDescriptor): Dependency =
                ValParameter(desc, desc.type, listOf(desc.type.arguments))
        }

        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            when (val status = resolve(type)) {
                is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                            byAssumption = true,
                            isParameter = true
                        )
                        ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                        ImmutabilityMap.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                            byAssumption = false,
                            isParameter = true
                        )
                    }
                    ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
                }
                is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
                is ImmutabilityMap.Result.Mutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.MUTABLE_BY_ASSUMPTION,
                            true
                        )
                        ImmutabilityMap.Result.Reason.UNKNOWN -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.UNKNOWN,
                            true
                        )
                        ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.MUTABLE,
                            true
                        )
                    }
                    ImmutabilityProperty.ShallowImmutable(reason)
                }
                is ImmutabilityMap.Result.ShallowImmutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                            true
                        )
                        ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                        ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.SHALLOW_IMMUTABLE,
                            true
                        )
                    }
                    ImmutabilityProperty.ShallowImmutable(reason)
                }
            }
    }

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

        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            ImmutabilityProperty.Mutable(MutableReason.VarProperty(true))
    }

    data class ValProperty(
        val desc: VariableDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: VariableDescriptor): Dependency =
                ValProperty(desc, desc.type, listOf(desc.type.arguments))
        }

        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            when (val status = resolve(type)) {
                is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                            byAssumption = true,
                            isParameter = false
                        )
                        ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                        ImmutabilityMap.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                            byAssumption = false,
                            isParameter = false
                        )
                    }
                    ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
                }
                is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
                is ImmutabilityMap.Result.Mutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.MUTABLE_BY_ASSUMPTION,
                            false
                        )
                        ImmutabilityMap.Result.Reason.UNKNOWN -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.UNKNOWN,
                            false
                        )
                        ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.MUTABLE,
                            false
                        )
                    }
                    ImmutabilityProperty.ShallowImmutable(reason)
                }
                is ImmutabilityMap.Result.ShallowImmutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION,
                            false
                        )
                        ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                        ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.ValProperty(
                            ShallowImmutableReason.Type.SHALLOW_IMMUTABLE,
                            false
                        )
                    }
                    ImmutabilityProperty.ShallowImmutable(reason)
                }
            }
    }

    data class VarProperty(
        val desc: VariableDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: VariableDescriptor): Dependency =
                VarProperty(desc, desc.type, listOf(desc.type.arguments))
        }

        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            ImmutabilityProperty.Mutable(MutableReason.VarProperty(false))
    }

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

        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            ImmutabilityProperty.Mutable(MutableReason.PropertyWithGetter)
    }

    data class DelegatedValProperty(
        val desc: VariableDescriptor,
        val type: KotlinType,
        val delegate: KtPropertyDelegate,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: VariableDescriptor, delegate: KtPropertyDelegate): Dependency =
                DelegatedValProperty(desc, desc.type, delegate)
        }

        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            ImmutabilityProperty.Mutable(MutableReason.DelegatedProperty)
    }

    data class Parent(
        val parent: ClassifierDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromKotlinType(kotlinType: KotlinType): Dependency =
                kotlinType.constructor.declarationDescriptor?.let { Parent(it, kotlinType) }
                    ?: Error("Parent doesn't have ClassifierDescriptor, type: $kotlinType")
        }

        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            when (val status = resolve(type)) {
                is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.ParentTypeConditionallyDeeplyImmutable(
                            true
                        )
                        ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                        ImmutabilityMap.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.ParentTypeConditionallyDeeplyImmutable(
                            false
                        )
                    }
                    ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
                }
                is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
                is ImmutabilityMap.Result.Mutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> MutableReason.ParentType(MutableReason.ParentType.Type.MUTABLE_BY_ASSUMPTION)
                        ImmutabilityMap.Result.Reason.UNKNOWN -> MutableReason.ParentType(MutableReason.ParentType.Type.UNKNOWN)
                        ImmutabilityMap.Result.Reason.RESOLVED -> MutableReason.ParentType(MutableReason.ParentType.Type.MUTABLE)
                    }
                    ImmutabilityProperty.Mutable(reason)
                }
                is ImmutabilityMap.Result.ShallowImmutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.ParentTypeShallowImmutable(true)
                        ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                        ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.ParentTypeShallowImmutable(false)
                    }
                    ImmutabilityProperty.ShallowImmutable(reason)
                }
            }
    }

    data class Outer(
        val outer: ClassifierDescriptor,
        val debug: List<Any?> = listOf()
    ) : Dependency() {
        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            when (val status = resolve(outer.defaultType)) {
                is ImmutabilityMap.Result.ConditionallyDeeplyImmutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ConditionallyDeeplyImmutableReason.OuterClassTypeConditionallyDeeplyImmutable(
                            true
                        )
                        ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                        ImmutabilityMap.Result.Reason.RESOLVED -> ConditionallyDeeplyImmutableReason.OuterClassTypeConditionallyDeeplyImmutable(
                            false
                        )
                    }
                    ImmutabilityProperty.ConditionallyDeeplyImmutable(status.conditions, reason)
                }
                is ImmutabilityMap.Result.Immutable -> ImmutabilityProperty.Immutable()
                is ImmutabilityMap.Result.Mutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> MutableReason.OuterClass(MutableReason.OuterClass.Type.MUTABLE_BY_ASSUMPTION)
                        ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("Outer class unknown")
                        ImmutabilityMap.Result.Reason.RESOLVED -> MutableReason.OuterClass(MutableReason.OuterClass.Type.MUTABLE)
                    }
                    ImmutabilityProperty.Mutable(reason)
                }
                is ImmutabilityMap.Result.ShallowImmutable -> {
                    val reason = when (status.reason) {
                        ImmutabilityMap.Result.Reason.ASSUMPTION -> ShallowImmutableReason.OuterClassShallowImmutable(true)
                        ImmutabilityMap.Result.Reason.UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                        ImmutabilityMap.Result.Reason.RESOLVED -> ShallowImmutableReason.OuterClassShallowImmutable(false)
                    }
                    ImmutabilityProperty.ShallowImmutable(reason)
                }
            }
    }

    data class Error(
        val reason: Any?,
        val debug: List<Any?> = listOf()
    ) : Dependency() {
        override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty =
            ImmutabilityProperty.Mutable(MutableReason.Error)
    }

    abstract fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty

}


typealias Dependencies = List<Dependency>
