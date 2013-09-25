/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.storage.entities;

import org.osiam.resources.scim.Address;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Address Entity
 */
@Entity(name = "scim_address")
public class AddressEntity implements Serializable {

    private static final long serialVersionUID = -6535056565939057058L;

    @Id
    @GeneratedValue
    private long id;

    
    @Column
    @Enumerated(EnumType.STRING)
    private CanonicalAddressTypes type;

    
    @Column
    private String formatted;

    
    @Column
    private String streetAddress;

    
    @Column
    private String locality;

    
    @Column
    private String region;

    
    @Column
    private String postalCode;

    
    @Column
    private String country;

    
    @Column(name = "postgresql_does_not_like_primary")
    private Boolean primary;

    @ManyToOne
    private UserEntity user;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getType() {
        if (type != null) {
            return type.toString();
        }
        return null;
    }

    public void setType(String type) {
        if (type != null) {
            this.type = CanonicalAddressTypes.valueOf(type);
        }
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Address toScim() {
        return new Address.Builder().
                setCountry(getCountry()).
                setFormatted(getFormatted()).
                setLocality(getLocality()).
                setPostalCode(String.valueOf(getPostalCode())).
                setRegion(getRegion()).
                setStreetAddress(getStreetAddress()).
                build();
    }

    public static AddressEntity fromScim(Address address) {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCountry(address.getCountry());
        addressEntity.setFormatted(address.getFormatted());
        addressEntity.setLocality(address.getLocality());
        addressEntity.setPostalCode(address.getPostalCode());
        addressEntity.setPrimary((address.isPrimary() == null ? false : address.isPrimary()));
        addressEntity.setRegion(address.getRegion());
        addressEntity.setStreetAddress(address.getStreetAddress());
        addressEntity.setType(address.getType());
        return addressEntity;
    }

    private enum CanonicalAddressTypes {
        work, home, other
    }
}