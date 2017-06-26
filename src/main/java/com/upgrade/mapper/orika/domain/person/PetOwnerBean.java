package com.upgrade.mapper.orika.domain.person;

import com.upgrade.mapper.orika.domain.person.pet.PetBean;
import lombok.Data;

/**
 * Created by matthewgale on 6/26/17.
 */
@Data
public class PetOwnerBean extends PersonBean {
    private PetBean pet;
}
