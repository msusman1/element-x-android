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

package io.element.android.libraries.deeplink

import android.content.Intent
import android.net.Uri
import io.element.android.libraries.matrix.api.core.asRoomId
import io.element.android.libraries.matrix.api.core.asSessionId
import io.element.android.libraries.matrix.api.core.asThreadId
import javax.inject.Inject

class DeeplinkParser @Inject constructor() {
    fun getFromIntent(intent: Intent): DeeplinkData? {
        return intent
            .takeIf { it.action == Intent.ACTION_VIEW }
            ?.data
            ?.toDeeplinkData()
    }

    private fun Uri.toDeeplinkData(): DeeplinkData? {
        if (scheme != "elementx") return null
        if (host != "open") return null
        val pathBits = path.orEmpty().split("/").drop(1)
        val sessionId = pathBits.elementAtOrNull(0)?.asSessionId() ?: return null
        val roomId = pathBits.elementAtOrNull(1)?.asRoomId()
        val threadId = pathBits.elementAtOrNull(2)?.asThreadId()
        return DeeplinkData(
            sessionId = sessionId,
            roomId = roomId,
            threadId = threadId,
        )
    }
}
