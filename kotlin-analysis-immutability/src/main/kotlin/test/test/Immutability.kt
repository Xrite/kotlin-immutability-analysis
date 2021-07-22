package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.fir.types.ProjectionKind
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

class Immutability(entities: List<Entity>, vararg assumptions: Assumptions) {
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

    fun join(results: List<Result>): Result = when {
        Result.Mutable in results -> Result.Mutable
        Result.ShallowImmutable in results -> Result.ShallowImmutable
        results.any { it is Result.ConditionallyDeeplyImmutable } -> {
            val conditions = results.mapNotNull {
                when (it) {
                    is Result.ConditionallyDeeplyImmutable -> it.conditions
                    else -> null
                }
            }.flatten().toSet()
            Result.ConditionallyDeeplyImmutable(conditions)
        }
        else -> Result.Immutable
    }

    private val map: MutableMap<DeclarationDescriptor, ImmutabilityStatus> = entities.mapNotNull {
        when (it) {
            is ClassTemplate -> it.desc
            ErrorTemplate -> null
            is ObjectTemplate -> it.desc
            //is ParameterizedClassTemplate -> it.desc
        }
    }.associateWith { ImmutabilityStatus.Immutable() }.toMutableMap()
    private val assumptions = assumptions.toList()

    operator fun get(
        descriptor: ClassifierDescriptor,
        parameters: List<TypeProjection>
    ): Result = mapWithAssumptions(descriptor)?.let {
            when (it) {
                is ImmutabilityStatus.ConditionallyDeeplyImmutable -> {
                    join(it.conditions.map { i ->
                        if (parameters[i].type.isTypeParameter()) {
                            val t = parameters[i]
                            Result.ConditionallyDeeplyImmutable(setOf(t))
                        } else {
                            when (val t = get(parameters[i].type)) {
                                is Result.ConditionallyDeeplyImmutable -> t
                                Result.Immutable -> t
                                Result.Mutable -> Result.ShallowImmutable
                                Result.ShallowImmutable -> Result.ShallowImmutable
                            }
                        }
                    })
                }
                is ImmutabilityStatus.Immutable -> Result.Immutable
                is ImmutabilityStatus.Mutable -> Result.Mutable
                is ImmutabilityStatus.ShallowImmutable -> Result.ShallowImmutable
            }
        } ?: Result.Mutable

    /*
    operator fun get(
        descriptor: ClassifierDescriptor,
        parameters: List<TypeProjection> = listOf()
    ): ImmutabilityStatus = when(val status = getStatus(descriptor, parameters)) {
            is Immutability.Result.ConditionallyDeeplyImmutable ->ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions.map {
                (it.type.constructor.declarationDescriptor as TypeParameterDescriptor).index
            }.toSet())
            Result.Immutable -> ImmutabilityStatus.Immutable
            Result.Mutable -> ImmutabilityStatus.Mutable
            Result.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable
        }
     */


    operator fun get(
        type: KotlinType
    ): Result = if (type.isTypeParameter()) {
            Result.ConditionallyDeeplyImmutable(setOf(type.asTypeProjection()))
        } else { null }
        ?: type.constructor.declarationDescriptor?.let { get(it, type.arguments) }
        ?: Result.Mutable

    /*
    operator fun get(
        type: KotlinType
    ): ImmutabilityStatus = when(val status = getStatus(type)) {
        is Immutability.Result.ConditionallyDeeplyImmutable ->ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions.map {
            (it.type.constructor.declarationDescriptor as TypeParameterDescriptor).index
        }.toSet())
        Result.Immutable -> ImmutabilityStatus.Immutable
        Result.Mutable -> ImmutabilityStatus.Mutable
        Result.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable
    }
     */

    fun checkAssumptions(name: String): ImmutabilityStatus? {
        val t = assumptions.mapNotNull { it.get(name) }
        if (t.isNotEmpty()) {
            return join(t)
        }
        return null
    }

    fun mapWithAssumptions(descriptor: DeclarationDescriptor): ImmutabilityStatus? =
        checkAssumptions(descriptor.fqNameSafe.asString()) ?: map[descriptor]

    fun update(entity: Entity, newValue: ImmutabilityStatus): Boolean =
        when (entity) {
            is ClassTemplate -> update(entity.desc, newValue)
            ErrorTemplate -> false
            is ObjectTemplate -> update(entity.desc, newValue)
        }

    fun update(descriptor: DeclarationDescriptor, newValue: ImmutabilityStatus): Boolean {
        if (map[descriptor] == newValue) {
            return false
        }
        map[descriptor] = newValue
        return true
    }

    override fun toString(): String {
        return map.map {
            it.key.fqNameSafe.asString() + " -> " + it.value.toString()
        }.joinToString(separator = "\n")
    }
}
