package au.com.eventsecretary.builder;

import au.com.eventsecretary.accounting.event.Cancellation;
import au.com.eventsecretary.accounting.event.EventMetadata;
import au.com.eventsecretary.accounting.event.EventMetadataImpl;
import au.com.eventsecretary.accounting.event.Refund;
import au.com.eventsecretary.accounting.organisation.Association;
import au.com.eventsecretary.accounting.organisation.AssociationImpl;
import au.com.eventsecretary.accounting.organisation.Term;
import au.com.eventsecretary.accounting.organisation.TermImpl;
import au.com.eventsecretary.accounting.organisation.VocabularyImpl;
import au.com.eventsecretary.accounting.registration.Conditions;
import au.com.eventsecretary.accounting.registration.ConditionsImpl;
import au.com.eventsecretary.accounting.registration.RegistrationTarget;
import au.com.eventsecretary.client.AssociationClient;

import java.util.Arrays;

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

    public RegistrationMetadataBuilder<AssociationBuilder> registration(RegistrationTarget registrationTarget) {
        return RegistrationMetadataBuilder.builder(registrationTarget, this, this.association.getRegistrationMetadata(), this.association.getPricingSchedules());
    }

    public RequirementBuilder<AssociationBuilder> requirement(String code) {
        return RequirementBuilder.builder(code, association.getRequirements(), association.getConditions(), this);
    }

    public ConditionsBuilder<AssociationBuilder> condition(String code)
    {
        Conditions conditions = new ConditionsImpl();
        association.getConditions().add(conditions);
        return new ConditionsBuilder(code, conditions, this);
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
