package com.zipjung.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class Result<T> implements Serializable {
    private T data;
    private int count;
}
