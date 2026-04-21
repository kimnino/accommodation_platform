package com.accommodation.platform.common.i18n;

public class LocaleContextHolder {

    public static String getCurrentLocale() {
        return org.springframework.context.i18n.LocaleContextHolder.getLocale().getLanguage();
    }
}
