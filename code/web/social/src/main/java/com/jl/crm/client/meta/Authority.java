package com.jl.crm.client.meta;

public class Authority {
    private String authority ;

      Authority() {
    }

    public Authority(String authority) {

        this.authority = authority;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "authority='" + authority + '\'' +
                '}';
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
