package com.upgrade.mapper.orika.domain.person;

import com.upgrade.mapper.orika.domain.person.pet.PetEntity;
import lombok.Data;

/**
 * Created by matthewgale on 6/26/17.
 */
@Data
public class PetOwnerEntity extends PersonEntity {
    private PetEntity pet;
}
