package xyz.efekurbann.topbalance.objects;

import java.util.UUID;

public class TopPlayer {

    private final String name;
    private final UUID uuid;
    private final double balance;
    private final double bank;

    public TopPlayer(String name, UUID uuid, double balance, double bank) {
        this.balance = balance;
        this.bank = bank;
        this.uuid = uuid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public UUID getUUID() {
        return uuid;
    }

    public double getBank() {
        return bank;
    }
}
