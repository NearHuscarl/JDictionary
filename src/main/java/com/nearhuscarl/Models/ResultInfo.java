package com.nearhuscarl.Models;

public class ResultInfo {
    private String message;
    private Status status;
    private Exception exception = null;

    public ResultInfo() {
        this(Status.Failed, "");
    }
    public ResultInfo(Status status) {
        this(status, "");
    }
    public ResultInfo(Status status, String message) {
        this.status = status;
        this.message = message;
    }
    public ResultInfo(Status status, String message, Exception exception) {
        this.status = status;
        this.message = message;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
