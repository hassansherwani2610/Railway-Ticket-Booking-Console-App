package org.example.app.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashUtil {
    public static String hashPassword(String originalPassword){
        if(originalPassword == null || originalPassword.isEmpty()){
            System.out.println("Password can not be NUll or EMPTY");
        }

        return BCrypt.hashpw(originalPassword , BCrypt.gensalt(12)); // Cost Factor = 12, to make the hashed password more SECURE & difficult for HACKER to crack it.
    }

    public static Boolean checkPassword(String originalPassword , String hashedPassword){
        return BCrypt.checkpw(originalPassword , hashedPassword);
    }

    private PasswordHashUtil(){ // So no one will make OBJECT of this class, In other words we make this utility class Non-Instantiable.
        throw new UnsupportedOperationException("Utility Class");
    }
}
