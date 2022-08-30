$(document)
		.ready(
				function() {

					$("#cImg").prop('src', 'captcha');

					$("#refreshCaptcha").click(function() {
						refreshCaptcha();
						return false;
					});

					$('#login-submit').click(function(e) {
						if (!validateLogin()) {
							e.preventDefault();
						}
					});
					$('#getEmailOtp').click(function(e) {
						if (!getEmailOtpReg()) {
							e.preventDefault();
						} else {
							$('#captchEmailmodel').show();
						}
					});

					$('#emailOtpBtn').click(function() {
						//debugger;
						//alert("send otp"+$('#username').val());
						$('#forgotPass').attr('action', 'getForgotOtp');
						//$('#forgotPass').submit();
					});

					$('#verifyUser').click(function(e) {

						if (!getForgetOtpReg()) {
							e.preventDefault();
						} else {
							$('#forgotPass').attr('action', 'forgotPassword');
						}
						//this.form.submit();
					});

				/*	var myTextEmail = document.getElementById("email");
					// var wordCount = document.getElementById("wordCount");

					myTextEmail
							.addEventListener(
									"blur",
									function() {
										var validRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
										if (myTextEmail.value.match(validRegex)) {

											// alert("Valid email address!");

											// document.form1.text1.focus();

											return true;

										} else {

											alert("Invalid email address!");

											document.form1.text1.focus();

											return false;

										}

									});
*/
					
					 

					$('.captchEmailmodelClose').click(function() {
						$('#captchEmailmodel').hide();
					});

					$('#forgotPassbtn').click(function(e) {
						if (!validateForgotPass()) {
							e.preventDefault();
						}
					});

					$("#password").on('keydown', function(e) {
						if (e.keyCode === 13) {
							passHashhhh();
						}
					});

				});

function refreshCaptcha() {
	var captcha = 'captcha?' + new Date().getTime();
	$('#cImg').attr('src', captcha);
}

/**
 * This method check field is empty or not .
 * 
 * @param field variable of the field.
 * @return boolean true/false.
 */
function isEmpty(field) {
	if (field == '')
		return true;
	else
		return false;
}

/**
 * This function check the data is malicious or not.
 * 
 * @param field - show the error message in this field
 * @param data - data to check
 * @return boolean true/false.
 */
function isSafe(field, data) {
	var dataReg = /^[A-Za-z0-9\s$@#._-]+$/;
	var error = field + "Err";
	if ((!dataReg.test(data))) {
		$(error).text('Malicious data ...').addClass("errMsg");
		return false;
	}
	return true;
}

function passwordValidation(fId, password, passwordErr) {
	var passwordReg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$_.])[a-zA-Z0-9!@#$_.]{8,20}$/;
	if (isBlank(fId, password, passwordErr))
		return false;
	if (!isBlank(fId, password, passwordErr)) {
		if (!passwordReg.test(password)) {
			$('#passwordErr')
					.text(
							'Password must have at least 8 characters, an uppercase,'
									+ ' a lowercase, a number and a special character(!@#$_.)')
					.addClass("errMsg");
			return false;
		}
	}
	return true;
}
function conpasswordValidation(fId, password, confirmPassword,
		confirmPasswordErr) {
	if (isBlank(fId, confirmPassword, confirmPasswordErr))
		return false;
	if (!isBlank(fId, confirmPassword, confirmPasswordErr)) {
		if (password != confirmPassword) {
			$('#confirmPasswordErr').text('Password Mismatch').addClass(
					"errMsg");
			return false;
		}
	}
	return true;
}

function checkMobile_alternate() {

	var value = $("#alternateNo").val();

	var res = isValidMobile_alternate('alternateNo', value, 'passwordErr');
	if (res == false)

		document.getElementById("update_btn").disabled = true;
	else
		document.getElementById("update_btn").disabled = false;
}

function checkMobile() {

	var value = $("#mobileNo").val();

	var res = isValidMobile('mobileNo', value, 'MobileErr');

	if (res == false)

		document.getElementById("update_btn").disabled = true;
	else {

		document.getElementById("update_btn").disabled = false;
	}
}

function isValidMobile_alternate(fId, field, error) {
	var numberReg = /^[0-9]{10,10}$/;
	// var numberReg=/^[1-9]{1}[0-9]{9}$/;

	if (field != '' && !numberReg.test(field)) {
		// $(error).text('Please enter valid mobile').addClass("errMsg");
		document.getElementById(error).innerHTML = "Please enter valid mobile";
		return false;
	}
	document.getElementById(error).innerHTML = "";
	return true;

}

function isValidMobile(fId, field, error) {
	var numberReg = /^[0-9]{10,10}$/;
	// var numberReg=/^[1-9]{1}[0-9]{9}$/;
	if (isBlank(fId, field, error))
		return false;
	if (field != '' && !numberReg.test(field)) {
		// $(error).text('Please enter valid mobile').addClass("errMsg");
		document.getElementById(error).innerHTML = "Please enter valid mobile";
		return false;
	}
	document.getElementById(error).innerHTML = "";
	return true;

}

