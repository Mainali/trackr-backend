package de.techdev.trackr.domain.common;

import de.techdev.trackr.domain.employee.Settings;
import de.techdev.trackr.domain.employee.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class EmployeeSettingsLocaleResolver implements LocaleContextResolver {

    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return getLocaleFromSessionOrAuthentication(request);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        if(locale != null) {
            WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        }
    }

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        final Locale locale = getLocaleFromSessionOrAuthentication(request);
        LocaleContext localeContext = () -> locale;
        LocaleContextHolder.setLocaleContext(localeContext);
        return localeContext;
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        if(localeContext != null) {
            Locale locale = localeContext.getLocale();
            WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        }
    }

    /**
     * Tries to extract the locale from the session. If not present it extracts the locale from the logged in user. Defaults to english.
     * @param request The request
     * @return The extracted locale, default {@link java.util.Locale#ENGLISH}.
     */
    protected Locale getLocaleFromSessionOrAuthentication(HttpServletRequest request) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        if(locale == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication != null) {
                Object principal = authentication.getPrincipal();
                if (User.class.isAssignableFrom(principal.getClass())) {
                    String username = ((User) principal).getUsername();
                    Settings localeSetting = settingsRepository.findByTypeAndEmployee_Email(Settings.SettingsType.LOCALE, username);
                    if (localeSetting != null) {
                        locale = Locale.forLanguageTag(localeSetting.getValue());
                    }
                }
            }
            //Either no authentication or admin user
            if(locale == null) {
                locale = Locale.ENGLISH;
            }
            WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        }
        LocaleContextHolder.setLocale(locale);
        return locale;
    }
}
