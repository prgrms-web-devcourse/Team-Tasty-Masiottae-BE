package com.tasty.masiottae.account.service;

import static com.tasty.masiottae.account.domain.CheckProperty.EMAIL;
import static com.tasty.masiottae.account.domain.CheckProperty.NICK_NAME;

import com.tasty.masiottae.account.converter.AccountConverter;
import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.dto.AccountCreateRequest;
import com.tasty.masiottae.account.dto.AccountDuplicatedResponse;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.dto.AccountImageUpdateResponse;
import com.tasty.masiottae.account.dto.AccountLogoutRequest;
import com.tasty.masiottae.account.dto.AccountNickNameUpdateRequest;
import com.tasty.masiottae.account.dto.AccountNickNameUpdateResponse;
import com.tasty.masiottae.account.dto.AccountPasswordUpdateRequest;
import com.tasty.masiottae.account.dto.AccountReIssueRequest;
import com.tasty.masiottae.account.dto.AccountSnsUpdateRequest;
import com.tasty.masiottae.account.dto.AccountSnsUpdateResponse;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.account.repository.TimerUtils;
import com.tasty.masiottae.account.repository.TokenCache;
import com.tasty.masiottae.common.aws.AwsS3ImageUploader;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.jwt.JwtAccessToken;
import com.tasty.masiottae.security.jwt.JwtRefreshToken;
import com.tasty.masiottae.security.jwt.JwtToken;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountEntityService accountEntityService;
    private final AwsS3ImageUploader s3ImageUploader;

    private final TokenCache tokenCache;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountConverter accountConverter;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JwtToken saveAccount(AccountCreateRequest accountCreateRequest,
            MultipartFile image) {
        Account object = accountConverter.fromAccountCreateRequest(accountCreateRequest, image);
        object.encryptPassword(object.getPassword(), passwordEncoder);

        if (Objects.nonNull(image)) {
            object.updateImage(s3ImageUploader.uploadAccountImage(image));
        }

        Account entity = accountRepository.save(object);
        AccountDetail detail = new AccountDetail(entity);
        JwtAccessToken accessToken = jwtTokenProvider.generateAccessToken(detail);
        JwtRefreshToken refreshToken = jwtTokenProvider.generateRefreshToken(detail);
        tokenCache.registerRefreshToken(detail.getUsername(), refreshToken);
        return accountConverter.toJwtToken(accessToken, refreshToken);
    }

    public Page<AccountFindResponse> findAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(accountConverter::toAccountFindResponse);
    }

    public AccountFindResponse findOneAccount(Long id) {
        return accountConverter.toAccountFindResponse(
                accountEntityService.findById(id));
    }

    @Transactional
    public void updatePassword(Long id, AccountPasswordUpdateRequest accountPasswordUpdateRequest) {
        Account entity = accountEntityService.findById(id);
        entity.encryptPassword(accountPasswordUpdateRequest.password(), passwordEncoder);
    }

    @Transactional
    public AccountNickNameUpdateResponse updateNickName(Long id,
            AccountNickNameUpdateRequest accountUpdateRequest) {
        Account entity = accountEntityService.findById(id);
        entity.updateNickName(accountUpdateRequest.nickName());
        return new AccountNickNameUpdateResponse(accountUpdateRequest.nickName());
    }

    @Transactional
    public AccountSnsUpdateResponse updateSnsAccount(Long id,
            AccountSnsUpdateRequest accountSnsUpdateRequest) {
        Account entity = accountEntityService.findById(id);
        entity.updateSnsAccount(accountSnsUpdateRequest.snsAccount());
        return new AccountSnsUpdateResponse(accountSnsUpdateRequest.snsAccount());
    }

    @Transactional
    public AccountImageUpdateResponse updateImage(Long id, MultipartFile image) {
        Account entity = accountEntityService.findById(id);
        String imageUrl = s3ImageUploader.uploadAccountImage(image);
        entity.updateImage(imageUrl);
        return new AccountImageUpdateResponse(imageUrl);
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = accountEntityService.findById(id);
        accountRepository.delete(account);
    }

    public AccountDuplicatedResponse checkDuplicateByNickName(String nickName) {
        boolean isNickname = accountRepository.existsByNickName(nickName);
        Optional<String> errorMessage = Optional.empty();

        if (isNickname) {
            errorMessage = Optional.of(NICK_NAME.getErrorMessage());
        }

        return new AccountDuplicatedResponse(isNickname, errorMessage);
    }

    public AccountDuplicatedResponse checkDuplicateByEmail(String email) {
        boolean isEmail = accountRepository.existsByEmail(email);
        Optional<String> errorMessage = Optional.empty();

        if (isEmail) {
            errorMessage = Optional.of(EMAIL.getErrorMessage());
        }

        return new AccountDuplicatedResponse(isEmail, errorMessage);
    }

    public JwtAccessToken reIssueAccessToken(AccountReIssueRequest request) {
        if (tokenCache.holdRefreshToken(request.email(), request.refreshToken())
            && !tokenCache.isAccessTokenInBlackList(request.email(), request.accessToken())) {
            tokenCache.blockAccessToken(
                request.email(),
                request.accessToken(),
                TimerUtils.getExpirationDate(jwtTokenProvider.getExpirationTime()));

            Account entity = accountEntityService.findEntityGraphMenuByEmail(request.email());
            AccountDetail detail = new AccountDetail(entity);
            return jwtTokenProvider.generateAccessToken(detail);
        }

        throw new IllegalArgumentException("토큰 정보가 잘못 입력되었습니다.");
    }

    public void logout(AccountLogoutRequest request) {
        if (!tokenCache.holdRefreshToken(request.email(), request.refreshToken())) {
            throw new IllegalArgumentException("리프레쉬 토큰 정보가 잘못 입력되었습니다.");
        }

        tokenCache.removeRefreshToken(request.email());
        tokenCache.blockAccessToken(
            request.email(),
            request.accessToken(),
            TimerUtils.getExpirationDate(jwtTokenProvider.getExpirationTime()));
    }

}
