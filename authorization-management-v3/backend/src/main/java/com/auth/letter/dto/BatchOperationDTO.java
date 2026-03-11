package com.auth.letter.dto;

import lombok.Data;

import java.util.List;

/**
 * Batch Operation Request
 */
@Data
public class BatchOperationDTO {
    private List<Long> ids;
}