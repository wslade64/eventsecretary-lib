package au.com.eventsecretary.builder;

import au.com.eventsecretary.accounting.organisation.Requirement;
import au.com.eventsecretary.accounting.organisation.RequirementImpl;
import au.com.eventsecretary.accounting.organisation.RequirementType;
import au.com.eventsecretary.accounting.organisation.RequirementValue;
import au.com.eventsecretary.accounting.organisation.RequirementValueImpl;
import au.com.eventsecretary.accounting.registration.Conditions;
import au.com.eventsecretary.accounting.registration.ConditionsImpl;
import au.com.eventsecretary.equestrian.organisation.Operation;

import java.util.List;

import static au.com.eventsecretary.CollectionUtility.addArrayToList;
import static au.com.eventsecretary.simm.IdentifiableUtils.findByCode;
import static au.com.eventsecretary.simm.IdentifiableUtils.id;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class RequirementBuilder<P> {
    private final Requirement requirement;
    private final P parent;
    private final List<Conditions> conditions;

    public static RequirementBuilder<Void> builder(String code, List<Requirement> requirementList) {
        return new RequirementBuilder<Void>(code, requirementList, null, null);
    }

    public static <P> RequirementBuilder<P> builder(String code, List<Requirement> requirementList, List<Conditions> conditions, P parent) {
        return new RequirementBuilder<P>(code, requirementList, conditions, parent);
    }

    private RequirementBuilder(String code, List<Requirement> requirementList, List<Conditions> conditions, P parent) {
        this.parent = parent;
        requirement = new RequirementImpl();
        requirement.setId(id());
        requirement.setCode(code);
        requirementList.add(requirement);
        this.conditions = conditions;
    }

    public RequirementBuilder<P> name(String name) {
        requirement.setName(name);
        return this;
    }

    public RequirementBuilder<P> code(String code) {
        requirement.setCode(code);
        return this;
    }

    public RequirementBuilder<P> operation(Operation operation) {
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

    public RequirementBuilder<P> registration$(String value, String operand) {
        value(value, operand, RequirementType.registration);
        return this;
    }

    public RequirementBuilder<P> conditions$(String[] conditions) {
        addArrayToList(requirementConditions(), Conditions::getConditions, conditions);
        return this;
    }

    public RequirementBuilder<P> preamble$(String[] preamble) {
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
            Conditions conditions = findByCode(RequirementBuilder.this.conditions, associationConditionCode);
            requirementValueConditions().setId(conditions.getId());
            return this;
        }

        public RequirementValueBuilder name(String name) {
            requirementValue.setName(name);
            return this;
        }

        public RequirementBuilder<P> end() {
            return RequirementBuilder.this;
        }
    }

    public RequirementValueBuilder registration(String value, String operand) {
        return new RequirementValueBuilder(value(value, operand, RequirementType.registration));
    }

    public RequirementBuilder<P> competitor$(String value, String operand) {
        value(value, operand, RequirementType.competitor);
        return this;
    }

    public RequirementBuilder<P> companion$(String value, String operand) {
        value(value, operand, RequirementType.companion);
        return this;
    }

    public RequirementBuilder<P> declaration$() {
        value(null, null, RequirementType.declaration);
        return this;
    }

    public RequirementBuilder<P> declaration$(String value) {
        value(value, null, RequirementType.declaration);
        return this;
    }

    public P end() {
        return parent;
    }

    public Requirement build() {
        return requirement;
    }
}
