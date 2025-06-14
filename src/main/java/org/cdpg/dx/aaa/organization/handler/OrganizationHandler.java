package org.cdpg.dx.aaa.organization.handler;


import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdpg.dx.aaa.organization.models.*;
import org.cdpg.dx.aaa.organization.service.OrganizationService;
import org.cdpg.dx.aaa.organization.util.ProviderRoleRequestMapper;
import org.cdpg.dx.aaa.user.service.UserService;
import org.cdpg.dx.common.exception.DxForbiddenException;
import org.cdpg.dx.common.exception.DxNotFoundException;
import org.cdpg.dx.common.response.ResponseBuilder;
import org.cdpg.dx.common.util.RequestHelper;

import java.util.List;
import java.util.UUID;


public class OrganizationHandler {

    private static final Logger LOGGER = LogManager.getLogger(OrganizationHandler.class);
    private final OrganizationService organizationService;
    private final UserService userService;


    public OrganizationHandler(OrganizationService organizationService, UserService userService) {
        this.organizationService = organizationService;
        this.userService = userService;
    }

    public void updateOrganisationById(RoutingContext ctx) {

        UUID orgId = RequestHelper.getPathParamAsUUID(ctx, "id");
        UpdateOrgDTO updateOrgDTO = RequestHelper.parseBody(ctx, UpdateOrgDTO::fromJson);

        organizationService.updateOrganizationById(orgId, updateOrgDTO)
                .onSuccess(updatedOrg -> ResponseBuilder.sendSuccess(ctx, updatedOrg))
                .onFailure(err -> {
                    LOGGER.error("Failed to Update Organization id: {}, message: {}", orgId, err.getMessage(), err);
                    ctx.fail(err);
                });
    }

    public void deleteOrganisationById(RoutingContext ctx) {

        UUID orgId = RequestHelper.getPathParamAsUUID(ctx, "id");
        organizationService.deleteOrganization(orgId)
                .onSuccess(updatedOrg -> ResponseBuilder.sendSuccess(ctx, "Organisation deleted Successfully!"))
                .onFailure(ctx::fail);

    }

    public void listAllOrganisations(RoutingContext ctx) {

        organizationService.getOrganizations()
                .onSuccess(orgs -> {
                    ResponseBuilder.sendSuccess(ctx, orgs);
                })
                .onFailure(ctx::fail);

    }

    public void approveJoinOrganisationRequests(RoutingContext ctx) {

        JsonObject OrgRequestJson = ctx.body().asJsonObject();

        UUID requestId = RequestHelper.getPathParamAsUUID(ctx, "req_id");

        Status status = Status.fromString(OrgRequestJson.getString("status"));


        organizationService.updateOrganizationJoinRequestStatus(requestId, status)
                .onSuccess(approved -> {
                    if (approved) {
                        ResponseBuilder.sendSuccess(ctx,  "Approved Organisation Join Request");
                    } else {
                        ctx.fail(new DxNotFoundException("Request Not Found"));
                    }
                })
                .onFailure(ctx::fail);

    }

    public void getJoinOrganisationRequests(RoutingContext ctx) {

        UUID orgId = RequestHelper.getPathParamAsUUID(ctx, "id");
        organizationService.getOrganizationPendingJoinRequests(orgId)
                .onSuccess(requests -> ResponseBuilder.sendSuccess(ctx, requests))
                .onFailure(ctx::fail);
    }

    public void joinOrganisationRequest(RoutingContext ctx) {

        UUID orgId = RequestHelper.getPathParamAsUUID(ctx, "id");
        JsonObject OrgRequestJson = ctx.body().asJsonObject();
        OrganizationJoinRequest organizationJoinRequest;
        User user = ctx.user();
        OrgRequestJson.put("user_id", user.subject());

        String userName = user.principal().getString("name");
        OrgRequestJson.put("user_name", userName);
        OrgRequestJson.put("organization_id", orgId.toString());

        System.out.println("OrgRequestJson: " + OrgRequestJson.encodePrettily());

        organizationJoinRequest = OrganizationJoinRequest.fromJson(OrgRequestJson);

        organizationService.joinOrganizationRequest(organizationJoinRequest)
                .onSuccess(createdRequest -> ResponseBuilder.sendSuccess(ctx, "Created Join request"))
                .onFailure(ctx::fail);

    }

