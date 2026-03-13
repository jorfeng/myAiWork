package com.auth.letter.api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.AuthAttachmentVO;
import com.auth.letter.vo.PageResult;

@Path("/api/attachment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IAttachmentApi {

    @GET
    @Path("/list")
    ApiResponse<PageResult<AuthAttachmentVO>> list(
        @QueryParam("letterId") Long letterId,
        @QueryParam("pageNum") Integer pageNum,
        @QueryParam("pageSize") Integer pageSize
    );

    @DELETE
    @Path("/delete")
    ApiResponse<Void> delete(@QueryParam("id") Long id);

    @POST
    @Path("/deleteBatch")
    ApiResponse<Void> deleteBatch(List<Long> ids);

    @POST
    @Path("/upload")
    ApiResponse<Long> upload(
        @QueryParam("letterId") Long letterId,
        @QueryParam("docName") String docName,
        @QueryParam("docType") String docType,
        @QueryParam("docId") String docId
    );
}