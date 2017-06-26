package com.upgrade.mapper.orika.domain.person.pet;

import lombok.Data;

/**
 * Created by matthewgale on 6/26/17.
 */
@Data
public class PetBean {
    private String name;
    private String sound;
    private int legs;
    private CollarBean collarBean;
}
