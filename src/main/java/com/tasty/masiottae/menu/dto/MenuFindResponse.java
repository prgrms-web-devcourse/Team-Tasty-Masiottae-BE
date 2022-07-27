package com.tasty.masiottae.menu.dto;

import com.tasty.masiottae.option.dto.OptionFindResponse;
import java.util.List;

public record MenuFindResponse(Long id, String franchise, String image, String title,
                               String originalTitle, String author,
                               String content, Integer likes,
                               Integer expectedPrice, List<OptionFindResponse> options,
                               List<TasteFindResponse> tastes) {

}
