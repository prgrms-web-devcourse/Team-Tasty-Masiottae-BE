package com.tasty.masiottae.account.service;

import static com.tasty.masiottae.account.domain.CheckProperty.EMAIL;
import static com.tasty.masiottae.account.domain.CheckProperty.NICK_NAME;
import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_ACCOUNT;

import com.tasty.masiottae.account.converter.AccountConverter;
import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.dto.AccountCreateRequest;
import com.tasty.masiottae.account.dto.AccountDuplicatedResponse;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.dto.AccountImageUpdateResponse;
import com.tasty.masiottae.account.dto.AccountNickNameUpdateRequest;
import com.tasty.masiottae.account.dto.AccountNickNameUpdateResponse;
import com.tasty.masiottae.account.dto.AccountPasswordUpdateRequest;
import com.tasty.masiottae.account.dto.AccountSnsUpdateRequest;
import com.tasty.masiottae.account.dto.AccountSnsUpdateResponse;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.common.exception.custom.NotFoundException;
import com.tasty.masiottae.common.util.AwsS3Service;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.jwt.JwtToken;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AcccountEntityService acccountEntityService;
    private final AwsS3Service awsS3Service;

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountConverter accountConverter;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JwtToken saveAccount(AccountCreateRequest accountCreateRequest,
            MultipartFile image) {
        String imageUrl = null;
        if (Objects.nonNull(image)) {
            imageUrl = awsS3Service.uploadAccountImage(image);
        }

        Account object = Account.createAccount(accountCreateRequest.email(),
                accountCreateRequest.password(),
                accountCreateRequest.nickName(),
                imageUrl,
                accountCreateRequest.snsAccount());
        object.encryptPassword(object.getPassword(), passwordEncoder);

        AccountDetail detail = new AccountDetail(accountRepository.save(object));
        return jwtTokenProvider.generatedAccountToken(detail);
    }

    public Page<AccountFindResponse> findAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(accountConverter::toAccountFindResponse);
    }

    public AccountFindResponse findOneAccount(Long id) {
        return accountConverter.toAccountFindResponse(
                acccountEntityService.findById(id));
    }

    @Transactional
    public void updatePassword(Long id, AccountPasswordUpdateRequest accountPasswordUpdateRequest) {
        Account entity = acccountEntityService.findById(id);
        entity.encryptPassword(accountPasswordUpdateRequest.password(), passwordEncoder);
    }

    @Transactional
    public AccountNickNameUpdateResponse updateNickName(Long id,
            AccountNickNameUpdateRequest accountUpdateRequest) {
        Account entity = acccountEntityService.findById(id);
        entity.updateNickName(accountUpdateRequest.nickName());
        return new AccountNickNameUpdateResponse(accountUpdateRequest.nickName());
    }

    @Transactional
    public AccountSnsUpdateResponse updateSnsAccount(Long id,
            AccountSnsUpdateRequest accountSnsUpdateRequest) {
        Account entity = acccountEntityService.findById(id);
        entity.updateSnsAccount(accountSnsUpdateRequest.snsAccount());
        return new AccountSnsUpdateResponse(accountSnsUpdateRequest.snsAccount());
    }

    @Transactional
    public AccountImageUpdateResponse updateImage(Long id, MultipartFile image) {
        Account entity = acccountEntityService.findById(id);
        String imageUrl = awsS3Service.uploadAccountImage(image);
        entity.updateImage(imageUrl);
        return new AccountImageUpdateResponse(imageUrl);
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = acccountEntityService.findById(id);
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

    public void validateExistsAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new NotFoundException(NOT_FOUND_ACCOUNT.getMessage());
        }
    }

}
