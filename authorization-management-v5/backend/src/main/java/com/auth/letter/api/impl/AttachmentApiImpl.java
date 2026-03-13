package com.auth.letter.api.impl;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.auth.letter.api.IAttachmentApi;
import com.auth.letter.service.AttachmentService;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.AuthAttachmentVO;
import com.auth.letter.vo.PageResult;

@Component
@Path("/api/attachment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttachmentApiImpl implements IAttachmentApi {

    @Autowired
    private AttachmentService attachmentService;

    @Override
    @GET
    @Path("/list")
    public ApiResponse<PageResult<AuthAttachmentVO>> list(
            @QueryParam("letterId") Long letterId,
            @QueryParam("pageNum") Integer pageNum,
            @QueryParam("pageSize") Integer pageSize) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        PageResult<AuthAttachmentVO> result = attachmentService.queryByLetterId(letterId, pageNum, pageSize);
        return ApiResponse.success(result);
    }

    @Override
    @DELETE
    @Path("/delete")
    public ApiResponse<Void> delete(@QueryParam("id") Long id) {
        attachmentService.delete(id);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/deleteBatch")
    public ApiResponse<Void> deleteBatch(List<Long> ids) {
        attachmentService.deleteBatch(ids);
        return ApiResponse.success(null);
    }

    @Override
    @POST
    @Path("/upload")
    public ApiResponse<Long> upload(
            @QueryParam("letterId") Long letterId,
            @QueryParam("docName") String docName,
            @QueryParam("docType") String docType,
            @QueryParam("docId") String docId) {
        Long id = attachmentService.upload(letterId, docName, docType, docId);
        return ApiResponse.success(id);
    }
}