/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.fixtures.fakes

import org.matrix.rustcomponents.sdk.NoPointer
import org.matrix.rustcomponents.sdk.RoomListService
import org.matrix.rustcomponents.sdk.SyncService

class FakeRustSyncService(
    private val roomListService: RoomListService = FakeRustRoomListService(),
) : SyncService(NoPointer) {
    override fun roomListService(): RoomListService = roomListService
}