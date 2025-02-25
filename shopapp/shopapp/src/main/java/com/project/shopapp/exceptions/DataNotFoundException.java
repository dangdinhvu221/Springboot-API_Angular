package com.project.shopapp.exceptions;

// Kiểm tra exceptions trả về như nào để phân loại
// Phần thực thi bên trong thì ko thêm j
public class DataNotFoundException extends Exception {
    public DataNotFoundException(String message){
        super(message);
    }

}
