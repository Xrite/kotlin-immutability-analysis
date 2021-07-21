package test.test

inline fun <reified R> Iterable<*>.anyInstance(): Boolean = this.any { it is R }
