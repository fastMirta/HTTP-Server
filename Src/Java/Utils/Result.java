package Src.Java.Utils;

public class Result<T> {
    private String result = "";
    private boolean isSuccess;
    private T data;

    private Result(String error, boolean isSuccess, T data){
        this.result = error;
        this.isSuccess = isSuccess;
        this.data = data;
    }

    public String getError(){
        return result;
    }

    public boolean isSuccess(){
        return isSuccess;
    }

    public T getData(){
        return data;
    }


    public static <T> Result<T> success(T data) {return new Result<>("", true, data);}
    public static <T> Result<T> failure(String error) {return new Result<T>(error, false, null);}
}
