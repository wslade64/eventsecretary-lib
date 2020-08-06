package au.com.eventsecretary.simm;

import au.com.eventsecretary.common.Identifiable;
import au.com.eventsecretary.user.identity.Identity;
import au.com.eventsecretary.user.identity.Role;

import java.util.List;
import java.util.stream.Collectors;

import static au.com.eventsecretary.simm.IdentifiableUtils.cloneIdentifiable;

public interface IdentityUtils {
    static String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return phoneNumber;
        }
        phoneNumber = phoneNumber.trim();
        phoneNumber = phoneNumber.replaceAll("/(/)/ -", "");
        if (phoneNumber.startsWith("+61")) {
            phoneNumber = "0" + phoneNumber.substring("+61".length());
        }
        return phoneNumber;
    }

    static <T extends Identifiable> T sanitise(T identifiable, Identity requestIdentity) {
        if (requestIdentity != null && requestIdentity.getRole() == Role.SYSTEM) {
            return identifiable;
        }
        return cloneIdentifiable(identifiable);
    }

    static <T extends Identifiable> List<T> sanitise(final List<T> list, final Identity requestIdentity) {
        if (requestIdentity != null && requestIdentity.getRole() == Role.SYSTEM) {
            return list;
        }
        return list.stream().map(item -> sanitise(item, requestIdentity)).collect(Collectors.toList());
    }
}
