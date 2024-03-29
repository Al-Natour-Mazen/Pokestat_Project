package fr.univ_poitiers.dptinfo.pokestat.Entities;

import androidx.room.*;

import java.io.Serializable;

@Entity(tableName = "pokemon_table")
public class Pokemon implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "weight")
    private String weight;

    @ColumnInfo(name = "height")
    private String height;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    @ColumnInfo(name = "hp")
    private String hp;

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getHp() {
        return hp;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

}

