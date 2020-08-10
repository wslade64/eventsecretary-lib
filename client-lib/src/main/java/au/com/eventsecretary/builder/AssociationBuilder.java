package au.com.eventsecretary.builder;

import au.com.eventsecretary.accounting.event.Cancellation;
import au.com.eventsecretary.accounting.event.EventMetadata;
import au.com.eventsecretary.accounting.event.EventMetadataImpl;
import au.com.eventsecretary.accounting.event.Refund;
import au.com.eventsecretary.accounting.organisation.Association;
import au.com.eventsecretary.accounting.organisation.AssociationImpl;
import au.com.eventsecretary.accounting.organisation.Requirement;
import au.com.eventsecretary.accounting.organisation.RequirementImpl;
import au.com.eventsecretary.accounting.organisation.RequirementType;
import au.com.eventsecretary.accounting.organisation.RequirementValue;
import au.com.eventsecretary.accounting.organisation.RequirementValueImpl;
import au.com.eventsecretary.accounting.organisation.Term;
import au.com.eventsecretary.accounting.organisation.TermImpl;
import au.com.eventsecretary.accounting.organisation.VocabularyImpl;
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
import au.com.eventsecretary.client.AssociationClient;
import au.com.eventsecretary.equestrian.organisation.Operation;

import java.math.BigDecimal;
import java.util.Arrays;

import static au.com.eventsecretary.CollectionUtility.addArrayToList;
import static au.com.eventsecretary.CollectionUtility.toBigDecimalArray;
import static au.com.eventsecretary.builder.PricingBuilder.listPricing;
import static au.com.eventsecretary.builder.PricingBuilder.unitPricing;
import static au.com.eventsecretary.simm.IdentifiableUtils.findByCode;
import static au.com.eventsecretary.simm.IdentifiableUtils.id;

public class AssociationBuilder {
    private final AssociationClient associationClient;
    private final Association association;

    public AssociationBuilder(String code, String name, AssociationClient associationClient) {
        this.association = new AssociationImpl();
        this.association.setCode(code);
        this.association.setName(name);
        this.associationClient = associationClient;
    }

    public VocabularyBuilder vocabulary() {
        this.association.setVocabulary(new VocabularyImpl());
        return new VocabularyBuilder();
    }

    public RegistrationMetadataBuilder registration(RegistrationTarget registrationTarget) {
        return new RegistrationMetadataBuilder(registrationTarget);
    }

    public RequirementBuilder requirement(String code) {
        return new RequirementBuilder(code);
    }

    public ConditionsBuilder<AssociationBuilder> condition(String code)
    {
        Conditions conditions = new ConditionsImpl();
        association.getConditions().add(conditions);
        return new ConditionsBuilder(code, conditions, this);
    }

    public class RequirementBuilder {
        Requirement requirement;

        RequirementBuilder(String code) {
            requirement = new RequirementImpl();
            requirement.setId(id());
            requirement.setCode(code);
            association.getRequirements().add(requirement);
        }

        public RequirementBuilder name(String name) {
            requirement.setName(name);
            return this;
        }

        public RequirementBuilder code(String code) {
            requirement.setCode(code);
            return this;
        }


        public RequirementBuilder operation(Operation operation) {
            requirement.setOperation(operation);
            return this;
        }

        private RequirementValue value(String value, String operand, RequirementType requirementType) {
            RequirementValue requirementValue = new RequirementValueImpl();
            requirementValue.setRequirementType(requirementType);
            requirementValue.setValue(value);
            requirementValue.setOperand(operand);
            requirement.getRequirementValues().add(requirementValue);
            return requirementValue;
        }

        private Conditions requirementConditions() {
            Conditions requirementConditions = requirement.getConditions();
            if (requirementConditions == null) {
                requirementConditions = new ConditionsImpl();
                requirement.setConditions(requirementConditions);
            }
            return requirementConditions;
        }

        public RequirementBuilder registration$(String value, String operand) {
            value(value, operand, RequirementType.registration);
            return this;
        }

        public RequirementBuilder conditions$(String[] conditions) {
            addArrayToList(requirementConditions(), Conditions::getConditions, conditions);
            return this;
        }

        public RequirementBuilder preamble$(String[] preamble) {
            addArrayToList(requirementConditions(), Conditions::getPreamble, preamble);
            return this;
        }

        public class RequirementValueBuilder {
            private final RequirementValue requirementValue;

            RequirementValueBuilder(RequirementValue requirementValue) {
                this.requirementValue = requirementValue;
            }

            private Conditions requirementValueConditions() {
                Conditions requirementValueConditions = requirementValue.getConditions();
                if (requirementValueConditions == null) {
                    requirementValueConditions = new ConditionsImpl();
                    requirementValue.setConditions(requirementValueConditions);
                }
                return requirementValueConditions;
            }

            public RequirementValueBuilder conditions(String[] conditions) {
                addArrayToList(requirementValueConditions(), Conditions::getConditions, conditions);
                return this;
            }

