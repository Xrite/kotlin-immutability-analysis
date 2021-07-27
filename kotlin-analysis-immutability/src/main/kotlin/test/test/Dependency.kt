package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.types.KotlinType

sealed class Dependency {
    data class DebugType(val debug: List<Any?>) : Dependency()

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
    }

    // TODO: rename to a better name (ValProperty?)
    data class ValTo(
        val desc: VariableDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: VariableDescriptor): Dependency =
                ValTo(desc, desc.type, listOf(desc.type.arguments))
        }
    }

    // TODO: rename to a better name (VarProperty?)
    data class VarTo(
        val desc: VariableDescriptor,
        val type: KotlinType,
        val debug: List<Any?> = listOf()
    ) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: VariableDescriptor): Dependency =
                VarTo(desc, desc.type, listOf(desc.type.arguments))
        }
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
    }

    data class Outer(
        val outer: ClassifierDescriptor,
        val debug: List<Any?> = listOf()
    ) : Dependency()

    data class Error(
        val reason: Any?,
        val debug: List<Any?> = listOf()
    ) : Dependency()
}

typealias Dependencies = List<Dependency>
