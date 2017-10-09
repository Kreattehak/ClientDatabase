package com.company.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Entity
@Table(name = "addresses")
public class Address extends BaseEntity implements Serializable {

    @ManyToOne
    @JoinTable(name = "client_address", joinColumns = @JoinColumn(name = "address_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id"))
    @JsonIgnore
    private Client client;

    @Column(name = "streetName", length = 50, nullable = false)
    @Length(min = 3, message = "{validation.minLength}")
    private String streetName;

    @Column(name = "cityName", length = 50, nullable = false)
    @Length(min = 3, message = "{validation.minLength}")
    private String cityName;

    @Column(name = "zipCode", length = 6, nullable = false)
    @Pattern(regexp = "\\d{2}-\\d{3}", message = "{validation.zipCodePattern}")
    private String zipCode;

    public Address() {
    }

    public Address(String streetName, String cityName, String zipCode) {
        this.streetName = streetName;
        this.cityName = cityName;
        this.zipCode = zipCode;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    @Override
    public String toString() {
        return "Address{" +
                "id=" + super.getId() +
                ", streetName='" + streetName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
