package com.tasty.masiottae.common.util;

import java.util.List;

public record PageResponse<T>(List<T> list, boolean isLast) {

}
