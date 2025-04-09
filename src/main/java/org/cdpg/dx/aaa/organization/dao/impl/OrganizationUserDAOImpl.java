package org.cdpg.dx.aaa.organization.dao.impl;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Future;
import org.cdpg.dx.aaa.organization.dao.OrganizationUserDAO;
import org.cdpg.dx.aaa.organization.models.OrganizationUser;
import org.cdpg.dx.aaa.organization.util.Role;
import org.cdpg.dx.database.postgres.service.PostgresService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrganizationUserDAOImpl implements OrganizationUserDAO{
  private final PostgresService postgresService;
  public OrganizationUserDAOImpl(PostgresService postgresService) {
    this.postgresService = postgresService;
  }

  @Override
  public Future<Boolean> updateRole(UUID orgId, UUID userId, Role role)
  {
    return Future.succeededFuture(true);
  }

  @Override
  public Future<Boolean> delete(UUID orgId, UUID userId)
  {
    return Future.succeededFuture(true);
  }

  @Override
  public Future<Boolean> deleteUsersByOrgId(UUID orgId, ArrayList<UUID> uuids) {
    return null;
  }


  @Override
  public Future<List<OrganizationUser>> get(UUID orgId) {
  return null;
  }



}
