package com.tasty.masiottae.menu.dto;

import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.option.dto.OptionFindResponse;
import java.time.LocalDateTime;
import java.util.List;

public record MenuFindResponse(Long id, FranchiseFindResponse franchise, String image, String title,
                               String originalTitle, AccountFindResponse author,
                               String content, Integer likes, Integer comments,
                               Integer expectedPrice, List<OptionFindResponse> optionList,
                               List<TasteFindResponse> tasteList, LocalDateTime createdAt,
                               LocalDateTime updatedAt) {

}
