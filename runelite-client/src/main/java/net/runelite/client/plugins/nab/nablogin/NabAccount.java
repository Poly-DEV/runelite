package net.runelite.client.plugins.nab.nablogin;

import lombok.Data;

@Data
public class NabAccount {
    private String account;
    private String username;
    
    public NabAccount( String account, String username ){
        this.account = account;
        this.username = username;
    }
}
