package com.dinesh.ecustomer.handler;

import java.util.Map;

public record CustomErrorResponse(
        Map<String,String> errors
) {
}
