package com.tasty.masiottae.common.util;

import java.util.List;

public interface PageUtil {

    static <T> List<T> page(PageInfo pageInfo, List<T> list) {
        if (PageUtil.isOveOffsetThanSize(pageInfo, list.size())) {
            throw new IllegalArgumentException("offset이 size보다 크거나 같을 수 없습니다.");
        }

        int lastIndex = PageUtil.getLastIndex(pageInfo, list.size());

        return list.subList(pageInfo.offset(), lastIndex);
    }

    private static boolean isOveOffsetThanSize(PageInfo pageInfo, int size) {
        return pageInfo.offset() >= size;
    }

    private static <T> int getLastIndex(PageInfo pageInfo, int size) {
        return Math.min(pageInfo.offset() + pageInfo.limit(), size);
    }
}
