package com.batuhan.core.util.retrofit

import javax.inject.Qualifier

interface FirebaseQualifiers {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Management

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Firestore

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CloudMessaging
}
