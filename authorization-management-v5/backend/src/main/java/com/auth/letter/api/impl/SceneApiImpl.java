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
import com.auth.letter.api.ISceneApi;
import com.auth.letter.service.SceneService;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.AuthSceneVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.SceneCreateRequest;
import com.auth.letter.vo.SceneMatchRequest;
import com.auth.letter.vo.SceneMatchResult;

@Component
@Path("/api/scene")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SceneApiImpl implements ISceneApi {

    @Autowired
    private SceneService sceneService;

    @Override
    @GET
    @Path("/list")
    public ApiResponse<PageResult<AuthSceneVO>> list(
            @QueryParam("letterId") Long letterId,
            @QueryParam("pageNum") Integer pageNum,
            @QueryParam("pageSize") Integer pageSize) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        PageResult<AuthSceneVO> result = sceneService.queryByLetterId(letterId, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Override
    @POST
    @Path("/create")
    public ApiResponse<Long> create(SceneCreateRequest request) {
        Long id = sceneService.create(request);
        return ApiResponse.success(id);
    }

    @Override
    @PUT
    @Path("/update")
    public ApiResponse<Void> update(SceneCreateRequest request) {
        sceneService.update(request);
        return ApiResponse.success(null);
    }

    @Override
    @DELETE
    @Path("/delete")
    public ApiResponse<Void> delete(@QueryParam("id") Long id) {
        sceneService.delete(id);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/deleteBatch")
    public ApiResponse<Void> deleteBatch(List<Long> ids) {
        sceneService.deleteBatch(ids);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/match")
    public ApiResponse<List<SceneMatchResult>> match(SceneMatchRequest request) {
        List<SceneMatchResult> results = sceneService.matchScenes(request.getLetterId(), request.getParams());
        return ApiResponse.success(results);
    }
}