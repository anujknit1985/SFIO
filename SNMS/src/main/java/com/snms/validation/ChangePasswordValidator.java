package com.snms.validation;

import org.springframework.validation.Errors;

import com.snms.dto.UserForm;

public class ChangePasswordValidator {
  
  final int editPassword = 1;
  final int forgetPassword = 2;
  
  /**
   * Check user validation for 
   * @param userForm - object of UserForm
   * @param captchaRequired - boolean variable for captcha required
   * @param purpose - int variable for which purpose method used
   * @param errors - objects of Errors
   */
  public void userValidatePassword(UserForm userForm, boolean captchaRequired, 
          int purpose, Errors errors){
      String username = null;
      String password = null;
     // String captcha = captchaRequired ? userForm.getCaptcha() : null;
      String changePassword = userForm.getChangePassword();
      String confirmPassword = userForm.getConfirmPassword();
      
      if(purpose == editPassword){
          password = userForm.getPassword();
         // validatePass("password", password, true, errors);
      }
      if (purpose == forgetPassword){
          username = userForm.getUsername();
          if ("".equals(username.trim()))
              errors.rejectValue("username", "errmsg.required");
      }

      validatePass("changePassword", changePassword, true, errors);
      validatePass("confirmPassword", confirmPassword, true, errors);
      if(purpose == editPassword)
          isSameOldNewPass("changePassword", changePassword, password,
                  errors);
      isSameNewConfirmPass("confirmPassword", confirmPassword,
                  changePassword, errors);
      /*if(captchaRequired)
          if (!new NfraValidator().isValidCaptcha(captcha,errors))
              errors.rejectValue("captcha", "errmsg.captcha");*/
  }
  /**
   * Check the password validation 
   * @param passfield - password field value
   * @param isrequired - boolean variable for for mendatory or not
   * @param errors - objects of Errors
   */
  public void validatePass(String passfield,String password,
          boolean isrequired, Errors errors){
      if(!isrequired) return;
      if (password == null || "".equals(password.trim()))
          errors.rejectValue(passfield, "errmsg.required");
      else 
          new SNMSValidator().isValidData(passfield, password, errors);
  }
  /**
   * Check the password validation for current and new password
   * @param changePasswordField - new password field value
   * @param password - existing password value
   * @param errors - objects of Errors
   */
  public void isSameOldNewPass(String changePasswordField, 
          String changePassword, String password, Errors errors){
      if(changePassword == null || "".equals(changePassword))        
          errors.rejectValue(changePasswordField, "errmsg.required");           
      else if(changePassword.equals(password))
          errors.rejectValue(changePasswordField, "msg.samepassdeny");
  }
  /**
   * Check the password validation for new and confirm password
   * @param confirmPasswordField - confirm password field value
   * @param confirmPassword - confirm password value
   * @param newPassword - new password value
   * @param errors - objects of Errors
   */
  public void isSameNewConfirmPass(String confirmPasswordField, 
          String confirmPassword,String newPassword, Errors errors){
      if(confirmPassword == null || "".equals(confirmPassword))
          errors.rejectValue(confirmPasswordField, "errmsg.required");
      else if(!confirmPassword.equals(newPassword))
          errors.rejectValue(confirmPasswordField, "errmsg.conPassword");
  }

}
