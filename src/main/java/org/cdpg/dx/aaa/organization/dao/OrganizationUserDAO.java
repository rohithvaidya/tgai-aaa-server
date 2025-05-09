package org.cdpg.dx.aaa.organization.dao;

import io.vertx.core.Future;
import org.cdpg.dx.aaa.organization.models.Organization;
import org.cdpg.dx.aaa.organization.models.OrganizationUser;
import org.cdpg.dx.aaa.organization.models.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface OrganizationUserDAO {

    Future<Boolean> updateRole(UUID orgId, UUID userId, Role role);

    Future<OrganizationUser> create(OrganizationUser organization);

    Future<Boolean> delete(UUID orgId, UUID userId);

    Future<Boolean> deleteUsersByOrgId(UUID orgId, List<UUID> uuids);

    Future<List<OrganizationUser>> getAll(UUID orgId);

    Future<Boolean> isOrgAdmin(UUID orgid, UUID userid);

    Future<OrganizationUser> getById(UUID userId);
}
