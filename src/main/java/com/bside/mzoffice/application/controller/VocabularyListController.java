package com.bside.mzoffice.application.controller;

import com.bside.mzoffice.common.response.ServerResponse;
import com.bside.mzoffice.vocabularyList.dto.response.QuizResponse;
import com.bside.mzoffice.vocabularyList.dto.response.WordResponse;
import com.bside.mzoffice.vocabularyList.service.VocabularyListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vocabulary")
@RequiredArgsConstructor
public class VocabularyListController {
    private final VocabularyListService vocabularyListService;


    /**
     * 오늘 보지 않았던 랜덤 단어 응답
     *
     * @return ServerResponse - responseCode
     */
    @Operation(
            summary = "랜덤 단어 응답",
            description = "오늘 유저가 보지 못한 단어 부터 우선 응답합니다."
    )
    @ApiResponse(responseCode = "200", description = "단어 정보 응답 ",
            content = @Content(schema = @Schema(implementation = WordResponse.class)))
    @GetMapping("/word")
    public ServerResponse<WordResponse> getRandomWord(Authentication authentication) {
        return ServerResponse.successResponse(vocabularyListService.getRandomWord(authentication));
    }

    /**
     * 랜덤 퀴즈 응답
     *
     * @return ServerResponse - responseCode
     */
    @Operation(
            summary = "랜덤 단어 응답",
            description = "오늘 유저가 보지 못한 단어 부터 우선 응답합니다."
    )
    @ApiResponse(responseCode = "200", description = "단어 정보 응답 + 선택지로 나올 잘못된 단어 4개 응답",
            content = @Content(schema = @Schema(implementation = QuizResponse.class)))
    @GetMapping("/quiz")
    public ServerResponse<QuizResponse> getRandomQuiz(Authentication authentication) {
        return ServerResponse.successResponse(vocabularyListService.getRandomQuiz(authentication));
    }
}
