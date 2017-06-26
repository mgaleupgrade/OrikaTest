package com.upgrade.mapper.orika.domain.person;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by matthewgale on 6/26/17.
 */
public class NonDefaultMapperTest {
    private static MapperFactory mapperFactory;

    @BeforeClass
    public static void instantiateMapper() {
        mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(PersonEntity.class, PersonBean.class)
                .field("civicLocation", "address")
                .byDefault()
                // address is a non-standard field so more sophisticated mapping is needed. Note different mappers are
                // needed for each direction given the complexity.
                .customize(
                    new CustomMapper<PersonEntity, PersonBean>() {
                        public void mapAtoB(PersonEntity personEntity, PersonBean personBean, MappingContext context) {
                            personBean.setFullName(personEntity.getFirstname() + " " + personEntity.getLastname());
                        }

                        public void mapBtoA(PersonBean personBean, PersonEntity personEntity, MappingContext context) {
                            personEntity.setFirstname(personBean.getFullName().split(" ")[0]);
                            personEntity.setLastname(personBean.getFullName().split(" ")[1]);
                        }
                    }
                )
                .register();
    }

    @Test
    public void testSimpleMap() {
        PersonEntity entity = buildEntity();
        PersonBean bean = new PersonBean();

        MapperFacade facade = mapperFactory.getMapperFacade();
        facade.map(entity, bean);
        assertEquals(entity, bean);
    }

    @Test
    public void testSimpleMapReverse() {
        PersonEntity entity = new PersonEntity();
        PersonBean bean = buildBean();

        MapperFacade facade = mapperFactory.getMapperFacade();
        facade.map(bean, entity);
        assertEquals(entity, bean);
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
        assertThat(personEntity.getCivicLocation()).isEqualTo(personBean.getAddress());
        assertThat(personEntity.getFirstname()).isEqualTo(personBean.getFullName().split(" ")[0]);
        assertThat(personEntity.getLastname()).isEqualTo(personBean.getFullName().split(" ")[1]);
    }
}
