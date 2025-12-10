package com.cloud.inventory.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor
public class WarehouseDto {
    private UUID id;
    private String code;
    private String name;
    private String address;
}