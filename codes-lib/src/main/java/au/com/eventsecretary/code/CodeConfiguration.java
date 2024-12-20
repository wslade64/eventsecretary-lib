package au.com.eventsecretary.code;

import au.com.eventsecretary.common.codes.CodeContext;
import au.com.eventsecretary.persistence.BusinessObjectPersistence;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
public class CodeConfiguration {
    @Bean
    public CodeService codeService(BusinessObjectPersistence persistence) {
        return new CodeService(persistence);
    }

    @Bean
    public RestCodeController codesController(CodeService codeService, @Qualifier("code-contexts") List<CodeContext> codeContexts) {
        return new RestCodeController(codeService, codeContexts);
    }
}
