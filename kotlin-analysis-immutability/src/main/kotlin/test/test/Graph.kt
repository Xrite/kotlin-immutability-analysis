package test.test

fun makeIndex(entities: List<Entity>) = entities.mapNotNull { when(it) {
        is ClassTemplate -> it.desc to it
        ErrorTemplate -> null
        is ObjectTemplate -> it.desc to it
    else -> null
} }.toMap()



