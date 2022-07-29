package com.tasty.masiottae.option;

import com.tasty.masiottae.option.domain.Option;
import com.tasty.masiottae.option.dto.OptionSaveRequest;
import org.springframework.stereotype.Component;

@Component
public class OptionConverter {

    public Option toOption(OptionSaveRequest request) {
        return Option.createOption(request.name(), request.description());
    }
}