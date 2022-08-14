package com.tasty.masiottae.menu.repository;

import com.tasty.masiottae.menu.domain.Menu;
import com.tasty.masiottae.menu.dto.SearchCond;
import java.util.List;

public interface MenuRepositoryCustom {

    List<Menu> search(SearchCond searchCond);
}
