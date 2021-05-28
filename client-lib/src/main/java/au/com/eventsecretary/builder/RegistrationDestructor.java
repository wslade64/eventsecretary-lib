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

    public boolean riderInsurance(List<Registration> registrations) {
        String value = value(hasRiderInsurance(registrationMetadataList), registrations);
        return "true".equals(value);
    }

    public String horseMembershipNumber(List<Registration> registrations) {
        return value(hasHorseMembershipNumber(registrationMetadataList), registrations);
    }

    public String horseLicenseNumber(List<Registration> registrations) {
        return value(hasHorseLicenseNumber(registrationMetadataList), registrations);
    }

    public String horseDressageLevel(List<Registration> registrations) {
        return value(hasHorseDressageLevel(registrationMetadataList), registrations);
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

    public boolean hasHorseDressageLevel() {
        return hasHorseDressageLevel(registrationMetadataList) != null;
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

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasClubs(List<RegistrationMetadata> registrationMetadataList) {
        return hasRegistration(registrationMetadataList, "riderRegistration", RegistrationType.organisation, RegistrationValueType.reference);
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasRiderMembershipNumber(List<RegistrationMetadata> registrationMetadataList) {
        return hasRegistration(registrationMetadataList, "riderRegistration", RegistrationType.number, RegistrationValueType.pattern);
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasRiderInsurance(List<RegistrationMetadata> registrationMetadataList) {
        return hasRegistration(registrationMetadataList, "riderRegistration", RegistrationType.insurance, RegistrationValueType.binary);
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasHorseMembershipNumber(List<RegistrationMetadata> registrationMetadataList) {
        return hasRegistration(registrationMetadataList, "horseRegistration", RegistrationType.number, RegistrationValueType.pattern);
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasHorseLicenseNumber(List<RegistrationMetadata> registrationMetadataList) {
        return hasRegistration(registrationMetadataList, "dressageLicense", RegistrationType.license, RegistrationValueType.pattern);
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasHorseDressageLevel(List<RegistrationMetadata> registrationMetadataList) {
        return hasRegistration(registrationMetadataList, "drLevel", RegistrationType.name, RegistrationValueType.list);
    }

    public static Pair<RegistrationMetadata, RegistrationValueMetadata> hasRegistration(List<RegistrationMetadata> registrationMetadataList
        , String registrationMetadataCode
        , RegistrationType registrationType
        , RegistrationValueType registrationValueType) {
        RegistrationMetadata registrationMetadata = findByCode(registrationMetadataList, registrationMetadataCode);
        if (registrationMetadata == null) {
            return null;
        }
        RegistrationValueMetadata registrationValueMetadata = registrationMetadata.getRegistrationValueMetadata()
                .stream()
                .filter(registrationValueMetadata1 -> registrationValueMetadata1.getRegistrationType() == registrationType
                        && registrationValueMetadata1.getRegistrationValueType() == registrationValueType)
                .findFirst()
                .orElse(null);
        if (registrationValueMetadata == null) {
            return null;
        }
        return Pair.of(registrationMetadata, registrationValueMetadata);
    }
}
