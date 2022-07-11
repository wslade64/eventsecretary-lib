package au.com.eventsecretary.code;

import au.com.eventsecretary.ValidationException;
import au.com.eventsecretary.apps.AbstractController;
import au.com.eventsecretary.common.codes.Code;
import au.com.eventsecretary.common.codes.CodeContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TODO
 *
 * @author Warwick Slade
 */
@RestController
@RequestMapping(value = "/v1/code")
public class RestCodeController extends AbstractController
{
    private final CodeService codeService;
    private final List<CodeContext> contexts;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public RestCodeController(CodeService codeService, List<CodeContext> contexts) {
        this.codeService = codeService;
        this.contexts = contexts;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/context")
    @ResponseBody
    public List<CodeContext> getContexts() {
        return contexts;
    }

    @RequestMapping(method = RequestMethod.GET, params = "setId")
    @ResponseBody
    public List<Code> getCodesBySetId(@RequestParam String setId) {
        return codeService.findCodesBySetId(setId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{codeId}")
    @ResponseBody
    public Code getCode(@PathVariable String codeId) {
        return codeService.findCodeById(codeId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Code createCode(@RequestBody Code code) {
        if (StringUtils.isBlank(code.getCode())) {
            throw new ValidationException("missingCode", "The code must have a code");
        }
        return codeService.createCode(code);
    }

    @RequestMapping(method = RequestMethod.POST, params = "multiple")
    @ResponseBody
    public List<Code> createCodes(@RequestBody List<Code> codes) {
        for (Code code : codes) {
            if (StringUtils.isBlank(code.getCode())) {
                throw new ValidationException("missingCode", "The code must have a code");
            }
        }
        return codeService.createCodes(codes);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public Code updateCode(@RequestBody Code code) {
        if (StringUtils.isBlank(code.getId())) {
            throw new ValidationException("missingId", "The code must have an identifier");
        }
        if (StringUtils.isBlank(code.getCode())) {
            throw new ValidationException("missingCode", "The code must have a code");
        }
        return codeService.updateCode(code);
    }
}
