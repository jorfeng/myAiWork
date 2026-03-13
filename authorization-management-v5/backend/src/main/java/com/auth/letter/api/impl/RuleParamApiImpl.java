package com.auth.letter.api.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.auth.letter.api.IRuleParamApi;
import com.auth.letter.service.RuleParamService;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.AuthRuleParamVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.RuleParamCreateRequest;
import com.auth.letter.vo.RuleParamQueryRequest;

@Component
@Path("/api/rule-param")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RuleParamApiImpl implements IRuleParamApi {

    @Autowired
    private RuleParamService ruleParamService;

    @Override
    @POST
    @Path("/list")
    public ApiResponse<PageResult<AuthRuleParamVO>> list(RuleParamQueryRequest request) {
        PageResult<AuthRuleParamVO> result = ruleParamService.queryList(request);
        return ApiResponse.success(result);
    }

    @Override
    @POST
    @Path("/create")
    public ApiResponse<Long> create(RuleParamCreateRequest request) {
        Long id = ruleParamService.create(request);
        return ApiResponse.success(id);
    }

    @Override
    @PUT
    @Path("/update")
    public ApiResponse<Void> update(RuleParamCreateRequest request) {
        ruleParamService.update(request);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/activate")
    public ApiResponse<Void> activate(@QueryParam("id") Long id) {
        ruleParamService.activate(id);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/deactivate")
    public ApiResponse<Void> deactivate(@QueryParam("id") Long id) {
        ruleParamService.deactivate(id);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/activateBatch")
    public ApiResponse<Void> activateBatch(List<Long> ids) {
        ruleParamService.activateBatch(ids);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/deactivateBatch")
    public ApiResponse<Void> deactivateBatch(List<Long> ids) {
        ruleParamService.deactivateBatch(ids);
        return ApiResponse.success(null);
    }

    @Override
    @GET
    @Path("/allActive")
    public ApiResponse<List<AuthRuleParamVO>> allActive() {
        List<AuthRuleParamVO> result = ruleParamService.getAllActive();
        return ApiResponse.success(result);
    }
}