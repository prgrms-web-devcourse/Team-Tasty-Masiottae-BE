package com.tasty.masiottae.common.util;

import com.tasty.masiottae.common.exception.ErrorMessage;
import java.util.List;

public interface PageUtil {

    static <T> PageResponse<T> page(List<T> list, int size, int page) {
        int totalPage = getTotalPage(size, list);

        validatePageNum(page, totalPage);

        int offset = size * (page - 1);
        int endIndex = getEndIndex(page, size, list, totalPage, offset);

        return new PageResponse<>(list.subList(offset, endIndex), totalPage == page);
    }

    private static void validatePageNum(int page, int totalPage) {
        if (page > totalPage) {
            throw new IllegalArgumentException(
                    ErrorMessage.PAGE_GREATER_THAN_TOTAL_PAGE.getMessage());
        }
    }

    private static <T> int getEndIndex(int page, int size, List<T> list, int totalPage,
            int offset) {
        return page == totalPage ? list.size() : offset + size;
    }

    private static <T> int getTotalPage(int size, List<T> list) {
        return list.size() / size + (list.size() % size == 0 ? 0 : 1);
    }
}
