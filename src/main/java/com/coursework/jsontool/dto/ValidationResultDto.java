package com.coursework.jsontool.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
public class ValidationResultDto {
    private boolean valid;
    private int errorCount;
    private List<String> errors;
}