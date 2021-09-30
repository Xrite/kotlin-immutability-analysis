package org.jetbrains.research.ml.kotlinAnalysis.immutability

fun interface Assumptions {
    fun get(name: String): ImmutabilityProperty?
}

