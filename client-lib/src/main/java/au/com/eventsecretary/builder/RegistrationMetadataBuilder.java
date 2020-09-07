package au.com.eventsecretary.builder;

import au.com.eventsecretary.accounting.pricing.PricingSchedule;
import au.com.eventsecretary.accounting.registration.Conditions;
import au.com.eventsecretary.accounting.registration.ConditionsImpl;
import au.com.eventsecretary.accounting.registration.RegistrationMetadata;
import au.com.eventsecretary.accounting.registration.RegistrationMetadataImpl;
import au.com.eventsecretary.accounting.registration.RegistrationScopes;
import au.com.eventsecretary.accounting.registration.RegistrationTarget;
import au.com.eventsecretary.accounting.registration.RegistrationType;
import au.com.eventsecretary.accounting.registration.RegistrationValueMetadata;
import au.com.eventsecretary.accounting.registration.RegistrationValueMetadataImpl;
import au.com.eventsecretary.accounting.registration.RegistrationValueType;

import java.math.BigDecimal;
import java.util.List;

import static au.com.eventsecretary.CollectionUtility.addArrayToList;
import static au.com.eventsecretary.CollectionUtility.toBigDecimalArray;
import static au.com.eventsecretary.builder.PricingBuilder.listPricing;
import static au.com.eventsecretary.builder.PricingBuilder.unitPricing;
import static au.com.eventsecretary.simm.IdentifiableUtils.id;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class RegistrationMetadataBuilder<P> {
    private final String[] days = {"1", "2"};

    private RegistrationMetadata registrationMetadata;
    private final P parent;
    private final List<PricingSchedule> pricingSchedules;

    public static RegistrationMetadataBuilder<Void> builder(RegistrationTarget registrationTarget) {
        return new RegistrationMetadataBuilder<>(registrationTarget, null, null, null);
    }

    public static <P> RegistrationMetadataBuilder<P> builder(RegistrationTarget registrationTarget, P parent, List<RegistrationMetadata> registrationMetadataList, List<PricingSchedule> pricingSchedules) {
        return new RegistrationMetadataBuilder<>(registrationTarget, parent, registrationMetadataList, pricingSchedules);
    }

    private RegistrationMetadataBuilder(RegistrationTarget registrationTarget, P parent, List<RegistrationMetadata> registrationMetadataList, List<PricingSchedule> pricingSchedules) {
        this.parent = parent;
        this.registrationMetadata = new RegistrationMetadataImpl();
        this.registrationMetadata.setId(id());
        this.registrationMetadata.setRegistrationTarget(registrationTarget);
        this.registrationMetadata.setRegistrationScope(RegistrationScopes.permanent);
        this.pricingSchedules = pricingSchedules;
        if (registrationMetadataList != null) {
            registrationMetadataList.add(registrationMetadata);
        }
    }

    public RegistrationMetadataBuilder<P> temporary() {
        registrationMetadata.setRegistrationScope(RegistrationScopes.temporary);
        return this;
    }

    private Conditions registrationMetadataConditions() {
        Conditions registrationMetadataConditions = registrationMetadata.getConditions();
        if (registrationMetadataConditions == null) {
            registrationMetadataConditions = new ConditionsImpl();
            registrationMetadata.setConditions(registrationMetadataConditions);
        }
        return registrationMetadataConditions;
    }

    public RegistrationMetadataBuilder<P> preamble(String[] preamble) {
        addArrayToList(registrationMetadataConditions(), Conditions::getPreamble, preamble);
        return this;
    }

    public RegistrationMetadataBuilder<P> conditions(String[] conditions) {
        addArrayToList(registrationMetadataConditions(), Conditions::getConditions, conditions);
        return this;
    }

    public RegistrationValueMetadataBuilder value(RegistrationType registrationType, RegistrationValueType RegistrationValueType) {
        return new RegistrationValueMetadataBuilder(registrationType, RegistrationValueType);
    }

    public RegistrationMetadataBuilder<P> code(String code) {
        registrationMetadata.setCode(code);
        return this;
    }

    public RegistrationMetadataBuilder<P> name(String name) {
        registrationMetadata.setName(name);
        return this;
    }

    public RegistrationMetadataBuilder<P> startDate(int startDate) {
        registrationMetadata.setStartDate(startDate);
        return this;
    }

    public RegistrationMetadataBuilder<P> cost(int... values) {
        String target = String.format("association.registration.%s", registrationMetadata.getCode());
        PricingSchedule pricingSchedule = values.length == 1
                ? unitPricing(target, new BigDecimal(values[0]))
                : listPricing(target, days, toBigDecimalArray(values));
        pricingSchedule.setName(registrationMetadata.getName());
        pricingSchedule.setCode(registrationMetadata.getCode());
        pricingSchedules.add(pricingSchedule);
        return this;
    }

    public P end() {
        return parent;
    }

    public RegistrationMetadata build() {
        return registrationMetadata;
    }

    public class RegistrationValueMetadataBuilder {
        private RegistrationValueMetadata registrationValueMetadata;

        private RegistrationValueMetadataBuilder(RegistrationType registrationType, RegistrationValueType registrationValueType) {
            registrationValueMetadata = new RegistrationValueMetadataImpl();
            registrationValueMetadata.setId(id());
            registrationValueMetadata.setRegistrationType(registrationType);
            registrationValueMetadata.setRegistrationValueType(registrationValueType);
            RegistrationMetadataBuilder.this.registrationMetadata.getRegistrationValueMetadata().add(registrationValueMetadata);
        }

        public RegistrationValueMetadataBuilder constraint(String constraint) {
            registrationValueMetadata.setRegistrationConstraint(constraint);
            return this;
        }

        public RegistrationValueMetadataBuilder code(String code) {
            registrationValueMetadata.setCode(code);
            return this;
        }

        public RegistrationValueMetadataBuilder name(String name) {
            registrationValueMetadata.setName(name);
            return this;
        }

        public RegistrationMetadataBuilder<P> end() {
            return RegistrationMetadataBuilder.this;
        }

    }
}
