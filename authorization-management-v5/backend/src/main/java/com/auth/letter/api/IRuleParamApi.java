package com.auth.letter.api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.AuthRuleParamVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.RuleParamCreateRequest;
import com.auth.letter.vo.RuleParamQueryRequest;

@Path("/api/rule-param")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IRuleParamApi {

    @POST
    @Path("/list")
    ApiResponse<PageResult<AuthRuleParamVO>> list(RuleParamQueryRequest request);

    @POST
    @Path("/create")
    ApiResponse<Long> create(RuleParamCreateRequest request);

    @PUT
    @Path("/update")
    ApiResponse<Void> update(RuleParamCreateRequest request);

    @POST
    @Path("/activate")
    ApiResponse<Void> activate(@QueryParam("id") Long id);

    @POST
    @Path("/deactivate")
    ApiResponse<Void> deactivate(@QueryParam("id") Long id);

    @POST
    @Path("/activateBatch")
    ApiResponse<Void> activateBatch(List<Long> ids);

    @POST
    @Path("/deactivateBatch")
    ApiResponse<Void> deactivateBatch(List<Long> ids);

    @GET
    @Path("/allActive")
    ApiResponse<List<AuthRuleParamVO>> allActive();
}