            public RequirementValueBuilder preamble(String[] preamble) {
                addArrayToList(requirementValueConditions(), Conditions::getPreamble, preamble);
                return this;
            }

            public RequirementValueBuilder conditionRef(String associationConditionCode) {
                Conditions conditions = findByCode(association.getConditions(), associationConditionCode);
                requirementValueConditions().setId(conditions.getId());
                return this;
            }

            public RequirementValueBuilder name(String name) {
                requirementValue.setName(name);
                return this;
            }

            public RequirementBuilder end() {
                return RequirementBuilder.this;
            }

        }

        public RequirementValueBuilder registration(String value, String operand) {
            return new RequirementValueBuilder(value(value, operand, RequirementType.registration));
        }

        public RequirementBuilder competitor$(String value, String operand) {
            value(value, operand, RequirementType.competitor);
            return this;
        }
        public RequirementBuilder companion$(String value, String operand) {
            value(value, operand, RequirementType.companion);
            return this;
        }

        public RequirementBuilder declaration$() {
            value(null, null, RequirementType.declaration);
            return this;
        }
        public RequirementBuilder declaration$(String value) {
            value(value, null, RequirementType.declaration);
            return this;
        }

        public AssociationBuilder end() {
            return AssociationBuilder.this;
        }

    }

    public class RegistrationMetadataBuilder {
        private final String[] days = {"1", "2"};

        private RegistrationMetadata registrationMetadata;

        private RegistrationMetadataBuilder(RegistrationTarget registrationTarget) {
            registrationMetadata = new RegistrationMetadataImpl();
            registrationMetadata.setId(id());
            registrationMetadata.setRegistrationTarget(registrationTarget);
            registrationMetadata.setRegistrationScope(RegistrationScopes.permanent);
            AssociationBuilder.this.association.getRegistrationMetadata().add(registrationMetadata);
        }

        public RegistrationMetadataBuilder temporary() {
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

        public RegistrationMetadataBuilder preamble(String[] preamble) {
            addArrayToList(registrationMetadataConditions(), Conditions::getPreamble, preamble);
            return this;
        }

        public RegistrationMetadataBuilder conditions(String[] conditions) {
            addArrayToList(registrationMetadataConditions(), Conditions::getConditions, conditions);
            return this;
        }

        public RegistrationValueMetadataBuilder value(RegistrationType registrationType, RegistrationValueType RegistrationValueType) {
            return new RegistrationValueMetadataBuilder(registrationType, RegistrationValueType);
        }

        public RegistrationMetadataBuilder code(String code) {
            registrationMetadata.setCode(code);
            return this;
        }

        public RegistrationMetadataBuilder name(String name) {
            registrationMetadata.setName(name);
            return this;
        }

        public RegistrationMetadataBuilder cost(int... values) {
            String target = String.format("association.registration.%s", registrationMetadata.getCode());
            PricingSchedule pricingSchedule = values.length == 1
                    ? unitPricing(target, new BigDecimal(values[0]))
                    : listPricing(target, days, toBigDecimalArray(values));
            pricingSchedule.setName(registrationMetadata.getName());
            pricingSchedule.setCode(registrationMetadata.getCode());
            association.getPricingSchedules().add(pricingSchedule);
            return this;
        }

        public AssociationBuilder end() {
            return AssociationBuilder.this;
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

            public RegistrationMetadataBuilder end() {
                return RegistrationMetadataBuilder.this;
            }

        }
    }

    public class EventMetadataBuilder {
        public EventMetadataBuilder preamble(String[] preamble) {
            association.getEventMetadata().getPreamble().addAll(Arrays.asList(preamble));
            return this;
        }
        public EventMetadataBuilder conditions(String[] conditions) {
            conditions[0] = String.format(conditions[0], association.getName());
            association.getEventMetadata().getConditions().addAll(Arrays.asList(conditions));
            return this;
        }
        public AssociationBuilder end() {
            return AssociationBuilder.this;
        }
    }

    public EventMetadataBuilder event(Refund refund, Cancellation cancellation) {
        EventMetadata eventMetadata = new EventMetadataImpl();
        association.setEventMetadata(eventMetadata);
        eventMetadata.setRefunds(refund);
        eventMetadata.setCancellation(cancellation);
        return new EventMetadataBuilder();
    }

    public Association build() {
        association.setId(associationClient.createAssociation(association));
        return association;
    }

    public class VocabularyBuilder {
        private Term term(String singular, String plural) {
            Term term = new TermImpl();
            term.setPlural(plural);
            term.setSingular(singular);
            return term;
        }

        public VocabularyBuilder organisation(String singular, String plural) {
            association.getVocabulary().setOrganisation(term(singular, plural));
            return this;
        }
        public VocabularyBuilder competitor(String singular, String plural) {
            association.getVocabulary().setCompetitor(term(singular, plural));
            return this;
        }
        public VocabularyBuilder companion(String singular, String plural) {
            association.getVocabulary().setCompanion(term(singular, plural));
            return this;
        }

        public AssociationBuilder end() { return AssociationBuilder.this; }
    }
}
