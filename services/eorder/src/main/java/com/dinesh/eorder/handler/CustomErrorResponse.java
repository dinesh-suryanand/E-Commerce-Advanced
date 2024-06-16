package com.dinesh.eorder.handler;

import java.util.Map;

public record CustomErrorResponse(
        Map<String,String> errors
) {
}
