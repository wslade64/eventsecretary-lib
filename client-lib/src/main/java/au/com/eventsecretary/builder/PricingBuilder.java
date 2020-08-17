package au.com.eventsecretary.builder;

import au.com.eventsecretary.accounting.pricing.AmountPrice;
import au.com.eventsecretary.accounting.pricing.AmountPriceImpl;
import au.com.eventsecretary.accounting.pricing.Comparator;
import au.com.eventsecretary.accounting.pricing.ConditionalPricing;
import au.com.eventsecretary.accounting.pricing.ConditionalPricingImpl;
import au.com.eventsecretary.accounting.pricing.FixedPrice;
import au.com.eventsecretary.accounting.pricing.FixedPriceImpl;
import au.com.eventsecretary.accounting.pricing.ListPrice;
import au.com.eventsecretary.accounting.pricing.ListPriceImpl;
import au.com.eventsecretary.accounting.pricing.Operator;
import au.com.eventsecretary.accounting.pricing.Price;
import au.com.eventsecretary.accounting.pricing.PricingSchedule;
import au.com.eventsecretary.accounting.pricing.PricingScheduleImpl;
import au.com.eventsecretary.accounting.pricing.Selector;
import au.com.eventsecretary.accounting.pricing.SelectorImpl;
import au.com.eventsecretary.accounting.pricing.UnitPrice;
import au.com.eventsecretary.accounting.pricing.UnitPriceImpl;
import au.com.eventsecretary.accounting.pricing.ValuePrice;
import au.com.eventsecretary.accounting.pricing.ValuePriceImpl;
import au.com.eventsecretary.common.Amount;

import java.math.BigDecimal;

import static au.com.eventsecretary.simm.IdentifiableUtils.id;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class PricingBuilder {
    static public PricingSchedule pricingSchedule(String target) {
        PricingSchedule pricingSchedule = new PricingScheduleImpl();
        pricingSchedule.setId(id());
        pricingSchedule.setTarget(target);
        return pricingSchedule;
    }

    static public UnitPrice unitPrice(BigDecimal amount) {
        UnitPrice unitPricing = new UnitPriceImpl();
        unitPricing.setId(id());
        unitPricing.setAmount(amount);
        return unitPricing;
    }

    static public ListPrice listPrice() {
        ListPrice listPrice = new ListPriceImpl();
        listPrice.setId(id());
        return listPrice;
    }

    static public ListPrice listPrice(String[] values, BigDecimal[] amounts) {
        ListPrice listPrice = listPrice();

        for (int i = 0; i < values.length; i++) {
            ValuePrice valuePrice = valuePrice(values[i], amounts[i]);
            listPrice.getItem().add(valuePrice);
        }
        return listPrice;
    }

    static public FixedPrice fixedPrice(BigDecimal amount) {
        FixedPrice fixedPrice = new FixedPriceImpl();
        fixedPrice.setId(id());
        fixedPrice.setAmount(amount);
        return fixedPrice;
    }

    static public ValuePrice valuePrice(String value, BigDecimal amount) {
        ValuePrice valuePrice = new ValuePriceImpl();
        valuePrice.setId(id());
        valuePrice.setValue(value);
        valuePrice.setAmount(amount);
        return valuePrice;
    }

    static public PricingSchedule amountPricing(String target, Amount amount) {
        PricingSchedule pricingSchedule = pricingSchedule(target);
        AmountPrice amountPrice = new AmountPriceImpl();
        amountPrice.setId(id());
        amountPrice.setAmount(amount);
        conditionalPricing(pricingSchedule, amountPrice);
        return pricingSchedule;
    }

    static public PricingSchedule unitPricing(String target, BigDecimal amount) {
        PricingSchedule pricingSchedule = pricingSchedule(target);
        conditionalPricing(pricingSchedule, unitPrice(amount));
        return pricingSchedule;
    }

    static public PricingSchedule fixedPricing(String target, BigDecimal amount) {
        PricingSchedule pricingSchedule = pricingSchedule(target);
        conditionalPricing(pricingSchedule, fixedPrice(amount));
        return pricingSchedule;
    }

    static public ConditionalPricing conditionalPricing(PricingSchedule pricingSchedule, Price price) {
        ConditionalPricing conditionalPricing = new ConditionalPricingImpl();
        conditionalPricing.setId(id());
        conditionalPricing.setPrice(price);
        pricingSchedule.getPricing().add(conditionalPricing);
        return conditionalPricing;
    }

    static public PricingSchedule listPricing(String target, String[] values, BigDecimal ...amounts) {
        PricingSchedule pricingSchedule = pricingSchedule(target);
        ListPrice listPrice = listPrice(values, amounts);
        conditionalPricing(pricingSchedule, listPrice);
        return pricingSchedule;
    }

    static public Selector dateRangeSelector(ConditionalPricing conditionalPricing, int from, int to) {
        Selector selector = new SelectorImpl();
        selector.setSelector("date");
        selector.setValue(Integer.toString(from));
        selector.setComparator(Comparator.gte);
        conditionalPricing.getSelector().add(selector);

        selector = new SelectorImpl();
        selector.setSelector("date");
        selector.setValue(Integer.toString(to));
        selector.setComparator(Comparator.lte);
        conditionalPricing.getSelector().add(selector);

        conditionalPricing.setOperator(Operator.and);
        return selector;
    }

    static public Selector ageSelector(ConditionalPricing conditionalPricing, Comparator comparator, int value) {
        Selector selector = new SelectorImpl();
        selector.setSelector("age");
        selector.setValue(Integer.toString(value));
        selector.setComparator(comparator);
        conditionalPricing.getSelector().add(selector);
        return selector;
    }

    static public Selector optionalSelector(ConditionalPricing conditionalPricing) {
        Selector selector = new SelectorImpl();
        selector.setSelector("optional");
        conditionalPricing.getSelector().add(selector);
        return selector;
    }

}
