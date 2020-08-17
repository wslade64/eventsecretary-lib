package au.com.eventsecretary.simm;

import au.com.eventsecretary.common.Identifiable;
import au.com.eventsecretary.people.Address;
import au.com.eventsecretary.people.Person;
import au.com.eventsecretary.user.identity.Identity;
import au.com.eventsecretary.user.identity.Role;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static au.com.eventsecretary.simm.IdentifiableUtils.cloneIdentifiable;

public interface IdentityUtils {
    static String cleanPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return phoneNumber;
        }
        phoneNumber = phoneNumber.trim();
        phoneNumber = phoneNumber.replaceAll("[\\s()-]+", "");
        if (phoneNumber.startsWith("+61")) {
            phoneNumber = "0" + phoneNumber.substring("+61".length());
        }
        return phoneNumber;
    }

    static String cleanEmailAddress(String emailAddress) {
        if (emailAddress == null) {
            return emailAddress;
        }
        return emailAddress.trim().toLowerCase();
    }

    static String cleanName(String name) {
        if (name == null) {
            return name;
        }
        return WordUtils.capitalizeFully(name.trim());
    }

    static String join(Address address) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(address.getAddressLineOne());
        stringBuilder.append(",");
        stringBuilder.append(address.getAddressLineTwo());
        stringBuilder.append(",");
        stringBuilder.append(address.getSuburb());
        stringBuilder.append(",");
        stringBuilder.append(address.getState());
        stringBuilder.append(",");
        stringBuilder.append(address.getPostCode());
        return stringBuilder.toString();
    }
    static String[] splitName(String name) {
        if (StringUtils.isEmpty(name)) {
            return new String[] {};
        }
        name = name.trim();
        int index = name.indexOf(" ");
        if (index == -1) {
            return new String[] {
                    name
            };
        }
        String part1 = name.substring(0, index);
        String part2 = name.substring(index + 1).trim();
        if (part2.length() == 1) {
            return new String[] {
                    part1
            };
        }
        return new String[] {
                part1,
                part2
        };

    }

    static Map<String, String> stringsToMap(String string) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(string)) {
            return map;
        }
        String[] list = string.split(",");
        for (String pairString : list) {
            String[] pair = pairString.split("=");
            map.put(pair[0], pair.length == 2 ? pair[1] : null);
        }
        return map;
    }

    static Map<String, String> stringsToMapReverse(String string) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(string)) {
            return map;
        }
        String[] list = string.split(",");
        for (String pairString : list) {
            String[] pair = pairString.split("=");
            if (pair.length == 2) {
                map.put(pair[1], pair[0]);
            }
        }
        return map;
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

    static int comparePersonName(Person left, Person right) {
        String leftLastName = left != null ? left.getLastName() : null;
        String rightLastName = right != null ? right.getLastName() : null;
        int c = ObjectUtils.compare(leftLastName, rightLastName);
        if (c != 0) {
            return c;
        }

        String leftFirstName = left != null ? left.getFirstName() : null;
        String rightFirstName = right != null ? right.getFirstName() : null;
        return ObjectUtils.compare(leftFirstName, rightFirstName);
    }

    static int comparePersonPhoneNumber(Person left, Person right) {
        String leftPhoneNumber = left != null && left.getContactDetails() != null ? left.getContactDetails().getPhoneNumber() : null;
        String rightPhoneNumber = right != null && right.getContactDetails() != null ? right.getContactDetails().getPhoneNumber() : null;
        return ObjectUtils.compare(leftPhoneNumber, rightPhoneNumber);
    }

    static int comparePersonEmailAddress(Person left, Person right) {
        String leftEmailAddress = left != null && left.getContactDetails() != null ? left.getContactDetails().getEmailAddress() : null;
        String rightEmailAddress = right != null && right.getContactDetails() != null ? right.getContactDetails().getEmailAddress() : null;
        return ObjectUtils.compare(leftEmailAddress, rightEmailAddress);
    }
}
