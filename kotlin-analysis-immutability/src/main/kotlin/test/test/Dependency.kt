package test.test

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.types.KotlinType

sealed class Dependency {
    data class DebugType(val debug: Any?) : Dependency()

    data class ValTo(val desc: VariableDescriptor, val type: KotlinType, val debug: List<Any?> = listOf()) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: VariableDescriptor): Dependency =
                Dependency.ValTo(desc, desc.type, listOf(desc.type.arguments))
        }
    }

    data class VarTo(val desc: VariableDescriptor, val type: KotlinType, val debug: List<Any?> = listOf()) :
        Dependency() {
        companion object {
            fun fromDescriptor(desc: VariableDescriptor): Dependency =
                Dependency.VarTo(desc, desc.type, listOf(desc.type.arguments))
        }
    }

    data class Parent(val parent: ClassifierDescriptor, val type: KotlinType, val debug: List<Any?> = listOf()) :
        Dependency() {
        companion object {
            fun fromKotlinType(kotlinType: KotlinType): Dependency =
                kotlinType.constructor.declarationDescriptor?.let { Parent(it, kotlinType) }
                    ?: Error("Parent doesn't have ClassifierDescriptor, type: $kotlinType")
        }
    }

    data class Outer(val outer: ClassifierDescriptor, val debug: List<Any?> = listOf()) : Dependency() {
        companion object {
        }
    }

    data class Error(val reason: Any?, val debug: List<Any?> = listOf()) : Dependency()

} typealias Dependencies = List<Dependency>
