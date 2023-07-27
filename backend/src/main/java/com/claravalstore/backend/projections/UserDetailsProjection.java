package com.claravalstore.backend.projections;

public interface UserDetailsProjection {
    String getUserName();
    String getPassword();
    Long getPrivilegeId();
    String getAuthority();
}
