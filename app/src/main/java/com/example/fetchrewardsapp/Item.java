package com.example.fetchrewardsapp;

public class Item {
    private int listId;
    private String name;
    private int type; // 0 for header, 1 for item

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    public Item(int listId, String name, int type) {
        this.listId = listId;
        this.name = name;
        this.type = type;
    }

    public int getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
}
