package com.msa.member.controller;

import com.msa.member.error.ErrorVO;
import com.msa.member.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    @Operation(operationId = "test", summary = "test", description = "test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "test 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "4XX, 5XX", description = "test 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))})
    @PostMapping("/sign-up")
    public Callable<Response<Void>> test(
    ) {
        return () -> Response.OK;
    }

}
