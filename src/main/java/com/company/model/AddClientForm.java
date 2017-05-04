package com.company.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalDate;

public class AddClientForm {

    @Length(min = 3, message = "{validation.minLength}")
    private String firstName;

    @Length(min = 3, message = "{validation.minLength}")
    private String lastName;

    @NotNull(message = "{validation.client.notNull}")
    private Date dateOfRegistration;

    @Length(min = 3, message = "{validation.minLength}")
    private String streetName;

    @Length(min = 3, message = "{validation.minLength}")
    private String cityName;

    @Length(min = 3, message = "{validation.minLength}")
    private String zipCode;

    public AddClientForm() {
        this.dateOfRegistration = Date.valueOf(LocalDate.now());
    }

    public AddClientForm(String firstName, String lastName, String streetName, String cityName, String zipCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.streetName = streetName;
        this.cityName = cityName;
        this.zipCode = zipCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(Date dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
