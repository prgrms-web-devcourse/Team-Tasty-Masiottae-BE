package com.tasty.masiottae.menu;

import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.TasteFindResponse;
import com.tasty.masiottae.menu.dto.TasteSaveRequest;
import org.springframework.stereotype.Component;

@Component
public class TasteConverter {

    public TasteFindResponse toTasteFindResponse(Taste taste) {
        return new TasteFindResponse(taste.getId(), taste.getTasteName(), taste.getTasteColor());
    }

    public Taste toTaste(TasteSaveRequest request) {
        return Taste.createTaste(request.tasteName(), request.tasteColor());
    }

}