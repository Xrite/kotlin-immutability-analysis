package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
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