    public void approveOrganisationRequest(RoutingContext ctx) {
        LOGGER.debug("Got request>>>>>>>>>>>>>>>>>>>>");
        JsonObject OrgRequestJson = ctx.body().asJsonObject();

        UUID requestId = UUID.fromString(OrgRequestJson.getString("req_id"));
        Status status = Status.fromString(OrgRequestJson.getString("status"));

        JsonObject responseObject = OrgRequestJson.copy();
        responseObject.remove("status");
        LOGGER.debug("Calling service >>>>>>>>>>>>>>>>>>>>");
        organizationService.updateOrganizationCreateRequestStatus(requestId, status)
                .onSuccess(updated -> {
                    ResponseBuilder.sendSuccess(ctx, "Updated Sucessfully");
                })
                .onFailure(ctx::fail);
    }

    public void getOrganisationRequest(RoutingContext ctx) {

        organizationService.getAllOrganizationCreateRequests()
                .onSuccess(requests -> {
                    ResponseBuilder.sendSuccess(ctx, requests);

                })
                .onFailure(ctx::fail);

    }

    public void createOrganisationRequest(RoutingContext ctx) {
        JsonObject OrgRequestJson = ctx.body().asJsonObject();


        User user = ctx.user();
        OrgRequestJson.put("requested_by", user.subject());

        String userName = user.principal().getString("name");
        OrgRequestJson.put("user_name", userName);

       OrganizationCreateRequest organizationCreateRequest = OrganizationCreateRequest.fromJson(OrgRequestJson);


        organizationService.createOrganizationRequest(organizationCreateRequest).
                onSuccess(requests -> {
                    ResponseBuilder.sendSuccess(ctx, requests);

                })
                .onFailure(ctx::fail);
    }

    public void deleteOrganisationUserById(RoutingContext ctx) {
        UUID  orgId = RequestHelper.getPathParamAsUUID(ctx, "id");
        UUID userId = RequestHelper.getPathParamAsUUID(ctx, "user_id");

        organizationService.deleteOrganizationUser(orgId, userId)
                .onSuccess(deleted -> {
                    if (deleted) {
                        ResponseBuilder.sendSuccess(ctx, "Deleted Organisation User");
                    } else {
                        ctx.fail(new DxNotFoundException( "Organisation User Not Found"));
                    }
                })
                .onFailure(ctx::fail);

    }

    public void getOrganisationUserInfo(RoutingContext ctx) {
        UUID  orgId = RequestHelper.getPathParamAsUUID(ctx, "id");
        UUID userId = RequestHelper.getPathParamAsUUID(ctx, "user_id");

        // TODO check this belogns to the org

        userService.getUserInfoByID(userId).onSuccess(users -> {
                    ResponseBuilder.sendSuccess(ctx, users);
                })
                .onFailure(ctx::fail);

    }

    public void getOrganisationUsers(RoutingContext ctx) {

        UUID orgId = RequestHelper.getPathParamAsUUID(ctx, "id");

        organizationService.getOrganizationUsers(orgId)
                .onSuccess(users -> {
                    ResponseBuilder.sendSuccess(ctx, users);
                })
                .onFailure(ctx::fail);

    }

    public void updateOrganisationUserRole(RoutingContext ctx) {

        JsonObject OrgRequestJson = ctx.body().asJsonObject();

        Role role;
        role = Role.fromString(OrgRequestJson.getString("role"));

        UUID  orgId = RequestHelper.getPathParamAsUUID(ctx, "id");
        UUID userId = RequestHelper.getPathParamAsUUID(ctx, "user_id");

        organizationService.updateUserRole(orgId, userId, role)
                .onSuccess(updated -> {
                    if (updated) {
                        ResponseBuilder.sendSuccess(ctx,"Updated Organisation User Role");
                    } else {
                        ctx.fail(new DxNotFoundException( "Organisation User Not Found"));
                    }
                })
                .onFailure(ctx::fail);;

    }

