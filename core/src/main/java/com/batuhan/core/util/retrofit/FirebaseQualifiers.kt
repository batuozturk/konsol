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

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TestLabTesting

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TestLabToolResults

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class FirebaseCloudStorage
}
