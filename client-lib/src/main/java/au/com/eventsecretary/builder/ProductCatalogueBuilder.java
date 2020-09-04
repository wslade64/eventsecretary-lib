package au.com.eventsecretary.builder;

import au.com.auspost.simm.model.ComplexType;
import au.com.eventsecretary.accounting.product.Product;
import au.com.eventsecretary.accounting.product.ProductBase;
import au.com.eventsecretary.accounting.product.ProductCatalogue;
import au.com.eventsecretary.accounting.product.ProductCatalogueImpl;
import au.com.eventsecretary.accounting.product.ProductDelivery;
import au.com.eventsecretary.accounting.product.ProductDeliveryImpl;
import au.com.eventsecretary.accounting.product.ProductFamily;
import au.com.eventsecretary.accounting.product.ProductFamilyImpl;
import au.com.eventsecretary.accounting.product.ProductImpl;
import au.com.eventsecretary.simm.ModelExtensions;
import au.com.eventsecretary.simm.ModelExtensionsImpl;
import au.com.eventsecretary.vendor.Structure;

import java.math.BigDecimal;

import static au.com.eventsecretary.builder.PricingBuilder.fixedPricing;
import static au.com.eventsecretary.builder.PricingBuilder.unitPricing;
import static au.com.eventsecretary.simm.IdentifiableUtils.id;
/**
 * TODO
 *
 * @author Warwick Slade
 */
public class ProductCatalogueBuilder {
    private final ProductCatalogue productCatalogue;
    private Structure structure;

    public static ProductCatalogueBuilder productBuilder(String code) {
        return new ProductCatalogueBuilder(code);
    }

    private ProductCatalogueBuilder(String code) {
        this.productCatalogue = new ProductCatalogueImpl();
        this.productCatalogue.setId(id());
        this.productCatalogue.setCode(code);
    }

    public ProductCatalogueBuilder name(String name) {
        this.productCatalogue.setName(name);
        return this;
    }

    public ProductCatalogueBuilder express(BigDecimal cost) {
        ProductDelivery productDelivery = new ProductDeliveryImpl();
        productDelivery.setId(id());
        productDelivery.setCode("exp");
        productDelivery.setName("Express Post");
        productCatalogue.getDeliveryOptions().add(productDelivery);
        productDelivery.setPricingSchedule(fixedPricing(productDelivery.getId(), cost));

        return this;
    }

    public ProductCatalogueBuilder standard(BigDecimal cost) {
        ProductDelivery productDelivery = new ProductDeliveryImpl();
        productDelivery.setId(id());
        productDelivery.setCode("std");
        productDelivery.setName("Standard Post");
        productCatalogue.getDeliveryOptions().add(productDelivery);
        productDelivery.setPricingSchedule(fixedPricing(productDelivery.getId(), cost));
        return this;
    }

    public ProductFamilyBuilder family(String code) {
        return new ProductFamilyBuilder(code);
    }

    public ProductCatalogue build() {
        return productCatalogue;
    }

    public class ProductBaseBuilder<T extends ProductBase, B> {
        protected final T base;
        private B builder;

        protected ProductBaseBuilder(T base, String code) {
            this.base = base;
            this.base.setCode(code);
            this.base.setId(id());
        }

        protected void builder(B builder) {
            this.builder = builder;
        }

        public B name(String name) {
            this.base.setName(name);
            return builder;
        }

        public B note(String name) {
            this.base.setNote(name);
            return builder;
        }

        public B tag(String tagName) {
            base.getTags().add(tagId(tagName));
            return builder;
        }

        public B customField(String customField) {
            ComplexType complexType = productCatalogue.getModelExtensions().getComplexTypeExtensions().stream().filter(aComplexType -> customField.equals(aComplexType.getName())).findFirst().get();
            base.getCustomFieldIds().add(complexType.getId());
            return builder;
        }
    }

    public class ProductFamilyBuilder extends ProductBaseBuilder<ProductFamily, ProductFamilyBuilder> {
        public ProductFamilyBuilder(String code) {
            super(new ProductFamilyImpl(), code);
            builder(this);
            productCatalogue.getProductFamilies().add(base);
        }

        public ProductCatalogueBuilder end() {
            return ProductCatalogueBuilder.this;
        }

        public ProductBuilder product(String code) {
            ProductBuilder productBuilder = new ProductBuilder(code);
            base.getProducts().add(productBuilder.base);
            return productBuilder;
        }

        public class ProductBuilder extends ProductBaseBuilder<Product, ProductBuilder> {

            ProductBuilder(String code) {
                super(new ProductImpl(), code);
                builder(this);
            }

            public ProductBuilder fixedPrice(BigDecimal cost) {
                base.setPricingSchedule(fixedPricing(base.getId(), cost));
                return this;
            }

            public ProductBuilder unitPrice(BigDecimal cost) {
                base.setQuantity(true);
                base.setPricingSchedule(unitPricing(base.getId(), cost));
                return this;
            }

            public ProductFamilyBuilder end() {
                return ProductFamilyBuilder.this;
            }
        }
    }

    public ComplexTypeBuilder<ProductCatalogueBuilder> productExtension() {
        ModelExtensions modelExtensions = this.productCatalogue.getModelExtensions();
        if (modelExtensions == null) {
            modelExtensions = new ModelExtensionsImpl();
            this.productCatalogue.setModelExtensions(modelExtensions);
        }
        return new ComplexTypeBuilder("0BAFA766-7877-48ae-8378-D363E3708841", modelExtensions, this);
    }

    public ComplexTypeBuilder<ProductCatalogueBuilder> productFamilyExtension() {
        ModelExtensions modelExtensions = this.productCatalogue.getModelExtensions();
        if (modelExtensions == null) {
            modelExtensions = new ModelExtensionsImpl();
            this.productCatalogue.setModelExtensions(modelExtensions);
        }
        return new ComplexTypeBuilder("2E62DB99-892E-43ac-AB6F-5AE6C2852974", modelExtensions, this);
    }

    public ProductCatalogueBuilder tags(Structure structure) {
        this.structure = structure;
        return this;
    }

    public String tagId(String tagName) {
        return tagId(this.structure, tagName);
    }

    private String tagId(Structure structure, String tagName) {
        if (tagName.equals(structure.getName())) {
            return structure.getId();
        }
        for (Structure child : structure.getChildren()) {
            String id = tagId(child, tagName);
            if (id != null) {
                return id;
            }
        }
        return null;
    }
}
