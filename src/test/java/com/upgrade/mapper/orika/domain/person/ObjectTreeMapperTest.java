package com.upgrade.mapper.orika.domain.person;

import com.upgrade.mapper.orika.domain.person.pet.CollarBean;
import com.upgrade.mapper.orika.domain.person.pet.CollarEntity;
import com.upgrade.mapper.orika.domain.person.pet.PetBean;
import com.upgrade.mapper.orika.domain.person.pet.PetEntity;
import ma.glasnost.orika.*;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by matthewgale on 6/26/17.
 */
public class ObjectTreeMapperTest {
    private static MapperFactory mapperFactory;

    @BeforeClass
    public static void instantiateMapper() {
        mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(PetOwnerEntity.class, PetOwnerBean.class)
                .field("civicLocation", "address")
                .byDefault()
                // address is a non-standard field so more sophisticated mapping is needed. Note different mappers are
                // needed for each direction given the complexity.
                .customize(
                        new CustomMapper<PetOwnerEntity, PetOwnerBean>() {
                            public void mapAtoB(PetOwnerEntity PetOwnerEntity, PetOwnerBean PetOwnerBean, MappingContext context) {
                                PetOwnerBean.setFullName(PetOwnerEntity.getFirstname() + " " + PetOwnerEntity.getLastname());
                            }

                            public void mapBtoA(PetOwnerBean PetOwnerBean, PetOwnerEntity PetOwnerEntity, MappingContext context) {
                                PetOwnerEntity.setFirstname(PetOwnerBean.getFullName().split(" ")[0]);
                                PetOwnerEntity.setLastname(PetOwnerBean.getFullName().split(" ")[1]);
                            }
                        }
                )
                .register();

        mapperFactory.classMap(PetEntity.class, PetBean.class)
                // Note that the names need to match EXACTLY or the mapping wont occur
                .field("collarEntity", "collarBean")
                .byDefault()
                .register();
    }

    @Test
    public void testSimpleMap() {
        PetOwnerEntity entity = buildEntity();
        PetOwnerBean bean = new PetOwnerBean();

        BoundMapperFacade<PetOwnerEntity, PetOwnerBean> facade = mapperFactory.getMapperFacade(PetOwnerEntity.class, PetOwnerBean.class);
        facade.map(entity, bean);
        assertPetOwnerObjectTreeEquals(entity, bean);
    }

    @Test
    public void testSimpleMapReverse() {
        PetOwnerEntity entity = new PetOwnerEntity();
        PetOwnerBean bean = buildBean();

        BoundMapperFacade<PetOwnerEntity, PetOwnerBean> facade = mapperFactory.getMapperFacade(PetOwnerEntity.class, PetOwnerBean.class);
        facade.mapReverse(bean, entity);
        assertPetOwnerObjectTreeEquals(entity, bean);
    }

    @Test
    public void testAutoMap() {
        CollarBean collarBean = new CollarBean();
        collarBean.setColor("red");

        // Usage like this should be discouraged- we use a typed language, for debugability we should be using it.
        // This does prove a point though, that if you attempt a map from one object to another without an explicit
        // mapper defined, orika will treat the types you are attempting to map as if they were registered with a
        // `byDefault`- so in this case even though CollarBean and CollarEntity dont have an explicit mapper, orika
        // is still able to perform the conversion because of the field names.
        MapperFacade facade = mapperFactory.getMapperFacade();
        CollarEntity entity = facade.map(collarBean, CollarEntity.class);
        assertThat(entity.getColor()).isEqualTo(collarBean.getColor());
    }

    private PetOwnerEntity buildEntity() {
        PetOwnerEntity petOwnerEntity = new PetOwnerEntity();
        petOwnerEntity.setAge(29);
        petOwnerEntity.setFirstname("Matt");
        petOwnerEntity.setLastname("Gale");
        petOwnerEntity.setHairColor(HairColor.BLACK);
        petOwnerEntity.setCivicLocation("3767 Bright Street");

        PetEntity petEntity = new PetEntity();
        petEntity.setLegs(4);
        petEntity.setName("Rover");
        petEntity.setSound("BARK!");

        CollarEntity collarEntity = new CollarEntity();
        collarEntity.setColor("Yellow");
        petEntity.setCollarEntity(collarEntity);

        petOwnerEntity.setPet(petEntity);
        return petOwnerEntity;
    }

    private PetOwnerBean buildBean() {
        PetOwnerBean petOwnerBean = new PetOwnerBean();
        petOwnerBean.setAge(29);
        petOwnerBean.setFullName("Matt Gale");
        petOwnerBean.setHairColor(HairColor.BLACK);
        petOwnerBean.setAddress("3767 Bright Street");

        PetBean petBean = new PetBean();
        petBean.setLegs(4);
        petBean.setName("Rover");
        petBean.setSound("BARK!");
        petOwnerBean.setPet(petBean);

        CollarBean collarBean = new CollarBean();
        collarBean.setColor("Yellow");
        petBean.setCollarBean(collarBean);
        return petOwnerBean;
    }

    private void assertPersonEquals(PersonEntity personEntity, PersonBean personBean) {
        assertThat(personEntity.getAge()).isEqualTo(personBean.getAge());
        assertThat(personEntity.getHairColor()).isEqualTo(personBean.getHairColor());
        assertThat(personEntity.getCivicLocation()).isEqualTo(personBean.getAddress());
        assertThat(personEntity.getFirstname()).isEqualTo(personBean.getFullName().split(" ")[0]);
        assertThat(personEntity.getLastname()).isEqualTo(personBean.getFullName().split(" ")[1]);
    }

    private void assertPetEquals(PetEntity petEntity, PetBean petBean) {
        assertThat(petEntity.getName()).isEqualTo(petBean.getName());
        assertThat(petEntity.getLegs()).isEqualTo(petBean.getLegs());
        assertThat(petEntity.getSound()).isEqualTo(petBean.getSound());

        assertCollarEquals(petEntity.getCollarEntity(), petBean.getCollarBean());
    }

    private void assertCollarEquals(CollarEntity collarEntity, CollarBean collarBean) {
        assertThat(collarEntity.getColor()).isEqualTo(collarBean.getColor());
    }

    private void assertPetOwnerObjectTreeEquals(PetOwnerEntity petOwnerEntity, PetOwnerBean petOwnerBean) {
        assertPersonEquals(petOwnerEntity, petOwnerBean);
        assertPetEquals(petOwnerEntity.getPet(), petOwnerBean.getPet());
    }
}
