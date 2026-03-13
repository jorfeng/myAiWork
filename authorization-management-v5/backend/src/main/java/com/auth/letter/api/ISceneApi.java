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
import com.auth.letter.vo.AuthSceneVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.SceneCreateRequest;
import com.auth.letter.vo.SceneMatchRequest;
import com.auth.letter.vo.SceneMatchResult;

@Path("/api/scene")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ISceneApi {

    @GET
    @Path("/list")
    ApiResponse<PageResult<AuthSceneVO>> list(
        @QueryParam("letterId") Long letterId,
        @QueryParam("pageNum") Integer pageNum,
        @QueryParam("pageSize") Integer pageSize
    );

    @POST
    @Path("/create")
    ApiResponse<Long> create(SceneCreateRequest request);

    @PUT
    @Path("/update")
    ApiResponse<Void> update(SceneCreateRequest request);

    @DELETE
    @Path("/delete")
    ApiResponse<Void> delete(@QueryParam("id") Long id);

    @POST
    @Path("/deleteBatch")
    ApiResponse<Void> deleteBatch(List<Long> ids);

    @POST
    @Path("/match")
    ApiResponse<List<SceneMatchResult>> match(SceneMatchRequest request);
}