package com.auth.letter.controller;

import com.auth.letter.dto.*;
import com.auth.letter.service.AuthLetterService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Authorization Letter Controller
 * Uses JAX-RS annotations (@Path) for URL mapping
 */
@Component
@Path("/api/auth-letters")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AuthLetterController {

    private final AuthLetterService authLetterService;

    /**
     * Query authorization letters with pagination
     */
    @GET
    public Response queryList(
            @QueryParam("name") String name,
            @QueryParam("authTargetLevel") List<String> authTargetLevel,
            @QueryParam("applicableRegion") List<String> applicableRegion,
            @QueryParam("authPublishLevel") List<String> authPublishLevel,
            @QueryParam("authPublishOrg") List<String> authPublishOrg,
            @QueryParam("publishYear") Integer publishYear,
            @QueryParam("status") String status,
            @QueryParam("pageNum") @DefaultValue("1") Integer pageNum,
            @QueryParam("pageSize") @DefaultValue("10") Integer pageSize) {

        AuthLetterQueryDTO queryDTO = new AuthLetterQueryDTO();
        queryDTO.setName(name);
        queryDTO.setAuthTargetLevel(authTargetLevel);
        queryDTO.setApplicableRegion(applicableRegion);
        queryDTO.setAuthPublishLevel(authPublishLevel);
        queryDTO.setAuthPublishOrg(authPublishOrg);
        queryDTO.setPublishYear(publishYear);
        queryDTO.setStatus(status);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);

        PageResult<AuthLetterListVO> result = authLetterService.queryList(queryDTO);
        return Response.ok(ApiResponse.success(result)).build();
    }

    /**
     * Batch publish authorization letters
     */
    @PUT
    @Path("/batch/publish")
    public Response batchPublish(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }

        int count = authLetterService.batchPublish(dto.getIds());
        String message = count > 0
                ? String.format("成功发布%d条数据", count)
                : "没有符合条件的数据可发布";
        return Response.ok(ApiResponse.success(message, count)).build();
    }

    /**
     * Batch expire authorization letters
     */
    @PUT
    @Path("/batch/expire")
    public Response batchExpire(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }

        int count = authLetterService.batchExpire(dto.getIds());
        String message = count > 0
                ? String.format("成功失效%d条数据", count)
                : "没有符合条件的数据可失效";
        return Response.ok(ApiResponse.success(message, count)).build();
    }

    /**
     * Batch delete authorization letters
     */
    @DELETE
    @Path("/batch")
    public Response batchDelete(BatchOperationDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return Response.ok(ApiResponse.error(400, "请先选择数据")).build();
        }

        int count = authLetterService.batchDelete(dto.getIds());
        String message = count > 0
                ? String.format("成功删除%d条数据", count)
                : "没有符合条件的数据可删除";
        return Response.ok(ApiResponse.success(message, count)).build();
    }
}