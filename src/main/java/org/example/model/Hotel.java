package org.example.model;

import javax.persistence.*;

@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "class")
    private Integer hotelClass;

    @Column(name = "room_category")
    private String roomCategory;

    public Hotel() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHotelClass() {
        return hotelClass;
    }

    public void setHotelClass(Integer hotelClass) {
        this.hotelClass = hotelClass;
    }

    public String getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(String roomCategory) {
        this.roomCategory = roomCategory;
    }

    // Другие поля, геттеры и сеттеры

    // Конструкторы
}
