package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection

interface ImmutabilityWithContext : Immutability {
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

    fun resolveType(descriptor: ClassifierDescriptor, parameters: List<TypeProjection>): Result

    fun resolveType(type: KotlinType): Result

    fun resolveDescriptor(descriptor: ClassifierDescriptor): Result
}
