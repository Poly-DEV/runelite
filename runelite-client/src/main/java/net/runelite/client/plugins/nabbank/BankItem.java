package net.runelite.client.plugins.nabbank;

public class BankItem {
    private int id;
    private int quantity;
    
    public BankItem ( int id, int quantity ) {
        this.id = id;
        this.quantity = quantity;
    }
    
    public int getID () {
        return id;
    }
    
    public int getQuantity () {
        return quantity;
    }
}
