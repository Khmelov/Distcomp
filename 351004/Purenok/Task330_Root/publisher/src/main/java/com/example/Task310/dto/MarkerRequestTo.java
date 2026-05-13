package com.example.Task310.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                // Генерирует геттеры, сеттеры, toString, equals и hashCode
@NoArgsConstructor   // Пустой конструктор
@AllArgsConstructor  // Конструктор со всеми полями
@Builder             // Позволяет создавать объект через MarkerRequestTo.builder()...
public class MarkerRequestTo {


    
    private Long id; // Поле id нужно для операций обновления (Update)

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    private String name;
}