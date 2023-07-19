package com.batuhan.core.util.retrofit

import javax.inject.Qualifier

interface GoogleQualifiers {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleCloudProjects

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleAnalytics

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleCloudBilling
}
