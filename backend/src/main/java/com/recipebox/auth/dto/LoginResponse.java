package com.recipebox.auth.dto;

public record LoginResponse( String token, String name, String email ) {
}
