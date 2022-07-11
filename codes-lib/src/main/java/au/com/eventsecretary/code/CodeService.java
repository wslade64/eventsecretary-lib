package au.com.eventsecretary.code;

import au.com.eventsecretary.ResourceExistsException;
import au.com.eventsecretary.ResourceNotFoundException;
import au.com.eventsecretary.common.codes.Code;
import au.com.eventsecretary.common.codes.CodeImpl;
import au.com.eventsecretary.persistence.BusinessObjectPersistence;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static au.com.eventsecretary.simm.IdentifiableUtils.id;

/**
 * TODO
 *
 * @author Warwick Slade
 */
@Component
public class CodeService {
    private final BusinessObjectPersistence persistence;

    public CodeService(BusinessObjectPersistence persistence) {
        this.persistence = persistence;
    }

    public List<Code> findCodesBySetId(String codeSetId) {
        Code code = new CodeImpl();
        code.setSetId(codeSetId);
        return persistence.findObjects(code);
    }

    public Code findCodeByCode(String codeSetId, String codeCode) {
        Code code = new CodeImpl();
        code.setSetId(codeSetId);
        code.setCode(codeCode);
        return persistence.findObject(code);
    }

    public List<Code> createCodes(List<Code> codes) {
        for (Code code : codes) {
            Code codeByCode = findCodeByCode(code.getSetId(), code.getCode());
            if (codeByCode != null) {
                throw new ResourceExistsException(code.getCode());
            }
        }
        for (Code code : codes) {
            if (code.getId() == null) {
                code.setId(id());
            }
            persistence.storeObject(code);
        }
        return codes;
    }

    public Code createCode(Code code) {
        Code codeByCode = findCodeByCode(code.getSetId(), code.getCode());
        if (codeByCode != null) {
            throw new ResourceExistsException(code.getCode());
        }

        if (code.getId() == null) {
            code.setId(id());
        }
        persistence.storeObject(code);
        return code;
    }

    public Code updateCode(Code code) {
        Code existingCode = findCodeById(code.getId());
        if (!StringUtils.equals(code.getCode(), existingCode.getCode())) {
            Code codeByCode = findCodeByCode(code.getSetId(), code.getCode());
            if (codeByCode != null) {
                throw new ResourceExistsException(code.getCode());
            }
        }
        persistence.storeObject(code);
        return code;
    }

    public Code findCodeById(String codeId) {
        Code search = new CodeImpl();
        search.setId(codeId);
        Code result = persistence.findObject(search);
        if (result == null) {
            throw new ResourceNotFoundException(codeId);
        }
        return result;
    }
}
