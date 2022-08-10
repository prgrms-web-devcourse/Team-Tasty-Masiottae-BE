package com.tasty.masiottae.account.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimerUtils {

    public static Date now() {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.atZone(ZoneId.of("Asia/Seoul")).toInstant();
        return Date.from(instant);
    }

    public static Date getExpirationDate(int milliseconds) {
        return new Date(now().getTime() + milliseconds);
    }

    public static boolean isExpired(Date expirationDate) {
        return now().after(expirationDate);
    }

}
