package au.com.eventsecretary.builder;

import au.com.eventsecretary.accounting.registration.Registration;
import au.com.eventsecretary.accounting.registration.RegistrationImpl;
import au.com.eventsecretary.accounting.registration.RegistrationMetadata;
import au.com.eventsecretary.accounting.registration.RegistrationStatus;
import au.com.eventsecretary.accounting.registration.RegistrationType;
import au.com.eventsecretary.accounting.registration.RegistrationValue;
import au.com.eventsecretary.accounting.registration.RegistrationValueImpl;
import au.com.eventsecretary.accounting.registration.RegistrationValueMetadata;
import au.com.eventsecretary.common.Period;
import au.com.eventsecretary.common.PeriodImpl;
import au.com.eventsecretary.common.Timestamp;
import au.com.eventsecretary.people.Person;

import static au.com.eventsecretary.simm.IdentifiableUtils.findByCode;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class RegistrationBuilder {
    private Registration registration;
    private RegistrationMetadata registrationMetadata;

    public static RegistrationBuilder registrationBuilder(RegistrationMetadata registrationMetadata) {
        return new RegistrationBuilder(registrationMetadata);
    }

    private RegistrationBuilder(RegistrationMetadata registrationMetadata) {
        this.registrationMetadata = registrationMetadata;
        registration = new RegistrationImpl();
        registration.setRegistrationMetadataId(registrationMetadata.getId());
        registration.setStatus(RegistrationStatus.active);
        registration.setScope(registrationMetadata.getRegistrationScope());
        registration.setTarget(registrationMetadata.getRegistrationTarget());
    }

    public RegistrationBuilder owner(Person owner) {
        registration.setName(owner.getName());
        registration.setOwnerId(owner.getId());
        return this;
    }

    public RegistrationBuilder owner(String ownerId) {
        registration.setOwnerId(ownerId);
        return this;
    }

    public RegistrationBuilder name(String name) {
        registration.setName(name);
        return this;
    }

    public RegistrationBuilder association(String associationId) {
        registration.setAssociationId(associationId);
        return this;
    }

    public RegistrationValueBuilder value(RegistrationValueMetadata registrationValueMetadata) {
        return new RegistrationValueBuilder(registrationValueMetadata);
    }

    public RegistrationValueBuilder value(String code) {
        return new RegistrationValueBuilder(findByCode(registrationMetadata.getRegistrationValueMetadata(), code));
    }

    public RegistrationValueBuilder value(RegistrationType type) {
        RegistrationValueMetadata registrationValueMetadata = registrationMetadata.getRegistrationValueMetadata().stream().filter(rvm -> rvm.getRegistrationType() == type).findFirst().orElse(null);
        return new RegistrationValueBuilder(registrationValueMetadata);
    }

    public RegistrationBuilder renewal(Timestamp start, Timestamp end) {
        Period period = new PeriodImpl();
        period.setStart(start);
        period.setEnd(end);
        registration.getRenewals().add(period);
        return this;
    }

    public RegistrationBuilder renewal(Period period) {
        registration.getRenewals().add(period);
        return this;
    }

    public class RegistrationValueBuilder {
        RegistrationValue value;

        public RegistrationValueBuilder(RegistrationValueMetadata registrationValueMetadata) {
            if (registrationValueMetadata != null) {
                value = new RegistrationValueImpl();
                value.setRegistrationValueMetadataId(registrationValueMetadata.getId());
                value.setType(registrationValueMetadata.getRegistrationType());
                registration.getValue().add(value);
            }
        }

        public RegistrationValueBuilder type(RegistrationType registrationType) {
            if (value != null) {
                value.setType(registrationType);
            }
            return this;
        }

        public RegistrationValueBuilder value(String value) {
            if (this.value != null) {
                this.value.setValue(value);
            }
            return this;
        }

        public RegistrationValueBuilder value(boolean value) {
            if (this.value != null) {
                this.value.setValue(value ? "true" : "false");
            }
            return this;
        }

        public RegistrationBuilder end() {
            return RegistrationBuilder.this;
        }
    }

    public Registration build() {
        return registration;
    }
}
