/*
 * Copyright (c) 2022 New Vector Ltd
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

package io.element.android.x.matrix.room

import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.x.matrix.core.EventId
import io.element.android.x.matrix.core.RoomId
import io.element.android.x.matrix.timeline.MatrixTimeline
import io.element.android.x.matrix.timeline.RustMatrixTimeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import org.matrix.rustcomponents.sdk.Room
import org.matrix.rustcomponents.sdk.SlidingSyncRoom
import org.matrix.rustcomponents.sdk.UpdateSummary
import org.matrix.rustcomponents.sdk.genTransactionId
import org.matrix.rustcomponents.sdk.messageEventContentFromMarkdown

class RustMatrixRoom(
    private val slidingSyncUpdateFlow: Flow<UpdateSummary>,
    private val slidingSyncRoom: SlidingSyncRoom,
    private val room: Room,
    private val coroutineScope: CoroutineScope,
    private val coroutineDispatchers: CoroutineDispatchers,
) : MatrixRoom {

    override fun syncUpdateFlow(): Flow<Long> {
        return slidingSyncUpdateFlow
            .filter {
                it.rooms.contains(room.id())
            }
            .map {
                System.currentTimeMillis()
            }
            .onStart { emit(System.currentTimeMillis()) }
    }

    override fun timeline(): MatrixTimeline {
        return RustMatrixTimeline(
            matrixRoom = this,
            room = room,
            slidingSyncRoom = slidingSyncRoom,
            coroutineScope = coroutineScope,
            coroutineDispatchers = coroutineDispatchers
        )
    }

    override val roomId = RoomId(room.id())

    override val name: String?
        get() {
            return slidingSyncRoom.name()
        }

    override val bestName: String
        get() {
            return name?.takeIf { it.isNotEmpty() } ?: room.id()
        }

    override val displayName: String
        get() {
            return room.displayName()
        }

    override val topic: String?
        get() {
            return room.topic()
        }

    override val avatarUrl: String?
        get() {
            return room.avatarUrl()
        }

    override suspend fun userDisplayName(userId: String): Result<String?> =
        withContext(coroutineDispatchers.io) {
            runCatching {
                room.memberDisplayName(userId)
            }
        }

    override suspend fun userAvatarUrl(userId: String): Result<String?> =
        withContext(coroutineDispatchers.io) {
            runCatching {
                room.memberAvatarUrl(userId)
            }
        }

    override suspend fun sendMessage(message: String): Result<Unit> = withContext(coroutineDispatchers.io) {
        val transactionId = genTransactionId()
        val content = messageEventContentFromMarkdown(message)
        runCatching {
            room.send(content, transactionId)
        }
    }

    override suspend fun editMessage(originalEventId: EventId, message: String): Result<Unit> = withContext(coroutineDispatchers.io) {
        val transactionId = genTransactionId()
        // val content = messageEventContentFromMarkdown(message)
        runCatching {
            room.edit(/* TODO use content */ message, originalEventId.value, transactionId)
        }
    }

    override suspend fun replyMessage(eventId: EventId, message: String): Result<Unit> = withContext(coroutineDispatchers.io) {
        val transactionId = genTransactionId()
        // val content = messageEventContentFromMarkdown(message)
        runCatching {
            room.sendReply(/* TODO use content */ message, eventId.value, transactionId)
        }
    }

    override suspend fun redactEvent(eventId: EventId, reason: String?) = withContext(coroutineDispatchers.io) {
        val transactionId = genTransactionId()
        runCatching {
            room.redact(eventId.value, reason, transactionId)
        }
    }
}
