package com.tasty.masiottae.account.controller;

import static com.tasty.masiottae.account.domain.CHECK_PROPERTY.INVALID;
import static com.tasty.masiottae.account.domain.CHECK_PROPERTY.findProperty;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.tasty.masiottae.account.domain.CHECK_PROPERTY;
import com.tasty.masiottae.account.dto.AccountCreateRequest;
import com.tasty.masiottae.account.dto.AccountDuplicatedResponse;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.dto.AccountImageUpdateResponse;
import com.tasty.masiottae.account.dto.AccountNickNameUpdateRequest;
import com.tasty.masiottae.account.dto.AccountPasswordUpdateRequest;
import com.tasty.masiottae.account.dto.AccountSnsUpdateRequest;
import com.tasty.masiottae.account.service.AccountService;
import com.tasty.masiottae.security.jwt.JwtToken;
import com.tasty.masiottae.security.jwt.JwtTokenResponse;
import java.net.URI;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountService accountService;

    @PostMapping(value = "/signup", consumes = {MULTIPART_FORM_DATA_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> saveAccount(
            @RequestPart(required = false) final MultipartFile image,
            @RequestPart final AccountCreateRequest request) {
        Long id = accountService.saveAccount(request, image);
        return ResponseEntity.created(URI.create("/users/" + id)).build();
    }

    @GetMapping(value = "/accounts", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AccountFindResponse>> findAllAccounts(@PageableDefault final Pageable pageable) {
        Page<AccountFindResponse> accountList = accountService.findAllAccounts(pageable);
        return ResponseEntity.ok(accountList);
    }

    @GetMapping(value = "/accounts/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountFindResponse> findOneAccount(@PathVariable final Long id) {
        AccountFindResponse accountFindResponse = accountService.findOneAccount(id);
        return ResponseEntity.ok(accountFindResponse);
    }
    
    @PatchMapping(value = "/accounts/{id}/password", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePassword(@PathVariable final Long id,
            @RequestBody final AccountPasswordUpdateRequest request) {
        accountService.updatePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/accounts/{id}/nick-name", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateNickName(@PathVariable final Long id,
            @RequestBody final AccountNickNameUpdateRequest request) {
        accountService.updateNickName(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/accounts/{id}/sns", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateSnsAccount(@PathVariable final Long id,
            @RequestBody final AccountSnsUpdateRequest request) {
        accountService.updateSnsAccount(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/accounts/{id}/image", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AccountImageUpdateResponse> updateImage(@PathVariable final Long id,
            @RequestPart final MultipartFile image) {
        AccountImageUpdateResponse accountImageUpdateResponse = accountService.updateImage(id, image);
        return ResponseEntity.ok(accountImageUpdateResponse);
    }

    @DeleteMapping(value = "/accounts/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable final Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/accounts/check", params = {"property", "value"}, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDuplicatedResponse> checkDuplicateProperty(
            @RequestParam final String property,
            @RequestParam final String value) {
        CHECK_PROPERTY prop = findProperty(property);
        AccountDuplicatedResponse accountDuplicatedResponse;

        switch (prop) {
            case EMAIL ->  accountDuplicatedResponse = accountService.checkDuplicateByEmail(value);
            case NICK_NAME -> accountDuplicatedResponse = accountService.checkDuplicateByNickName(value);
            default -> accountDuplicatedResponse =
                    new AccountDuplicatedResponse(false, Optional.of(INVALID.getErrorMessage()));
        }

        return ResponseEntity.ok(accountDuplicatedResponse);
    }

    @GetMapping(value = "/login_success", produces = APPLICATION_JSON_VALUE)
    private ResponseEntity<JwtTokenResponse> respondWithTokenAndAccount(final HttpServletRequest request) {
        AccountFindResponse accountFindResponse = accountService.findOneAccount((Long)
                request.getSession().getAttribute("id"));
        JwtToken jwtToken = (JwtToken) request.getSession().getAttribute("token");
        JwtTokenResponse response = new JwtTokenResponse(jwtToken, accountFindResponse);
        return ResponseEntity.ok(response);
    }

}
