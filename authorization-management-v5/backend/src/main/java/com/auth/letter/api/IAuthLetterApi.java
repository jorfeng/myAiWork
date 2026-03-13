package com.auth.letter.api;

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
import com.auth.letter.vo.AuthLetterCreateRequest;
import com.auth.letter.vo.AuthLetterQueryRequest;
import com.auth.letter.vo.AuthLetterVO;
import com.auth.letter.vo.PageResult;

@Path("/api/authorization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IAuthLetterApi {

    @POST
    @Path("/list")
    ApiResponse<PageResult<AuthLetterVO>> list(AuthLetterQueryRequest request);

    @GET
    @Path("/detail")
    ApiResponse<AuthLetterVO> detail(@QueryParam("id") Long id);

    @POST
    @Path("/create")
    ApiResponse<Long> create(AuthLetterCreateRequest request);

    @PUT
    @Path("/update")
    ApiResponse<Void> update(AuthLetterCreateRequest request);

    @DELETE
    @Path("/delete")
    ApiResponse<Void> delete(@QueryParam("id") Long id);

    @POST
    @Path("/publish")
    ApiResponse<Void> publish(@QueryParam("id") Long id);

    @POST
    @Path("/activate")
    ApiResponse<Void> activate(@QueryParam("id") Long id);

    @POST
    @Path("/deactivate")
    ApiResponse<Void> deactivate(@QueryParam("id") Long id);
}