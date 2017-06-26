package com.upgrade.mapper.orika.domain.person;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by matthewgale on 6/26/17.
 */
public class SimpleMapperTest {
    private static MapperFactory mapperFactory;

    @BeforeClass
    public static void instantiateMapper() {
        mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(PersonEntity.class, PersonBean.class)
                .byDefault()
                .register();
    }

    @Test
    public void testSimpleMap() {
        PersonEntity entity = buildEntity();
        PersonBean bean = new PersonBean();

        MapperFacade facade = mapperFactory.getMapperFacade();
        // you can control the instatiation of the destination object. You can create an instance and pass it yourself
        // or let the mapper perform this for you. For complex objects there are more sophisticated means of object construction
        // but the need seems rare (e.g. acquisition as initialization or something)
        facade.map(entity, bean);
        assertEquals(entity, bean);

        //composite fields cant be mapped w/o a specific rule/hint
        assertThat(entity.getFirstname()).isNotNull();
        assertThat(entity.getLastname()).isNotNull();
        assertThat(bean.getFullName()).isNull();

        //name cant be inferred
        assertThat(entity.getCivicLocation()).isNotNull();
        assertThat(bean.getAddress()).isNull();

        // letting the mapper init the destination object
        bean = facade.map(entity, PersonBean.class);

        assertThat(entity.getFirstname()).isNotNull();
        assertThat(entity.getLastname()).isNotNull();
        assertThat(bean.getFullName()).isNull();

        assertThat(entity.getCivicLocation()).isNotNull();
        assertThat(bean.getAddress()).isNull();
    }

    @Test
    public void testSimpleMapReverse() {
        PersonEntity entity = new PersonEntity();
        PersonBean bean = buildBean();

        MapperFacade facade = mapperFactory.getMapperFacade();
        facade.map(bean, entity);
        assertEquals(entity, bean);

        // NOTE: the mapping, DOES NOT FAIL EXPLICITLY if the fields are null- by using `byDefault` orika does its best
        // to identify the correct candidate fields on the destination side. If it can't find one, it doesn't die- it
        // just cannot perform its mapping. The only time orika will die during mapping if you give a specific field to
        // map to, but that field does not exist.

        //composite fields cant be mapped w/o a specific rule/hint
        assertThat(entity.getFirstname()).isNull();
        assertThat(entity.getLastname()).isNull();
        assertThat(bean.getFullName()).isNotNull();

        //name cant be inferred
        assertThat(entity.getCivicLocation()).isNull();
        assertThat(bean.getAddress()).isNotNull();

        // letting the mapper init the destination object
        facade.map(entity, PersonBean.class);

        assertThat(entity.getFirstname()).isNull();
        assertThat(entity.getLastname()).isNull();
        assertThat(bean.getFullName()).isNotNull();

        assertThat(entity.getCivicLocation()).isNull();
        assertThat(bean.getAddress()).isNotNull();
    }

    private PersonEntity buildEntity() {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setAge(29);
        personEntity.setFirstname("Matt");
        personEntity.setLastname("Gale");
        personEntity.setHairColor(HairColor.BLACK);
        personEntity.setCivicLocation("3767 Bright Street");
        return personEntity;
    }

    private PersonBean buildBean() {
        PersonBean personBean = new PersonBean();
        personBean.setAge(29);
        personBean.setFullName("Matt Gale");
        personBean.setHairColor(HairColor.BLACK);
        personBean.setAddress("3767 Bright Street");
        return personBean;
    }

    private void assertEquals(PersonEntity personEntity, PersonBean personBean) {
        assertThat(personEntity.getAge()).isEqualTo(personBean.getAge());
        assertThat(personEntity.getHairColor()).isEqualTo(personBean.getHairColor());
    }
}
