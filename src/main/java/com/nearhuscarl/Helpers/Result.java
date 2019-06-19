package com.nearhuscarl.Helpers;

public class Result<T> {
    private ResultInfo info;
    private T data;

    public Result(ResultInfo info, T data) {
        this.info = info;
        this.data = data;
    }

    public ResultInfo getInfo() {
        return info;
    }

    public void setInfo(ResultInfo info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
