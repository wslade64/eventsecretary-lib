package au.com.eventsecretary.builder;

import au.com.eventsecretary.accounting.registration.Conditions;

import static au.com.eventsecretary.CollectionUtility.addArrayToList;
import static au.com.eventsecretary.simm.IdentifiableUtils.id;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class ConditionsBuilder<T> {
    private final Conditions conditions;
    private final T outerBuilder;

    public ConditionsBuilder(String code, Conditions conditions, T outerBuilder) {
        this.conditions = conditions;
        this.conditions.setId(id());
        this.conditions.setCode(code);
        this.outerBuilder = outerBuilder;
    }

    public T end() {
        return outerBuilder;
    }

    public ConditionsBuilder<T> conditions(String... conditions) {
        addArrayToList(this.conditions, Conditions::getConditions, conditions);
        return this;
    }

    public ConditionsBuilder<T> preamble(String... preamble) {
        addArrayToList(this.conditions, Conditions::getPreamble, preamble);
        return this;
    }
}
