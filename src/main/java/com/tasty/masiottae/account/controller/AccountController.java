package com.tasty.masiottae.account.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.tasty.masiottae.account.dto.AccountCreateRequest;
import com.tasty.masiottae.account.dto.AccountDuplicatedResponse;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.dto.AccountLoginRequest;
import com.tasty.masiottae.account.dto.AccountPasswordUpdateRequest;
import com.tasty.masiottae.account.dto.AccountUpdateRequest;
import com.tasty.masiottae.account.service.AccountService;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.jwt.JwtToken;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
import com.tasty.masiottae.security.jwt.JwtTokenResponse;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/accounts")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountService accountService;

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountFindResponse> findAccountById(@PathVariable final Long id) {
        AccountFindResponse accountFindResponse = new AccountFindResponse(1L, "nickname", "imgUrl",
                "example@naver.com", LocalDateTime.now(), 0);
        return ResponseEntity.ok(accountFindResponse);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AccountFindResponse>> findAccounts(@RequestParam final int page) {
        List<AccountFindResponse> list = new ArrayList<>();
        list.add(new AccountFindResponse(1L, "nickname", "imgUrl",
                "example@naver.com", LocalDateTime.now(), 0));

        PageRequest pageable = PageRequest.of(page, 10);

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());
        final Page<AccountFindResponse> pages = new PageImpl<>(list.subList(start, end), pageable,
                list.size());

        return ResponseEntity.ok(pages);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtTokenResponse> createAccount(
            @RequestBody final AccountCreateRequest request) {
        JwtTokenResponse jwtTokenResponse = accountService.save(request);
        return ResponseEntity.ok(jwtTokenResponse);
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateAccount(
            @PathVariable final Long id, @RequestBody final AccountUpdateRequest request) {
        AccountFindResponse updated = new AccountFindResponse(1L, request.nickname(),
                request.imgUrl(),
                "example@naver.com", LocalDateTime.now(), 0);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/password/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateAccountPassword(
            @PathVariable final Long id, @RequestBody final AccountPasswordUpdateRequest request) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> loginAccount(
            @RequestBody final AccountLoginRequest request) {
        AccountFindResponse loggedInAccount = new AccountFindResponse(1L, "nickname", "imgUrl",
                "example@naver.com", LocalDateTime.now(), 0);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/check", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDuplicatedResponse> isDuplicatedParam(
            @RequestParam final String property,
            @RequestParam final String value) {
        AccountDuplicatedResponse accountDuplicatedResponse = null;
        String email = "email";
        String nickname = "nickname";

        if (property.equals(email)) {
            accountDuplicatedResponse = accountService.duplicateCheckEmail(value);
        } else if (property.equals(nickname)) {
            accountDuplicatedResponse = accountService.duplicateCheckNickname(value);
        } else {
            throw new IllegalArgumentException("중복 체크가 가능한 파라미터를 입력해 주세요.");
        }

        return ResponseEntity.ok(accountDuplicatedResponse);
    }

}
