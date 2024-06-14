package com.dinesh.eproduct.handler;

import java.util.Map;

public record CustomErrorResponse(
        Map<String,String> errors
) {
}
