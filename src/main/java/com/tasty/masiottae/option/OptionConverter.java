package com.tasty.masiottae.option;

import com.tasty.masiottae.option.domain.Option;
import com.tasty.masiottae.option.dto.OptionSaveRequest;
import com.tasty.masiottae.option.dto.OptionFindResponse;
import org.springframework.stereotype.Component;

@Component
public class OptionConverter {

    public Option toOption(OptionSaveRequest request) {
        return Option.createOption(request.name(), request.description());
    }
    
    public OptionFindResponse toOptionFindResponse(Option option) {
        return new OptionFindResponse(option.getOptionName(), option.getDescription());
    }
}

