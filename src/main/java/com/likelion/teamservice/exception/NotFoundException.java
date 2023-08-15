package com.likelion.teamservice.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static class Team extends NotFoundException{
        public Team() {
            super("팀");
        }
    }
}
