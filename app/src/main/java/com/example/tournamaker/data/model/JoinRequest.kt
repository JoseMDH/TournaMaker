package com.example.tournamaker.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Enum to define the type of request
enum class RequestType {
    TEAM_JOIN, TOURNAMENT_JOIN
}

// Enum to define the status of the request
enum class RequestStatus {
    PENDING, ACCEPTED, DECLINED
}

data class JoinRequest(
    val id: String = "",
    val type: RequestType = RequestType.TEAM_JOIN,
    val requesterId: String = "",      // ID of the user/team making the request
    val requesterName: String = "",    // Name of the user/team
    val targetId: String = "",         // ID of the team/tournament being joined
    val targetName: String = "",       // Name of the team/tournament being joined
    val ownerId: String = "",          // ID of the user who needs to approve the request
    val status: RequestStatus = RequestStatus.PENDING,
    @ServerTimestamp
    val timestamp: Date? = null
) {
    // No-argument constructor for Firestore deserialization
    constructor() : this(
        id = "",
        type = RequestType.TEAM_JOIN,
        requesterId = "",
        requesterName = "",
        targetId = "",
        targetName = "",
        ownerId = "",
        status = RequestStatus.PENDING,
        timestamp = null
    )
}