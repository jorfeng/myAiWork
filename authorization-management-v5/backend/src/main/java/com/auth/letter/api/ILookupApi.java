package com.auth.letter.api;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.LookupValueVO;

@Path("/api/lookup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ILookupApi {

    @GET
    @Path("/{type}")
    ApiResponse<List<LookupValueVO>> getLookupValues(@PathParam("type") String type);
}