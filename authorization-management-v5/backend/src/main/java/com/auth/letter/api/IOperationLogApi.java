package com.auth.letter.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.AuthOperationLogVO;
import com.auth.letter.vo.OperationLogQueryRequest;
import com.auth.letter.vo.PageResult;

@Path("/api/log")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IOperationLogApi {

    @POST
    @Path("/list")
    ApiResponse<PageResult<AuthOperationLogVO>> list(OperationLogQueryRequest request);
}