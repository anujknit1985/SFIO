package com.snms.dto;

import lombok.Data;

@Data
public class UserForm
{

    private Long    id;
    private String username;
    private String password;
    private String captcha;
   /* private String captcha;
    private String saltId;*/
    private String changePassword;
    private String confirmPassword;
    private String textOTP;
    private String key;
   /* private String textOTP;
    private boolean isOtpGenerated;*/

   
}
