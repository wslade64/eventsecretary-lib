<#-- @ftlvariable name="buyer" type="java.lang.String" -->
<#-- @ftlvariable name="seller" type="java.lang.String" -->
<#-- @ftlvariable name="paymentSummary" type="java.util.List<String[]>" -->
<#-- @ftlvariable name="DateUtility" type="au.com.eventsecretary.simm.DateUtility" -->
<H3 style="border-top: 1px solid black;margin-top:16px;padding-top: 8px;">Payment summary</H3>
<table border="1" cellspacing="0" cellpadding="8px" style="width:100%; border-collapse: collapse;">
<#list paymentSummary as payment>
    <tr><td>${payment[0]}</td><td>${payment[1]}</td></tr>
</#list>
</table>
<p style="font: 11px Arial,Helvetica,sans-serif;">Event Secretary Pty Ltd (ACN 169 441 230) provides an online platform that enables the buyer (${buyer}) to purchase goods and services from the seller (${seller}). The payment summary is accurate as of <b>${DateUtility.smartTimestamp(DateUtility.now())}</b>. In the future the buyer may add items or be refunded for items. Event Secretary Pty Ltd does not issue Tax Invoices. A Tax Invoice may only be issued by the seller.</p>
