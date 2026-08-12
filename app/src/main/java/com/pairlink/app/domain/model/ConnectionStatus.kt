package com.pairlink.app.domain.model

/**
 * Status of partner pairing connection for a user account.
 */
enum class ConnectionStatus {
    NOT_PAIRED,
    REQUEST_SENT,
    REQUEST_RECEIVED,
    PAIRED
}

/**
 * Status of an individual pair request.
 */
enum class PairRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELLED,
    EXPIRED
}
