package com.ai.jobfinder.ai;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Prompt {
    private final String content;
}