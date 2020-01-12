package top.cloudli.gateway.tool;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncode {
    private static PasswordEncoder bCrypt = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        System.out.println(bCrypt.encode("e10adc3949ba59abbe56e057f20f883e"));
    }
}
