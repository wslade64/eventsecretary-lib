package au.com.eventsecretary.simm;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.common.Identifiable;
import au.com.eventsecretary.common.IdentifiableImpl;

import java.util.List;

/**
 * @author sladew
 */
public interface IdentifiableUtils {
    static void checkDuplicate(List<? extends Identifiable> list, Identifiable facilityType) {
        for (Identifiable type : list) {
            if (type.getName().equals(facilityType.getName())) {
                throw new ResourceExistsException("The name specified is currently in use!");
            }
        }
    }

    static Identifiable cloneIdentifiable(Identifiable facilityType) {
        IdentifiableImpl identifiable = new IdentifiableImpl();
        identifiable.setId(facilityType.getId());
        identifiable.setName(facilityType.getName());
        return identifiable;
    }

    static <T extends Identifiable> T findById(List<T> list, String id) {

        for (T item : list) {
            if (item.getId().equals(id)) {
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

}
