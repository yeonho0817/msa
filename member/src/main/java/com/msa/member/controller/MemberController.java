package com.msa.member.controller;

import com.msa.member.error.ErrorVO;
import com.msa.member.response.Response;
import com.msa.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @Operation(operationId = "test", summary = "test", description = "test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "test 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "4XX, 5XX", description = "test 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))})
    @GetMapping("/sign-up")
    public Callable<Response<Void>> test(
    ) {
        log.info("1");
        return () -> Response.OK;
    }

    @Operation(operationId = "getToken", summary = "getToken", description = "토큰을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "test 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "4XX, 5XX", description = "test 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))})
    @GetMapping("/getToken")
    public Callable<Response<String>> getToken(
    ) {
        return () -> Response.of(memberService.getToken());
    }

}
