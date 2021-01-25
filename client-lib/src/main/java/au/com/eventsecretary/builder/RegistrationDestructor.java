package au.com.eventsecretary.builder;

import au.com.eventsecretary.accounting.registration.Registration;
import au.com.eventsecretary.accounting.registration.RegistrationMetadata;
import au.com.eventsecretary.accounting.registration.RegistrationType;
import au.com.eventsecretary.accounting.registration.RegistrationValue;
import au.com.eventsecretary.accounting.registration.RegistrationValueMetadata;
import au.com.eventsecretary.accounting.registration.RegistrationValueType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static au.com.eventsecretary.simm.IdentifiableUtils.findByCode;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class RegistrationDestructor {
    private final List<RegistrationMetadata> registrationMetadataList;

    public RegistrationDestructor(List<RegistrationMetadata> registrationMetadataList) {
        this.registrationMetadataList = registrationMetadataList;
    }

    public static RegistrationDestructor destruct(List<RegistrationMetadata> registrationMetadataList) {
        return new RegistrationDestructor(registrationMetadataList);
    }

    public String club(List<Registration> registrations) {
        return value(hasClubs(registrationMetadataList), registrations);
    }

    public String riderMembershipNumber(List<Registration> registrations) {
        return value(hasRiderMembershipNumber(registrationMetadataList), registrations);
    }

    public String horseMembershipNumber(List<Registration> registrations) {
        return value(hasHorseMembershipNumber(registrationMetadataList), registrations);
    }

    public String horseLicenseNumber(List<Registration> registrations) {
        return value(hasHorseLicenseNumber(registrationMetadataList), registrations);
    }

    public boolean hasClubs() {
        return hasClubs(registrationMetadataList) != null;
    }

    public boolean hasRiderMembershipNumber() {
        return hasRiderMembershipNumber(registrationMetadataList) != null;
    }

    public boolean hasHorseMembershipNumber() {
        return hasHorseMembershipNumber(registrationMetadataList) != null;
    }

    public static String value(Pair<RegistrationMetadata, RegistrationValueMetadata> pair, List<Registration> registrations) {
        if (pair == null) {
            return null;
        }
        Registration registration = registrations
                .stream()
                .filter(registration1 -> StringUtils.equals(registration1.getRegistrationMetadataId(), pair.getLeft().getId()))
                .findFirst()
                .orElse(null);
        if (registration == null) {
            return null;
        }
        try {
            RegistrationValue registrationValue = registration
                    .getValue()
                    .stream()
                    .filter(registrationValue1 -> StringUtils.equals(registrationValue1.getRegistrationValueMetadataId(), pair.getRight().getId()))
                    .findFirst()
                    .orElse(null);
            if (registrationValue == null) {
                return null;
            }
            return registrationValue.getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasClubs(List<RegistrationMetadata> registrationMetadata) {
        RegistrationMetadata riderRegistration = findByCode(registrationMetadata, "riderRegistration");
        if (riderRegistration == null) {
            return null;
        }
        RegistrationValueMetadata registrationValueMetadata = riderRegistration.getRegistrationValueMetadata()
                .stream()
                .filter(registrationValueMetadata1 -> registrationValueMetadata1.getRegistrationType() == RegistrationType.organisation
                        && registrationValueMetadata1.getRegistrationValueType() == RegistrationValueType.reference)
                .findFirst()
                .orElse(null);
        if (registrationValueMetadata == null) {
            return null;
        }
        return Pair.of(riderRegistration, registrationValueMetadata);
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasRiderMembershipNumber(List<RegistrationMetadata> registrationMetadata) {
        RegistrationMetadata riderRegistration = findByCode(registrationMetadata, "riderRegistration");
        if (riderRegistration == null) {
            return null;
        }
        RegistrationValueMetadata registrationValueMetadata = riderRegistration.getRegistrationValueMetadata()
                .stream()
                .filter(registrationValueMetadata1 -> registrationValueMetadata1.getRegistrationType() == RegistrationType.number
                        && registrationValueMetadata1.getRegistrationValueType() == RegistrationValueType.pattern)
                .findFirst()
                .orElse(null);
        if (registrationValueMetadata == null) {
            return null;
        }
        return Pair.of(riderRegistration, registrationValueMetadata);
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasHorseMembershipNumber(List<RegistrationMetadata> registrationMetadataList) {
        RegistrationMetadata registrationMetadata = findByCode(registrationMetadataList, "horseRegistration");
        if (registrationMetadata == null) {
            return null;
        }
        RegistrationValueMetadata registrationValueMetadata = registrationMetadata.getRegistrationValueMetadata()
                .stream()
                .filter(registrationValueMetadata1 -> registrationValueMetadata1.getRegistrationType() == RegistrationType.number
                        && registrationValueMetadata1.getRegistrationValueType() == RegistrationValueType.pattern)
                .findFirst()
                .orElse(null);
        if (registrationValueMetadata == null) {
            return null;
        }
        return Pair.of(registrationMetadata, registrationValueMetadata);
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasHorseLicenseNumber(List<RegistrationMetadata> registrationMetadataList) {
        RegistrationMetadata registrationMetadata = findByCode(registrationMetadataList, "dressageLicense");
        if (registrationMetadata == null) {
            return null;
        }
        RegistrationValueMetadata registrationValueMetadata = registrationMetadata.getRegistrationValueMetadata()
                .stream()
                .filter(registrationValueMetadata1 -> registrationValueMetadata1.getRegistrationType() == RegistrationType.license
                        && registrationValueMetadata1.getRegistrationValueType() == RegistrationValueType.pattern)
                .findFirst()
                .orElse(null);
        if (registrationValueMetadata == null) {
            return null;
        }
        return Pair.of(registrationMetadata, registrationValueMetadata);
    }
}