    public void createProviderRequest(RoutingContext ctx) {

        User user = ctx.user();
        LOGGER.debug("User: {}", user);
        if (user == null || user.subject() == null || user.principal() == null) {
            ctx.fail(new DxForbiddenException("User not found"));
            return;
        }

        String userId = user.subject();
        String orgID = user.principal().getString("organisation_id");

        if (userId == null || userId.isEmpty()) {
            ctx.fail(new DxForbiddenException("User not found"));
            return;
        }

        if (orgID == null || orgID.isEmpty()) {
            ctx.fail(new DxForbiddenException("User is not part any organisation"));
            return;
        }

        JsonObject req = new JsonObject().
                put("user_id", user.subject()).
                put("organization_id", orgID);

        ProviderRoleRequest providerRoleRequest = ProviderRoleRequest.fromJson(req);

        organizationService.createProviderRequest(providerRoleRequest)
                .onSuccess(requests -> ResponseBuilder.sendSuccess(ctx, "Created Request"))
                .onFailure(ctx::fail);
    }

    public void updateProviderRequest(RoutingContext ctx) {
        JsonObject OrgRequestJson = ctx.body().asJsonObject();
        UUID reqId = RequestHelper.getPathParamAsUUID(ctx, "id");
        Status status = Status.fromString(OrgRequestJson.getString("status"));

        organizationService.updateProviderRequestStatus(reqId,status)
                .onSuccess(requests -> ResponseBuilder.sendSuccess(ctx, "Provider role updated"))
                .onFailure(ctx::fail);
    }

    public void getProviderRequest(RoutingContext ctx) {
        User user = ctx.user();
        LOGGER.debug("User: {}", user);
        if (user == null || user.subject() == null || user.principal() == null) {
            ctx.fail(new DxForbiddenException("User not found"));
            return;
        }

        String userId = user.subject();
        String orgID = user.principal().getString("organisation_id");

        if (userId == null || userId.isEmpty()) {
            ctx.fail(new DxForbiddenException("User not found"));
            return;
        }

        if (orgID == null || orgID.isEmpty()) {
            ctx.fail(new DxForbiddenException("User is not part any organisation"));
            return;
        }

        organizationService.getOrganizationUserInfo(UUID.fromString(user.subject())).compose(
                        orgUser -> {
                            if (orgUser == null || orgUser.role() != Role.ADMIN) {
                                return Future.failedFuture(new DxForbiddenException("User not found or not a admin"));
                            }
                            UUID orgId = orgUser.organizationId();
                            return organizationService.getAllPendingProviderRoleRequests(orgId);
                        }
                ).compose(requests -> {
                    List<Future<JsonObject>> enrichedFutures = requests.stream().map(req ->
                            organizationService.getOrganizationUserInfo(req.userId())
                                    .map(userInfo -> ProviderRoleRequestMapper.toJsonWithOrganisationUser(req, userInfo))
                    ).toList();
                    return Future.all(enrichedFutures).map(cf -> {
                        List<JsonObject> resultList = new java.util.ArrayList<>();
                        for (int i = 0; i < cf.size(); i++) {
                            resultList.add(cf.resultAt(i));
                        }
                        return resultList;
                    });
                })
                .onSuccess(enrichedRequests -> ResponseBuilder.sendSuccess(ctx, enrichedRequests))
                .onFailure(ctx::fail);
    }

    public void getOrganizationById(RoutingContext ctx) {
        UUID orgId = RequestHelper.getPathParamAsUUID(ctx, "id");

        organizationService.getOrganizationById(orgId)
                .onSuccess(org -> ResponseBuilder.sendSuccess(ctx, org.toJson()))
                .onFailure(err -> {
                    if (err instanceof DxNotFoundException) {
                        ctx.fail(new DxNotFoundException("Organization not found with id: " + orgId));
                    } else {
                        ctx.fail(err);
                    }
                });
    }

}
