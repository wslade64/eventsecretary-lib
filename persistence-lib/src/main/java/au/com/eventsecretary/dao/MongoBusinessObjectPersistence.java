package au.com.eventsecretary.dao;

import au.com.eventsecretary.persistence.BusinessObjectPersistence;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Store a Business Object in a Mongo database
 *
 * @author Warwick Slade
 */
@Component
public class MongoBusinessObjectPersistence implements BusinessObjectPersistence
{
    private MongoTemplate mongoOperation;

    public MongoBusinessObjectPersistence(MongoTemplate mongoOperation) {
        this.mongoOperation = mongoOperation;
    }

    @Override
    public <T> void storeObject(T object)
    {
        mongoOperation.save(object);
    }

    @Override
    public <T> T findObject(FindBy<T> search) {
        Criteria criteria = makeCriteria(search);
        Query query = criteria != null ? new Query(criteria) : new Query();

        Class<T> targetClass = (Class<T>) search.getTarget().getClass();

        return mongoOperation.findOne(query, targetClass);
    }

    @Override
    public <T> T findObject(T search)
    {
        Criteria criteria = makeCriteria(search);
        Query query = criteria != null ? new Query(criteria) : new Query();

        Class<T> targetClass = targetClass(search);

        return mongoOperation.findOne(query, targetClass);
    }

    public <T> List<T> findObjects(FindBy<T> search)
    {
        Criteria criteria = makeCriteria(search);
        Query query = new Query();
        if (criteria != null)
        {
            query.addCriteria(criteria);
        }
        if (search instanceof FindBy) {
            Integer limit = ((FindBy)search).getLimit();
            if (limit != null) {
                query.limit(limit);
            }
        }

        Class<T> targetClass = (Class<T>) search.getTarget().getClass();

        return mongoOperation.find(query, targetClass);
    }

    @Override
    public <T> List<T> findObjects(T search)
    {
        Criteria criteria = makeCriteria(search);
        Query query = new Query();
        if (criteria != null)
        {
            query.addCriteria(criteria);
        }
        if (search instanceof FindBy) {
            Integer limit = ((FindBy)search).getLimit();
            if (limit != null) {
                query.limit(limit);
            }
        }

        Class<T> targetClass = targetClass(search);

        return mongoOperation.find(query, targetClass);
    }

    @Override
    public <T> void deleteObject(T object)
    {
        if (object instanceof FindByCriteria)
        {
            Criteria criteria = ((FindByCriteria<T, Criteria>) object).getCriteria();
            mongoOperation.findAllAndRemove(new Query(criteria), ((FindByCriteria<T, Criteria>) object).getTarget().getClass());
            return;
        }

        Object id;
        try
        {
            id = PropertyUtils.getProperty(object, "id");
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Cant obtain primary key", e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException("Cant obtain primary key", e);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Cant obtain primary key", e);
        }
        if (id == null)
        {
            mongoOperation.dropCollection(object.getClass());
            return;
        }
        Query query = new Query(Criteria.where("id").is(id));

        mongoOperation.findAllAndRemove(query, object.getClass());
    }

    protected <T, C> Class<T> targetClass(T search)
    {
        Object target;
        if (search instanceof FindByOr)
        {
            target = ((FindByOr<T>)search).getTarget();
        }
        else if (search instanceof FindByCriteria)
        {
            target = ((FindByCriteria<T, C>)search).getTarget();
        }
        else
        {
            target = search;
        }
        return (Class<T>)target.getClass();
    }

    protected <T> Criteria makeCriteria(T search)
    {
        Object target;
        if (search instanceof FindByCriteria)
        {
           return ((FindByCriteria<T, Criteria>)search).getCriteria();
        }
        if (search instanceof FindByOr)
        {
            target = ((FindByOr<T>)search).getTarget();
        }
        else
        {
            target = search;
        }
        List<Criteria> criterias = new ArrayList<Criteria>();

        makeCriteria(criterias, target, "");
        if (criterias.size() == 0)
        {
            return null;
        }
        if (criterias.size() == 1)
        {
            return criterias.get(0);
        }
        if (search instanceof FindByOr)
            return new Criteria().orOperator(criterias.toArray(new Criteria[]{}));
        else
            return new Criteria().andOperator(criterias.toArray(new Criteria[]{}));
    }

    private void makeCriteria(List<Criteria> criterias, Object target, String prefix)
    {
        BeanWrapper wrapper = new BeanWrapperImpl(target);
        for (PropertyDescriptor propertyDescriptor : wrapper.getPropertyDescriptors())
        {
            if (propertyDescriptor.getWriteMethod() == null)
            {
                continue;
            }
            Object value = wrapper.getPropertyValue(propertyDescriptor.getName());
            if (value != null)
            {
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                if (propertyType.isPrimitive() && propertyType.equals(boolean.class))
                {
                    if (((Boolean)value) == false)
                    {
                        continue;
                    }
                }
                if (propertyType.isPrimitive() && propertyType.equals(long.class))
                {
                    if (((Long)value) == 0)
                    {
                        continue;
                    }
                }
                if (propertyType.isPrimitive() && propertyType.equals(int.class))
                {
                    if (((Integer)value) == 0)
                    {
                        continue;
                    }
                }
                if (propertyType.equals(String.class))
                {
                    String svalue = (String)value;
                    if (svalue.length() > 1 && svalue.startsWith("~") && svalue.endsWith("~"))
                    {
                        String strip = svalue.substring(1, svalue.length() - 1);
                        criterias.add(Criteria.where(prefix + propertyDescriptor.getName()).regex("^" + strip + "$", "i"));
                        continue;
                    }
                    if (svalue.length() > 1 && svalue.startsWith("`") && svalue.endsWith("`"))
                    {
                        String strip = svalue.substring(1, svalue.length() - 1);
                        criterias.add(Criteria.where(prefix + propertyDescriptor.getName()).regex(strip, "i"));
                        continue;
                    }
                }

                if (ClassUtils.isPrimitiveOrWrapper(value.getClass()) || value instanceof  String || value instanceof Date || value instanceof Enum)
                {
                    criterias.add(Criteria.where(prefix + propertyDescriptor.getName()).is(value));
                }
                else
                {
                    makeCriteria(criterias, value, prefix + propertyDescriptor.getName() + ".");
                }
            }
        }

    }
}