function isBlank(fId, field, error) {
	if (field == '') {
		$(error).text('Required Field').addClass("errMsg");
		$(fId).addClass("inputErr");
		return true;
	}
	$(error).text('');
	$(fId).removeClass("inputErr");
	return false;
}

/**
 * Validate Login
 */
/* $("#password").onchange(function() {
    	var encrypted = CryptoJS.AES.encrypt($('#password').val(), $('#key').val());	    	
    	console.log($('#password').val()+'     '+encrypted);
    });*/

function validateLogin(newhash) {
	var username = $('#username').val();
	var password = $('#password').val();
	var captcha = $('#captcha').val();
	var isValid = [];
	isValid.push(!isBlank('#username', username, "#usernameErr"));
	isValid.push(!isBlank('#password', password, "#passwordErr"));
	//isValid.push(!isBlank('#captcha', captcha, '#captchaErr'));
	if (!isEmpty(username))
		isValid.push(isSafe('#username', username));
	for (var i = isValid.length - 1; i >= 0; i--) {
		if (!isValid[i])
			return false;
	}
	return true;
}

function passHashhhh() {
	//debugger
	var password = $('#password').val();
	var passwordReg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$_.])[a-zA-Z0-9!@#$_.]{8,20}$/;
	if (isBlank('#password', password, "#passwordErr"))
		return false;
	if (!isBlank('#password', password, "#passwordErr")) {
		if (!passwordReg.test(password)) {
			$('#passwordErr')
					.text(
							'Password must have at least 8 characters, an uppercase,'
									+ ' a lowercase, a number and a special character(!@#$_.)')
					.addClass("errMsg");
			return false;
		} else {
			var encryptedBase64Key = $('#key').val();
			var parsedBase64Key = CryptoJS.enc.Base64.parse(encryptedBase64Key);
			var encryptedData = CryptoJS.AES.encrypt(password, parsedBase64Key,
					{
						mode : CryptoJS.mode.ECB,
						padding : CryptoJS.pad.Pkcs7
					});
			//console.log("encryptedData = " + encryptedData);
			$('#password').val(encryptedData.toString());
		}
	}

}

function forgotPasswordHash(val) {
	//debugger
	var password = $('#password').val();
	var conPassword = $('#confirmpass').val();

	var passwordReg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$_.])[a-zA-Z0-9!@#$_.]{8,20}$/;
	if (val == 1) {
		if (isBlank('#password', password, "#passwordErr"))
			return false;
		if (!isBlank('#password', password, "#passwordErr")) {
			if (!passwordReg.test(password)) {
				$('#passwordErr')
						.text(
								'Password must have at least 8 characters, an uppercase,'
										+ ' a lowercase, a number and a special character(!@#$_.)')
						.addClass("errMsg");
				return false;
			} else {
				var encryptedBase64Key = $('#key').val();
				var parsedBase64Key = CryptoJS.enc.Base64
						.parse(encryptedBase64Key);
				var encryptedData = CryptoJS.AES.encrypt(password,
						parsedBase64Key, {
							mode : CryptoJS.mode.ECB,
							padding : CryptoJS.pad.Pkcs7
						});
				//console.log("encryptedData = " + encryptedData);
				$('#password').val(encryptedData.toString());
			}
		}
	}
	if (val == 2) {
		if (isBlank('#confirmpass', conPassword, '#confirmpassErr'))
			return false;
		if (!isBlank('#confirmpass', conPassword, '#confirmpassErr')) {
			if (!passwordReg.test(conPassword)) {
				$('#confirmpassErr')
						.text(
								'Password must have at least 8 characters, an uppercase,'
										+ ' a lowercase, a number and a special character(!@#$_.)')
						.addClass("errMsg");
				return false;
			} else {
				var encryptedBase64Key = $('#key').val();
				var parsedBase64Key = CryptoJS.enc.Base64
						.parse(encryptedBase64Key);
				var encryptedData = CryptoJS.AES.encrypt(conPassword,
						parsedBase64Key, {
							mode : CryptoJS.mode.ECB,
							padding : CryptoJS.pad.Pkcs7
						});
				//console.log("encryptedData = " + encryptedData);
				$('#confirmpass').val(encryptedData.toString());
			}
		}
	}
}

/**
 * This method is for checking the field contain valid email or not.
 * 
 * @param fid is variable of the fid.
 * @param field is variable of the field.
 * @param error is the reference of error message span.
 * @return boolean true/false.
 */
function isValidEmail(fId, field, error) {
	var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
	if (isBlank(fId, field, error))
		return false;
	if (!emailReg.test(field)) {
		$(error).text('Invalid mail id').addClass("errMsg");
		return false;
	}
	$(error).text('');
	return true;
}

