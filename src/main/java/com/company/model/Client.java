package com.company.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "listofclients")
public class Client implements Serializable {

    private static final long serialVersionUID = 4910225916550731446L;

    private Long id;
    private String firstName;
    private String lastName;
    private String designation;
    private Integer salary;

    public Client() {
    }

    public Client(Long id) {
        this.id = id;
    }

    public Client(Long id, String firstname, String lastname, String designation, Integer salary) {
        this.id = id;
        this.firstName = firstname;
        this.lastName = lastname;
        this.designation = designation;
        this.salary = salary;
    }

    public Client(String firstname, String lastname, String designation, Integer salary) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.designation = designation;
        this.salary = salary;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "firstName", length = 50)
    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "lastName", length = 50)
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "designation", length = 50)
    public String getDesignation() {
        return this.designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Column(name = "salary")
    public Integer getSalary() {
        return this.salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Id: ").append(this.id).append(", firstName: ").append(this.firstName).append(", lastName: ")
                .append(this.lastName).append(", Designation: ").append(this.designation).append(", Salary: ")
                .append(this.salary);
        return sb.toString();
    }

}
