package au.com.eventsecretary.simm;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.common.Entity;
import au.com.eventsecretary.common.Identifiable;
import au.com.eventsecretary.common.IdentifiableImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * @author sladew
 */
public interface IdentifiableUtils {
    static boolean isId(String guid) {
        if (guid == null || guid.length() == 0) {
            return false;
        }
        if (guid.length() == 24 && guid.startsWith("5")) {
            return true;
        }
        return guid.length() == 36 && guid.indexOf("-") == 8;
    }

    static String id() {
        return UUID.randomUUID().toString();
    }

    static String random() {
        return UUID.randomUUID().toString();
    }

    static Identifiable reference(Identifiable identifiable) {
        Identifiable simple = new IdentifiableImpl();
        simple.setId(identifiable.getId());
        simple.setName(identifiable.getName());
        return simple;
    }

    static void checkDuplicate(List<? extends Identifiable> list, Identifiable facilityType) {
        for (Identifiable type : list) {
            if (type.getName().equals(facilityType.getName())) {
                throw new ResourceExistsException("The name specified is currently in use!");
            }
        }
    }

    static <T extends Identifiable> T copy(T from, T to) {
        if (from != null) {
            to.setCode(from.getCode());
            to.setName(from.getName());
        }
        return to;
    }

    static <T extends Identifiable> T cloneIdentifiable(T from) {
        T to = (T) new IdentifiableImpl();
        if (from != null) {
            to.setId(from.getId());
        }
        return copy(from, to);
    }

    static <T extends Entity> boolean hasById(List<T> list, String id) {
        return findById(list, id) != null;
    }

    static <T extends Entity> T findById(List<T> list, String id) {

        for (T item : list) {
            if (item == null) {
                continue;
            }
            if (StringUtils.equals(item.getId(), id)) {
                return item;
            }
        }
        return null;
    }

    static <T extends Identifiable> T findByCode(List<T> list, String code) {

        for (T item : list) {
            if (StringUtils.equals(item.getCode(), code)) {
                return item;
            }
        }
        return null;
    }

    static <T extends Identifiable> T findByName(List<T> list, String name) {

        for (T item : list) {
            if (StringUtils.equals(item.getName(), name)) {
                return item;
            }
        }
        return null;
    }

    static <T extends Identifiable> void addIfNotPresent(List<T> list, T identifable) {
        if (findById(list, identifable.getId()) == null) {
            list.add(identifable);
        }
    }

    static <T extends Identifiable> void addIfNotPresent(List<T> list, List<T> identifableList) {
        identifableList.forEach(identifable -> {
            if (findById(list, identifable.getId()) == null) {
                list.add(identifable);
            }
        });
    }

    static Identifiable create(String code, String name) {
        Identifiable identifiable = new IdentifiableImpl();
        identifiable.setId(UUID.randomUUID().toString());
        identifiable.setCode(code);
        identifiable.setName(name);
        return identifiable;
    }

    static Identifiable createIdentifiable(String id, String name) {
        Identifiable identifiable = new IdentifiableImpl();
        identifiable.setId(id);
        identifiable.setName(name);
        return identifiable;
    }

    static Identifiable createIdentifiable(String id, String name, String code) {
        Identifiable identifiable = createIdentifiable(id, name);
        identifiable.setCode(code);
        return identifiable;
    }

    static void ensureId(Identifiable identifiable) {
        if (identifiable == null) {
            return;
        }
        if (StringUtils.isBlank(identifiable.getId())) {
            identifiable.setId(id());
        }
    }

    static void ensureId(List<? extends Identifiable> identifiables) {
        identifiables.forEach(ident -> ensureId(ident));
    }

    static <T extends Identifiable> String name(T firstChoice, T secondChoice) {
        if (firstChoice != null && StringUtils.isNotBlank(firstChoice.getName())) {
            return firstChoice.getName();
        }
        if (secondChoice != null) {
            return secondChoice.getName();
        }
        return null;
    }

    static <T extends Identifiable> String code(T firstChoice, T secondChoice) {
        if (firstChoice != null && StringUtils.isNotBlank(firstChoice.getCode())) {
            return firstChoice.getCode();
        }
        if (secondChoice != null) {
            return secondChoice.getCode();
        }
        return null;
    }

    static boolean isValidString(String left, String right) {
        if (left == null) {
            return true;
        }
        return StringUtils.equals(left, right);
    }

    static boolean isValidInt(int left, int right) {
        if (left == 0) {
            return true;
        }
        return left == right;
    }
}
