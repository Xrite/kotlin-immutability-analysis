package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

class Immutability(private val entities: List<Entity>, private vararg val assumptions: Assumptions) {
    sealed class Result {
        object Immutable : Result() {
            override fun toString(): String {
                return "Immutable"
            }
        }

        object ShallowImmutable : Result() {
            override fun toString(): String {
                return "ShallowImmutable"
            }
        }

        data class ConditionallyDeeplyImmutable(val conditions: Set<TypeProjection>) : Result()

        object Mutable : Result() {
            override fun toString(): String {
                return "Mutable"
            }
        }
    }

    // TODO: maybe use Set here to speed things up???
    fun join(results: List<Result>): Result = when {
        Result.Mutable in results -> Result.Mutable
        Result.ShallowImmutable in results -> Result.ShallowImmutable
        results.any { it is Result.ConditionallyDeeplyImmutable } -> {
            val conditions = results
                .filterIsInstance<Result.ConditionallyDeeplyImmutable>()
                .flatMap { it.conditions }
                .toSet()
            Result.ConditionallyDeeplyImmutable(conditions)
        }
        else -> Result.Immutable
    }

    private val map: MutableMap<DeclarationDescriptor, ImmutabilityStatus> = entities.mapNotNull {
        when (it) {
            is ClassTemplate -> it.desc
            ErrorTemplate -> null
        }
    }.associateWith { ImmutabilityStatus.Immutable() }.toMutableMap()

    operator fun get(
        descriptor: ClassifierDescriptor,
        parameters: List<TypeProjection>
    ): Result = mapWithAssumptions(descriptor)?.let {
        when (it) {
            is ImmutabilityStatus.ConditionallyDeeplyImmutable -> {
                join(it.conditions.map { i ->
                    when {
                        parameters[i].isStarProjection -> Result.Mutable
                        parameters[i].projectionKind == Variance.OUT_VARIANCE -> Result.Mutable
                        parameters[i].type.isTypeParameter() -> {
                            val t = parameters[i]
                            Result.ConditionallyDeeplyImmutable(setOf(t))
                        }
                        else -> {
                            when (val t = get(parameters[i].type)) {
                                is Result.ConditionallyDeeplyImmutable -> t
                                Result.Immutable -> t
                                Result.Mutable -> Result.ShallowImmutable
                                Result.ShallowImmutable -> Result.ShallowImmutable
                            }
                        }
                    }
                })
            }
            is ImmutabilityStatus.Immutable -> Result.Immutable
            is ImmutabilityStatus.Mutable -> Result.Mutable
            is ImmutabilityStatus.ShallowImmutable -> Result.ShallowImmutable
        }
    } ?: Result.Mutable

    operator fun get(
        type: KotlinType
    ): Result =
        if (type.isTypeParameter()) {
            Result.ConditionallyDeeplyImmutable(setOf(type.asTypeProjection()))
        } else {
            null
        }
            ?: type.constructor.declarationDescriptor?.let { get(it, type.arguments) }
            ?: Result.Mutable

    private fun checkAssumptions(name: String): ImmutabilityStatus? {
        val t = assumptions.mapNotNull { it.get(name) }
        if (t.isNotEmpty()) {
            return join(t)
        }
        return null
    }

    private fun mapWithAssumptions(descriptor: DeclarationDescriptor): ImmutabilityStatus? =
        checkAssumptions(descriptor.fqNameSafe.asString()) ?: map[descriptor]

    fun update(entity: Entity, newValue: ImmutabilityStatus): Boolean =
        when (entity) {
            is ClassTemplate -> update(entity.desc, newValue)
            ErrorTemplate -> false
        }

    fun update(descriptor: DeclarationDescriptor, newValue: ImmutabilityStatus): Boolean {
        if (map[descriptor] == newValue) {
            return false
        }
        map[descriptor] = newValue
        return true
    }

    override fun toString(): String = map.map {
        it.key.fqNameSafe.asString() + " -> " + it.value.toString()
    }.joinToString(separator = "\n")

    fun results(): List<Pair<Entity, ImmutabilityStatus>> =
        entities.mapNotNull { entity ->
            when (entity) {
                is ClassTemplate -> map[entity.desc]?.let { entity to it }
                ErrorTemplate -> null
            }
        }
}
