package com.zerobase.store.store.domain.model;

import com.zerobase.store.store.domain.store.EditStore;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "STORE")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partnerId;

    private String storeName;
    private String address;
    private String description;

    private double lat;
    private double lnt;
    private double distance;

    private double rating;
    private Long ratingCount;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public void edit(EditStore.Request request){
        if(StringUtils.hasText(request.getStoreName())){
            this.storeName = request.getStoreName();
        }
        if(StringUtils.hasText(request.getAddress())){
            this.address = request.getAddress();
        }
        if(StringUtils.hasText(request.getDescription())){
            this.description = request.getDescription();
        }
        if(request.getLat() != 0 && request.getLnt() != 0){
            this.lat = request.getLat();
            this.lnt = request.getLnt();
        }
        this.updateAt = LocalDateTime.now();
    }
}
