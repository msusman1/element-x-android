/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.features.analytics.test

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent
import im.vector.app.features.analytics.itf.VectorAnalyticsScreen
import im.vector.app.features.analytics.plan.UserProperties
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analyticsproviders.api.AnalyticsProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAnalyticsService(
    isEnabled: Boolean = false,
    didAskUserConsent: Boolean = false
): AnalyticsService {

    private var isEnabledFlow = MutableStateFlow(isEnabled)
    private var didAskUserConsentFlow = MutableStateFlow(didAskUserConsent)

    override fun getAvailableAnalyticsProviders(): List<AnalyticsProvider> = emptyList()

    override fun getUserConsent(): Flow<Boolean> = isEnabledFlow

    override suspend fun setUserConsent(userConsent: Boolean) {
        isEnabledFlow.value = userConsent
    }

    override fun didAskUserConsent(): Flow<Boolean> = didAskUserConsentFlow

    override suspend fun setDidAskUserConsent() {
        didAskUserConsentFlow.value = true
    }

    override fun getAnalyticsId(): Flow<String> = MutableStateFlow("")

    override suspend fun setAnalyticsId(analyticsId: String) {
    }

    override suspend fun onSignOut() {
    }

    override fun capture(event: VectorAnalyticsEvent) {
    }

    override fun screen(screen: VectorAnalyticsScreen) {
    }

    override fun updateUserProperties(userProperties: UserProperties) {
    }

    override fun trackError(throwable: Throwable) {
    }
}