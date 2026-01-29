package ivar.hogblom.crmbackend.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


@Component
public final class OrganizationNumberValidator {

    // Samma regex som i frontend: /^\d{6,10}[-]?\d{4}$/
    private static final Pattern ORG_NUMBER_PATTERN =
            Pattern.compile("^\\d{6,10}-?\\d{4}$");

    private OrganizationNumberValidator() {
        // utility class
    }

    /**
     * Validates a Swedish organization number format.
     *
     * Valid examples:
     *  - 556677-8899
     *  - 5566778899
     *  - 12345678901234 (6–10 + 4 digits)
     *
     * @param orgNumber organization number as string
     * @return true if format is valid, false otherwise
     */
    public boolean isValid(String orgNumber) {
        if (orgNumber == null || orgNumber.isBlank()) {
            return false;
        }
        return ORG_NUMBER_PATTERN.matcher(orgNumber).matches();
    }
}