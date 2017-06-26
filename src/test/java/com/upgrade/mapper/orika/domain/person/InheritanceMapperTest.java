package com.upgrade.mapper.orika.domain.person;

import com.upgrade.mapper.orika.domain.person.child.ChildBean;
import com.upgrade.mapper.orika.domain.person.child.ChildEntity;
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
public class InheritanceMapperTest {
    private static MapperFactory mapperFactory;

    @BeforeClass
    public static void instantiateMapper() {
        mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(ChildEntity.class, ChildBean.class)
                .field("civicLocation", "address")
                .byDefault()
                // address is a non-standard field so more sophisticated mapping is needed. Note different mappers are
                // needed for each direction given the complexity.
                .customize(
                        new CustomMapper<ChildEntity, ChildBean>() {
                            public void mapAtoB(ChildEntity childEntity, ChildBean childBean, MappingContext context) {
                                childBean.setFullName(childEntity.getFirstname() + " " + childEntity.getLastname());
                            }

                            public void mapBtoA(ChildBean childBean, ChildEntity childEntity, MappingContext context) {
                                childEntity.setFirstname(childBean.getFullName().split(" ")[0]);
                                childEntity.setLastname(childBean.getFullName().split(" ")[1]);
                            }
                        }
                )
                .register();
    }

    @Test
    public void testSimpleMap() {
        ChildEntity entity = buildEntity();
        ChildBean bean = new ChildBean();

        MapperFacade facade = mapperFactory.getMapperFacade();
        facade.map(entity, bean);
        assertEquals(entity, bean);
    }

    @Test
    public void testSimpleMapReverse() {
        ChildEntity entity = new ChildEntity();
        ChildBean bean = buildBean();

        MapperFacade facade = mapperFactory.getMapperFacade();
        facade.map(bean, entity);
        assertEquals(entity, bean);
    }

    private ChildEntity buildEntity() {
        ChildEntity childEntity = new ChildEntity();
        childEntity.setAge(29);
        childEntity.setFirstname("Matt");
        childEntity.setLastname("Gale");
        childEntity.setHairColor(HairColor.BLACK);
        childEntity.setCivicLocation("3767 Bright Street");
        childEntity.setFavourIceCreamFlavour("Strawberry");
        return childEntity;
    }

    private ChildBean buildBean() {
        ChildBean childBean = new ChildBean();
        childBean.setAge(29);
        childBean.setFullName("Matt Gale");
        childBean.setHairColor(HairColor.BLACK);
        childBean.setAddress("3767 Bright Street");
        childBean.setFavourIceCreamFlavour("Strawberry");
        return childBean;
    }

    private void assertEquals(ChildEntity childEntity, ChildBean childBean) {
        assertThat(childEntity.getAge()).isEqualTo(childBean.getAge());
        assertThat(childEntity.getHairColor()).isEqualTo(childBean.getHairColor());
        assertThat(childEntity.getCivicLocation()).isEqualTo(childBean.getAddress());
        assertThat(childEntity.getFirstname()).isEqualTo(childBean.getFullName().split(" ")[0]);
        assertThat(childEntity.getLastname()).isEqualTo(childBean.getFullName().split(" ")[1]);
        assertThat(childEntity.getFavourIceCreamFlavour()).isEqualTo(childBean.getFavourIceCreamFlavour());
    }
}
