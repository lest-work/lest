package com.lest.modules.release.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements com.lest.common.base.ErrorCode {
    RELEASE_NOT_FOUND(7500, "Release plan not found"),
    RELEASE_CANNOT_DELETE(7501, "Cannot delete release plan"),
    RELEASE_CANNOT_DELETE_RELEASED(7502, "Cannot delete a released plan"),
    RELEASE_ALREADY_RELEASED(7503, "Release has already been released"),
    RELEASE_ALREADY_BUILDING(7504, "Release is already building"),
    RELEASE_NOT_BUILDING(7505, "Release is not in building state"),
    RELEASE_NOT_ARCHIVED(7506, "Release is not archived"),
    ARTIFACT_NOT_FOUND(7510, "Artifact not found"),
    ARTIFACT_UPLOAD_FAILED(7511, "Artifact upload failed"),
    ARTIFACT_TYPE_NOT_SUPPORTED(7512, "Artifact type not supported"),
    ISSUE_NOT_FOUND(7520, "Release issue not found"),
    ISSUE_ALREADY_EXISTS(7521, "Issue already associated with this release"),
    WEBHOOK_NOT_FOUND(7530, "Webhook not found"),
    WEBHOOK_DELIVERY_FAILED(7531, "Webhook delivery failed"),
    INVALID_RELEASE_DATE(7540, "Invalid release date"),
    RELEASE_DATE_IN_PAST(7541, "Release date cannot be in the past"),
    INVALID_BUILD_NUMBER(7542, "Invalid build number"),
    PROJECT_NOT_FOUND(7550, "Project not found"),
    PERMISSION_DENIED(7600, "Permission denied"),
    UNAUTHORIZED(7601, "Unauthorized"),
    SYSTEM_ERROR(7700, "System error");

    private final int code;
    private final String message;
}
