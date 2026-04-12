package com.salvi.atividadetres;

import java.io.Serializable;

public class AccountDTO implements Serializable {

    public AccountDTO() { };

    public AccountDTO(
            int id,
            String name,
            String email,
            String account,
            String password
    ){
        this.id = id;
        this.name = name;
        this.email = email;
        this.account = account;
        this.password = password;
    }

    public int id;
    public String name;
    public String email;
    public String account;
    public String password;

}
