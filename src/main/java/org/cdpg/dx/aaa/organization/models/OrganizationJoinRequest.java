package org.cdpg.dx.aaa.organization.models;

import io.vertx.core.json.JsonObject;
import org.cdpg.dx.aaa.organization.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record OrganizationJoinRequest(
  Optional<UUID> id,
  UUID organizationId,
  UUID userId,
  String status,
  String jobTitle,
  String empId,
  Optional<String> requestedAt,
  Optional<String> processedAt
) {
  public static OrganizationJoinRequest fromJson(JsonObject orgJoinRequest) {
    return new OrganizationJoinRequest(
      Optional.ofNullable(orgJoinRequest.getString(Constants.ORG_JOIN_ID)).map(UUID::fromString),
      UUID.fromString(orgJoinRequest.getString(Constants.ORGANIZATION_ID)),
      UUID.fromString(orgJoinRequest.getString(Constants.USER_ID)),
      Optional.ofNullable(orgJoinRequest.getString(Constants.STATUS)).orElse(Status.PENDING.getStatus()),
      orgJoinRequest.getString(Constants.JOB_TITLE),
      orgJoinRequest.getString(Constants.EMP_ID),
      Optional.ofNullable(orgJoinRequest.getString(Constants.REQUESTED_AT)),
      Optional.ofNullable(orgJoinRequest.getString(Constants.PROCESSED_AT))
    );
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    id.ifPresent(value -> json.put(Constants.ORG_JOIN_ID, value.toString()));
    json.put(Constants.ORGANIZATION_ID, organizationId.toString())
      .put(Constants.USER_ID, userId.toString())
      .put(Constants.STATUS, status)
      .put(Constants.JOB_TITLE, jobTitle)
      .put(Constants.EMP_ID, empId)
      .put(Constants.REQUESTED_AT, requestedAt.orElse(null))
      .put(Constants.PROCESSED_AT, processedAt.orElse(null));
    return json;
  }

  public Map<String, Object> toNonEmptyFieldsMap() {
    Map<String, Object> map = new HashMap<>();

    id.ifPresent(uuid -> map.put(Constants.ORG_JOIN_ID, uuid.toString()));
    map.put(Constants.ORGANIZATION_ID, organizationId.toString());
    map.put(Constants.USER_ID, userId.toString());
    map.put(Constants.STATUS, status);
    map.put(Constants.JOB_TITLE, jobTitle);
    map.put(Constants.EMP_ID, empId);
    requestedAt.ifPresent(value -> map.put(Constants.REQUESTED_AT, value));
    processedAt.ifPresent(value -> map.put(Constants.PROCESSED_AT, value));

    return map;
  }
}
