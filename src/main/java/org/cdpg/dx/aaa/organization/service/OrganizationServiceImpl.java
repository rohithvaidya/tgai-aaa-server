package org.cdpg.dx.aaa.organization.service;

import io.vertx.core.Future;
import org.cdpg.dx.aaa.organization.dao.*;
import org.cdpg.dx.aaa.organization.models.*;
import org.cdpg.dx.aaa.organization.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationCreateRequestDAO createRequestDAO;
    private final OrganizationUserDAO orgUserDAO;
    private final OrganizationDAO orgDAO;
    private final OrganizationJoinRequestDAO joinRequestDAO;

    public OrganizationServiceImpl(OrganizationDAOFactory factory) {
        this.createRequestDAO = factory.organizationCreateRequest();
        this.orgUserDAO = factory.organizationUserDAO();
        this.orgDAO = factory.organizationDAO();
        this.joinRequestDAO = factory.organizationJoinRequestDAO();
    }

    @Override
    public Future<OrganizationCreateRequest> createOrganizationRequest(OrganizationCreateRequest request) {
        return createRequestDAO.create(request);
    }

    @Override
    public Future<List<OrganizationCreateRequest>> getAllPendingOrganizationCreateRequests() {
        return createRequestDAO.getAllByStatus(Status.PENDING);
    }

    @Override
    public Future<OrganizationCreateRequest> getOrganizationCreateRequests(UUID requestId) {
        return createRequestDAO.getById(requestId);
    }

    @Override
    public Future<Boolean> updateOrganizationCreateRequestStatus(UUID requestId, Status status) {
      return createRequestDAO.updateStatus(requestId, status)
                .compose(approved -> {
                    if (!approved) return Future.succeededFuture(false);
                    if (Status.APPROVED.getStatus().equals(status.getStatus())) {
                      return createOrganizationFromRequest(requestId);
                    }
                    return Future.succeededFuture(true);
                });
    }


    private Future<Boolean> createOrganizationFromRequest(UUID requestId) {
      return createRequestDAO.getById(requestId)
        .compose(request -> {
          Organization org = new Organization(
            Optional.empty(),
              request.name(),
              request.logoPath(),
              request.entityType(),
              request.orgSector(),
              request.websiteLink(),
              request.address(),
              request.certificatePath(),
              request.pancardPath(),
              request.relevantDocPath(),
            Optional.empty(),
            Optional.empty()
          );
          return orgDAO.create(org)
            .compose(createdOrg ->
            {
              OrganizationUser orgUser = new OrganizationUser(
                Optional.empty(),
                createdOrg.id().get(),
                request.requestedBy(),
                Role.ADMIN,
                request.jobTitle(),
                request.empId(),
                Optional.ofNullable(request.orgManagerphoneNo()),
                Optional.empty(),
                Optional.empty()
              );

              return orgUserDAO.create(orgUser).map(user -> true);

            });
        });
    }

    @Override
    public Future<Organization> getOrganizationById(UUID orgId) {
        return orgDAO.get(orgId);
    }

    @Override
    public Future<List<Organization>> getOrganizations() {
        return orgDAO.getAll();
    }

    @Override
    public Future<Organization> updateOrganizationById(UUID orgId, UpdateOrgDTO updateOrgDTO) {
        return orgDAO.update(orgId, updateOrgDTO);
    }

    @Override
    public Future<Boolean> deleteOrganization(UUID orgId) {
      return orgDAO.delete(orgId);
    }

    @Override
    public Future<OrganizationJoinRequest> joinOrganizationRequest(OrganizationJoinRequest organizationJoinRequest) {
        return joinRequestDAO.join(organizationJoinRequest);
    }


    @Override
    public Future<Boolean> updateOrganizationJoinRequestStatus(UUID requestId, Status status) {
        return joinRequestDAO.updateStatus(requestId, status)
                .compose(approved -> {
                    if (!approved) return Future.succeededFuture(false);
                    if (Status.APPROVED.getStatus().equals(status.getStatus())) {
                        return addUserToOrganizationFromRequest(requestId);
                    }
                    return Future.succeededFuture(true);
                });
    }

    private Future<Boolean> addUserToOrganizationFromRequest(UUID requestId) {
      return joinRequestDAO.getById(requestId)
                .compose(request -> {
                    OrganizationUser orgUser = new OrganizationUser(
                      Optional.empty(),
                      request.organizationId(),
                      request.userId(),
                      Role.USER,
                      request.jobTitle(),
                      request.empId(),
                      Optional.empty(),
                      Optional.empty(),
                      Optional.empty()
                    );
                    return orgUserDAO.create(orgUser).map(created -> true);
                });
    }

    @Override
    public Future<List<OrganizationJoinRequest>> getOrganizationPendingJoinRequests(UUID orgId) {
        return joinRequestDAO.getAll(orgId, Status.PENDING);
    }

    @Override
    public Future<List<OrganizationUser>> getOrganizationUsers(UUID orgId) {
        return orgUserDAO.getAll(orgId);
    }

    @Override
    public Future<Boolean> updateUserRole(UUID orgId, UUID userId, Role role) {
        return orgUserDAO.updateRole(orgId, userId, role);
    }

  @Override
  public Future<Boolean> isOrgAdmin(UUID orgid, UUID userid) {
    return orgUserDAO.isOrgAdmin(orgid,userid);
  }

  @Override
    public Future<Boolean> deleteOrganizationUser(UUID orgId, UUID userId) {
        return orgUserDAO.delete(orgId, userId);
    }

    @Override
    public Future<Boolean> deleteOrganizationUsers(UUID orgId, List<UUID> userIds) {
        return orgUserDAO.deleteUsersByOrgId(orgId, userIds);
    }

    @Override
    public Future<OrganizationUser> getOrganizationUserInfo(UUID userId) {
        return orgUserDAO.getById(userId);
    }
}
