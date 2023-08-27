package au.com.eventsecretary.client;

import au.com.eventsecretary.accounting.pricing.Cost;
import au.com.eventsecretary.accounting.pricing.CostAmount;
import au.com.eventsecretary.accounting.pricing.CostAmountImpl;
import au.com.eventsecretary.accounting.pricing.CostImpl;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * TODO
 *
 * @author Warwick Slade
 */
public class OrderClientTest {

    private static Cost createNamedCostWithAmount(String name, String... amounts) {
        Cost cost = createCostWithAmount(amounts);
        cost.setName(name);
        return cost;
    }

    private static Cost createCostWithAmount(String... amounts) {
        Cost cost = new CostImpl();
        for (String amount : amounts) {
            CostAmount costAmount;
            cost.getAmount().add(costAmount = new CostAmountImpl());
            costAmount.setAmount(new BigDecimal(amount));
        }
        return cost;
    }

    @Test
    public void sumAmount() {
        assertThat(OrderClient.sumAmount(createCostWithAmount("1")).compareTo(new BigDecimal("1")) == 0, is(true));
        assertThat(OrderClient.sumAmount(createCostWithAmount("1", "1.9")).compareTo(new BigDecimal("2.9")) == 0, is(true));
    }

    @Test
    public void flattenCost() {
        Cost horseCost = new CostImpl();
        horseCost.setName("Horse 1");
        horseCost.getCosts().add(createNamedCostWithAmount("Item1", "10"));
        horseCost.getCosts().add(createNamedCostWithAmount("Item2", "1000"));

        List<String[]> strings = OrderClient.flattenCost(horseCost);
        assertThat(strings.size(), is(2));
        assertThat(strings.get(0)[0], is("Horse 1 - Item1"));
        assertThat(strings.get(0)[1], is("$10"));
        assertThat(strings.get(1)[0], is("Horse 1 - Item2"));
        assertThat(strings.get(1)[1], is("$1,000"));

        Cost riderCost = new CostImpl();
        riderCost.setName("Rider 1");
        riderCost.getCosts().add(createNamedCostWithAmount("Itema", "11.99"));
        riderCost.getCosts().add(horseCost);
        riderCost.getCosts().add(createNamedCostWithAmount("Itemb", "12.99"));

        strings = OrderClient.flattenCost(riderCost);
        assertThat(strings.size(), is(4));
        assertThat(strings.get(0)[0], is("Rider 1 - Itema"));
        assertThat(strings.get(0)[1], is("$11.99"));

        assertThat(strings.get(1)[0], is("Rider 1 - Itemb"));
        assertThat(strings.get(1)[1], is("$12.99"));

        assertThat(strings.get(2)[0], is("Horse 1 - Item1"));
        assertThat(strings.get(2)[1], is("$10"));

        assertThat(strings.get(3)[0], is("Horse 1 - Item2"));
        assertThat(strings.get(3)[1], is("$1,000"));

        Cost orderCost = new CostImpl();
        orderCost.getCosts().add(riderCost);

        strings = OrderClient.flattenCost(riderCost);
        assertThat(strings.size(), is(4));
        assertThat(strings.get(0)[0], is("Rider 1 - Itema"));
        assertThat(strings.get(0)[1], is("$11.99"));

        assertThat(strings.get(1)[0], is("Rider 1 - Itemb"));
        assertThat(strings.get(1)[1], is("$12.99"));

        assertThat(strings.get(2)[0], is("Horse 1 - Item1"));
        assertThat(strings.get(2)[1], is("$10"));

        assertThat(strings.get(3)[0], is("Horse 1 - Item2"));
        assertThat(strings.get(3)[1], is("$1,000"));
    }
}