function getEmailOtpReg() {
	var email = $('#username').val();
	var isValid = [];
	isValid.push(isValidEmail('#username', email, '#usernameErr'));
	for (var i = isValid.length - 1; i >= 0; i--) {
		if (!isValid[i])
			return false;
	}
	return true;
}

function getForgetOtpReg() {
	var otp = $('#otp').val();
	var isValid = [];

	if (isEmpty(otp)) {
		$('#otpErr').text('Required Field').addClass("errMsg");
		isValid.push(false);
	}

	var email = $('#username').val();
	isValid.push(isValidEmail('#username', email, '#usernameErr'));

	for (var i = isValid.length - 1; i >= 0; i--) {
		if (!isValid[i])
			return false;
	}
	return true;
}
function validateForgotPass() {
	var password = $('#password').val();
	var confirmpass = $('#confirmpass').val();
	var isValid = [];
	isValid.push(!isBlank('#password', password, "#passwordErr"));
	isValid.push(!isBlank('#confirmpass', confirmpass, "#confirmpassErr"));
	/*if (!isEmpty(password))
		isValid.push(passwordValidation('#password', password,'#passwordErr'));
	if (!isEmpty(confirmpass))
		isValid.push(passwordValidation('#confirmpass', confirmpass,'#confirmpassErr'));*/
	if (!isEmpty(confirmpass)) {
		if (password != confirmpass) {
			$('#confirmpassErr').text(
					'Password and confirm Password must be same').addClass(
					"errMsg");
			isValid.push(false);
		}
	}
	for (var i = isValid.length - 1; i >= 0; i--) {
		if (!isValid[i])
			return false;
	}
	//alert(newhash);
	//var finalhash = sha256.update(salt + sha256.update(password));
	//var finalhash=newhash;
	//$('#password').val(finalhash);
	return true;
}

function changePasswordHash(val) {
	//debugger
	var password = $('#password').val();
	var conPassword = $('#confirmPassword').val();
	var passwordReg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$_.])[a-zA-Z0-9!@#$_.]{8,20}$/;
	if (val == 1) {
		if (isBlank('#password', password, "#passwordErr"))
			return false;
		if (!isBlank('#password', password, "#passwordErr")) {
			if (!passwordReg.test(password)) {
				$('#msgnewpasswrong')
						.text(
								'Password must have at least 8 characters, an uppercase,'
										+ ' a lowercase, a number and a special character(!@#$_.)')
						.addClass("errMsg");
				return false;
			} else {
				$('#msgnewpasswrong').text('').removeClass("errMsg");
				var encryptedBase64Key = $('#key').val();
				var parsedBase64Key = CryptoJS.enc.Base64
						.parse(encryptedBase64Key);
				var encryptedData = CryptoJS.AES.encrypt(password,
						parsedBase64Key, {
							mode : CryptoJS.mode.ECB,
							padding : CryptoJS.pad.Pkcs7
						});
				//console.log("encryptedData = " + encryptedData);
				$('#password').val(encryptedData.toString());
			}
		}
	}
	if (val == 2) {
		if (isBlank('#confirmPassword', conPassword, '#confirmPasswordErr'))
			return false;
		if (!isBlank('#confirmPassword', conPassword, '#confirmPasswordErr')) {
			if (!passwordReg.test(conPassword)) {
				$('#msgconfpasswrong')
						.text(
								'Password must have at least 8 characters, an uppercase,'
										+ ' a lowercase, a number and a special character(!@#$_.)')
						.addClass("errMsg");
				return false;
			} else {
				$('#msgconfpasswrong').text('').removeClass("errMsg");
				var encryptedBase64Key = $('#key').val();
				var parsedBase64Key = CryptoJS.enc.Base64
						.parse(encryptedBase64Key);
				var encryptedData = CryptoJS.AES.encrypt(conPassword,
						parsedBase64Key, {
							mode : CryptoJS.mode.ECB,
							padding : CryptoJS.pad.Pkcs7
						});
				//console.log("encryptedData = " + encryptedData);
				$('#confirmPassword').val(encryptedData.toString());
			}
		}
	}
}
function isNumber(evt) {
	evt = (evt) ? evt : window.event;
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	if (charCode > 31 && (charCode < 48 || charCode > 57)) {
		return false;
	}
	return true;
}

function Validate_alphanumeric(e) {
	var keyCode = e.keyCode || e.which;

	//Regex for Valid Characters i.e. Alphabets and Numbers.
	var regex = /^[A-Za-z0-9. ]+$/;

	//Validate TextBox value against the Regex.
	var isValid = regex.test(String.fromCharCode(keyCode));
	if (!isValid) {
		alert("Only Alphabets and Numbers allowed.");
	}

	return isValid;
}
