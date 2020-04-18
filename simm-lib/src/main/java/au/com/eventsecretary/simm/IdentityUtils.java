package au.com.eventsecretary.simm;

import au.com.eventsecretary.common.Identifiable;
import au.com.eventsecretary.user.identity.Identity;
import au.com.eventsecretary.user.identity.Role;

import java.util.List;
import java.util.stream.Collectors;

import static au.com.eventsecretary.simm.IdentifiableUtils.cloneIdentifiable;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public interface IdentityUtils {
    public static <T extends Identifiable> T sanitise(T identifiable, Identity requestIdentity) {
        if (requestIdentity != null && requestIdentity.getRole() == Role.SYSTEM) {
            return identifiable;
        }
        return cloneIdentifiable(identifiable);
    }

    public static <T extends Identifiable> List<T> sanitise(final List<T> list, final Identity requestIdentity) {
        if (requestIdentity != null && requestIdentity.getRole() == Role.SYSTEM) {
            return list;
        }
        return list.stream().map(item -> sanitise(item, requestIdentity)).collect(Collectors.toList());
    }
}
