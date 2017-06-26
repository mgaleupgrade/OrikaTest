package com.upgrade.mapper.orika.domain.person;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by matthewgale on 6/26/17.
 */
@Data
@NoArgsConstructor
public class PersonBean {
    private String fullName;
    private int age;
    private HairColor hairColor;
    private String address;
}
