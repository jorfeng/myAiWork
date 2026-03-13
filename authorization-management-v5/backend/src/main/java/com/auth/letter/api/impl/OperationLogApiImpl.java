package com.auth.letter.api.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.auth.letter.api.IOperationLogApi;
import com.auth.letter.service.OperationLogService;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.AuthOperationLogVO;
import com.auth.letter.vo.OperationLogQueryRequest;
import com.auth.letter.vo.PageResult;

@Component
@Path("/api/log")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperationLogApiImpl implements IOperationLogApi {

    @Autowired
    private OperationLogService operationLogService;

    @Override
    @POST
    @Path("/list")
    public ApiResponse<PageResult<AuthOperationLogVO>> list(OperationLogQueryRequest request) {
        PageResult<AuthOperationLogVO> result = operationLogService.queryByLetterId(
            request.getLetterId(),
            request.getPageNum(),
            request.getPageSize()
        );
        return ApiResponse.success(result);
    }
}