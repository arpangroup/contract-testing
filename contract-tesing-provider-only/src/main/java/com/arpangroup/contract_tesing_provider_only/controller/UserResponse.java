package com.arpangroup.contract_tesing_provider_only.controller;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private boolean condition;
    private String name;

    // Override equals() and hashCode() for object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponse that = (UserResponse) o;
        return condition == that.condition && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, name);
    }
}
