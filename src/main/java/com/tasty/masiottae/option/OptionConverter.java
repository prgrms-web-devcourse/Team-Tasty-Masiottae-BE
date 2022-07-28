package com.tasty.masiottae.option;

import com.tasty.masiottae.option.domain.Option;
import com.tasty.masiottae.option.dto.OptionFindResponse;
import org.springframework.stereotype.Component;

@Component
public class OptionConverter {

    public OptionFindResponse toOptionFindResponse(Option option) {
        return new OptionFindResponse(option.getOptionName(), option.getDescription());
    }
}
