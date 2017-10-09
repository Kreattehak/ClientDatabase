package com.company.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "listofclients")
public class Client extends BaseEntity implements Serializable {

    @Column(name = "firstName", length = 50)
    @Length(min = 3, message = "{validation.minLength}")
    private String firstName;

    @Column(name = "lastName", length = 50)
    @Length(min = 3, message = "{validation.minLength}")
    private String lastName;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Address> address;

    @Column(name = "dateOfRegistration", nullable = false, updatable = false)
    private Date dateOfRegistration;

    @OneToOne
    @JoinTable(name = "client_main_address", joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private Address mainAddress;

    public Client() {
        this.address = new HashSet<>();
        this.dateOfRegistration = Date.valueOf(LocalDate.now());
    }

    public Client(String firstName, String lastName) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Address> getAddress() {
        return address;
    }

    public void setAddress(Set<Address> adress) {
        this.address = adress;
    }

    public void addAddress(Address address) {
        if (this.address.isEmpty()) {
            this.mainAddress = address;
        }
        this.address.add(address);
    }

    public boolean removeAddress(Address address) {
        return this.address.remove(address);
    }

    public Date getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(Date dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public Address getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(Address mainAddress) {
        this.mainAddress = mainAddress;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + super.getId()+
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                ", dateOfRegistration=" + dateOfRegistration +
                ", mainAddress=" + mainAddress +
                '}';
    }
}
