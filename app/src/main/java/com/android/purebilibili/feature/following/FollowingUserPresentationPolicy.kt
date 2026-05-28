package com.android.purebilibili.feature.following

import com.android.purebilibili.data.model.response.OfficialVerify
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val FollowingSinceFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

enum class FollowingOfficialVerifyBadgeTone {
    PERSONAL,
    ORGANIZATION
}

data class FollowingOfficialVerifyBadge(
    val text: String,
    val tone: FollowingOfficialVerifyBadgeTone
)

internal fun resolveFollowingOfficialVerifyBadge(
    officialVerify: OfficialVerify
): FollowingOfficialVerifyBadge? {
    return when (officialVerify.type) {
        0 -> FollowingOfficialVerifyBadge(
            text = officialVerify.desc.ifBlank { "个人认证" },
            tone = FollowingOfficialVerifyBadgeTone.PERSONAL
        )
        1 -> FollowingOfficialVerifyBadge(
            text = officialVerify.desc.ifBlank { "机构认证" },
            tone = FollowingOfficialVerifyBadgeTone.ORGANIZATION
        )
        else -> null
    }
}

internal fun formatFollowingSinceLabel(
    mtimeSeconds: Long,
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    if (mtimeSeconds <= 0L) return ""
    val date = Instant.ofEpochSecond(mtimeSeconds).atZone(zoneId).toLocalDate()
    return "关注于 ${FollowingSinceFormatter.format(date)}"
}
