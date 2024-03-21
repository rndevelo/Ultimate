package com.rndeveloper.ultimate.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenRouteService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseService
