package org.osiam.auth.login.ldap;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.ldap.ppolicy.PasswordPolicyData;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

public class OsiamLdapUserDetailsImpl implements LdapUserDetails, PasswordPolicyData {

    private static final long serialVersionUID = 2727607713572825412L;

    private String id;
    private String dn;
    private String username;
    private Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    // PPolicy data
    private int timeBeforeExpiration = Integer.MAX_VALUE;
    private int graceLoginsRemaining = Integer.MAX_VALUE;

    public OsiamLdapUserDetailsImpl(LdapUserDetailsImpl userDetails) {
        this.dn = userDetails.getDn();
        this.username = userDetails.getUsername();
        this.authorities = userDetails.getAuthorities();
        this.accountNonExpired = userDetails.isAccountNonExpired();
        this.accountNonLocked = userDetails.isAccountNonLocked();
        this.credentialsNonExpired = userDetails.isCredentialsNonExpired();
        this.enabled = userDetails.isEnabled();
        this.timeBeforeExpiration = userDetails.getTimeBeforeExpiration();
        this.graceLoginsRemaining = userDetails.getGraceLoginsRemaining();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getDn() {
        return dn;
    }

    @Override
    public int getTimeBeforeExpiration() {
        return timeBeforeExpiration;
    }

    @Override
    public int getGraceLoginsRemaining() {
        return graceLoginsRemaining;
    }
}