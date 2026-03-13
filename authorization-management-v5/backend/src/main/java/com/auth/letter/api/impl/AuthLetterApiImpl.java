package com.auth.letter.api.impl;

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
import com.auth.letter.api.IAuthLetterApi;
import com.auth.letter.service.AuthLetterService;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.AuthLetterCreateRequest;
import com.auth.letter.vo.AuthLetterQueryRequest;
import com.auth.letter.vo.AuthLetterVO;
import com.auth.letter.vo.PageResult;

@Component
@Path("/api/authorization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthLetterApiImpl implements IAuthLetterApi {

    @Autowired
    private AuthLetterService authLetterService;

    @Override
    @POST
    @Path("/list")
    public ApiResponse<PageResult<AuthLetterVO>> list(AuthLetterQueryRequest request) {
        PageResult<AuthLetterVO> result = authLetterService.queryList(request);
        return ApiResponse.success(result);
    }

    @Override
    @GET
    @Path("/detail")
    public ApiResponse<AuthLetterVO> detail(@QueryParam("id") Long id) {
        AuthLetterVO result = authLetterService.getDetail(id);
        return ApiResponse.success(result);
    }

    @Override
    @POST
    @Path("/create")
    public ApiResponse<Long> create(AuthLetterCreateRequest request) {
        Long id = authLetterService.create(request);
        return ApiResponse.success(id);
    }

    @Override
    @PUT
    @Path("/update")
    public ApiResponse<Void> update(AuthLetterCreateRequest request) {
        authLetterService.update(request);
        return ApiResponse.success(null);
    }

    @Override
    @DELETE
    @Path("/delete")
    public ApiResponse<Void> delete(@QueryParam("id") Long id) {
        authLetterService.delete(id);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/publish")
    public ApiResponse<Void> publish(@QueryParam("id") Long id) {
        authLetterService.publish(id);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/activate")
    public ApiResponse<Void> activate(@QueryParam("id") Long id) {
        authLetterService.activate(id);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/deactivate")
    public ApiResponse<Void> deactivate(@QueryParam("id") Long id) {
        authLetterService.deactivate(id);
        return ApiResponse.success(null);
    }
}