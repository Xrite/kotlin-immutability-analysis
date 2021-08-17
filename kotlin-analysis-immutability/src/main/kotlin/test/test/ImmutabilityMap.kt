package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import test.test.reasons.MutableReason
import kotlin.IllegalArgumentException

class ImmutabilityMap(private val entities: List<Entity>, private vararg val assumptions: Assumptions) {
    sealed class Result {
        class Immutable(val reason: Reason = Reason.RESOLVED) : Result() {
            override fun toString(): String {
                return "Immutable"
            }
            enum class Reason {
                ASSUMPTION,
                RESOLVED
            }
        }

        class ShallowImmutable(val reason: Reason = Reason.RESOLVED) : Result() {
            override fun toString(): String {
                return "ShallowImmutable"
            }
            enum class Reason {
                ASSUMPTION,
                RESOLVED
            }
        }

        class ConditionallyDeeplyImmutable(val conditions: Set<Int>, val reason: Reason = Reason.RESOLVED) :
            Result() {
            constructor(condition: Int, reason: Reason = Reason.RESOLVED) : this(setOf(condition), reason)
            enum class Reason {
                ASSUMPTION,
                RESOLVED
            }
        }

        class Mutable(val reason: Reason = Reason.RESOLVED) : Result() {
            override fun toString(): String {
                return "Mutable"
            }
            enum class Reason {
                ASSUMPTION,
                UNKNOWN,
                RESOLVED
            }
        }

    }


    // TODO: maybe use Set here to speed things up???
    fun join(results: List<Result>): Result = when {
        results.anyIsInstance<Result.Mutable>() -> Result.Mutable()
        results.anyIsInstance<Result.ShallowImmutable>() -> Result.ShallowImmutable()
        results.anyIsInstance<Result.ConditionallyDeeplyImmutable>() -> {
            val conditions = results
                .filterIsInstance<Result.ConditionallyDeeplyImmutable>()
                .flatMap { it.conditions }
                .toSet()
            Result.ConditionallyDeeplyImmutable(conditions)
        }
        else -> Result.Immutable()
    }

    private val map: MutableMap<DeclarationDescriptor, ImmutabilityProperty> = entities.mapNotNull {
        when (it) {
            is ClassTemplate -> it.desc
            ErrorTemplate -> null
        }
    }.associateWith { ImmutabilityProperty.Immutable() }.toMutableMap()

    inner class Resolver(context: List<TypeParameterDescriptor>) {
        private val indices = context.associate { it.original.typeConstructor to it.index }

        private fun index(projection: TypeProjection) = indices[projection.type.constructor]

        operator fun invoke(
            descriptor: ClassifierDescriptor,
            parameters: List<TypeProjection>
        ): Result = mapWithAssumptions(descriptor)?.let {
            when (it) {
                is ImmutabilityProperty.ConditionallyDeeplyImmutable -> {
                    join(it.conditions.map { i ->
                        when {
                            parameters[i].isStarProjection -> Result.Mutable()
                            parameters[i].projectionKind == Variance.OUT_VARIANCE -> Result.Mutable()
                            parameters[i].type.isTypeParameter() -> {
                                val idx = index(parameters[i])
                                if (idx != null) {
                                    Result.ConditionallyDeeplyImmutable(idx)
                                } else {
                                    throw IllegalArgumentException("Can't get index of ${parameters[i]}")
                                }
                            }
                            else -> {
                                when (val t = invoke(parameters[i].type)) {
                                    is Result.ConditionallyDeeplyImmutable -> t
                                    is Result.Immutable -> t
                                    is Result.Mutable -> Result.ShallowImmutable()
                                    is Result.ShallowImmutable -> Result.ShallowImmutable()
                                }
                            }
                        }
                    })
                }
                is ImmutabilityProperty.Immutable -> Result.Immutable(if (it.isByAssumption()) Result.Immutable.Reason.ASSUMPTION else Result.Immutable.Reason.RESOLVED)
                is ImmutabilityProperty.Mutable -> Result.Mutable(if (it.isByAssumption()) Result.Mutable.Reason.ASSUMPTION else Result.Mutable.Reason.RESOLVED)
                is ImmutabilityProperty.ShallowImmutable -> Result.ShallowImmutable(if (it.isByAssumption()) Result.ShallowImmutable.Reason.ASSUMPTION else Result.ShallowImmutable.Reason.RESOLVED)
            }
        } ?: Result.Mutable(Result.Mutable.Reason.UNKNOWN)

        operator fun invoke(
            type: KotlinType
        ): Result =
            if (type.isTypeParameter()) {
                val idx = index(type.asTypeProjection())
                if (idx != null) {
                    Result.ConditionallyDeeplyImmutable(idx)
                } else {
                    throw IllegalArgumentException("Can't get index of $type")
                }
            } else {
                null
            }
                ?: type.constructor.declarationDescriptor?.let { invoke(it, type.arguments) }
                ?: Result.Mutable(Result.Mutable.Reason.UNKNOWN)

    }

    private fun checkAssumptions(name: String): ImmutabilityProperty? {
        val t = assumptions.mapNotNull { it.get(name) }
        if (t.isNotEmpty()) {
            return join(t)
        }
        return null
    }

    private fun mapWithAssumptions(descriptor: DeclarationDescriptor): ImmutabilityProperty? =
        checkAssumptions(descriptor.fqNameSafe.asString()) ?: map[descriptor]

    operator fun set(entity: Entity, newValue: ImmutabilityProperty): Boolean =
        when (entity) {
            is ClassTemplate -> set(entity.desc, newValue)
            ErrorTemplate -> false
        }

    operator fun set(descriptor: DeclarationDescriptor, newValue: ImmutabilityProperty): Boolean {
        val oldValue = map[descriptor]
        if (oldValue != null && oldValue hasSameStatus newValue) {
            return false
        }
        map[descriptor] = newValue
        return true
    }

    operator fun get(descriptor: DeclarationDescriptor): ImmutabilityProperty? = mapWithAssumptions(descriptor)

    operator fun get(entity: Entity): ImmutabilityProperty? = when (entity) {
        is ClassTemplate -> get(entity.desc)
        ErrorTemplate -> null
    }

    override fun toString(): String = map.map {
        it.key.fqNameSafe.asString() + " -> " + it.value.toString()
    }.joinToString(separator = "\n")

    fun results(): List<Pair<Entity, ImmutabilityProperty>> =
        entities.mapNotNull { entity ->
            when (entity) {
                is ClassTemplate -> map[entity.desc]?.let { entity to it }
                ErrorTemplate -> null
            }
        }

    fun resultsForEntities(): List<Pair<Entity, ImmutabilityProperty?>> =
        entities.map { entity ->
            when (entity) {
                is ClassTemplate -> entity to map[entity.desc]
                ErrorTemplate -> entity to null
            }
        }

    fun unresolvedEntities(): List<Entity> =
        entities.filter {
            when (it) {
                is ClassTemplate -> map[it.desc] == null
                ErrorTemplate -> true
            }
        }
}
