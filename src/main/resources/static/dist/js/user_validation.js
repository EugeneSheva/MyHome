

$(document).ready(function() {
    let first_name = document.getElementById('first_name');
    let last_name = document.getElementById('last_name');
    let phone_number = document.getElementById('phone_number');
    let email = document.getElementById('email');
    let password = document.getElementById('password');
    let repeat_password = document.getElementById('confirm_password');

    let show_password = false;

    let save_button = document.getElementById('save_button');


    document.getElementById('showPass').addEventListener('click', () => {
        console.log(this);
        show_password = (show_password) ? false : true;
        if(show_password) {
            document.getElementById('password').type='text';
            document.getElementById("confirm_password").type='text';
        }
        else {
            document.getElementById('password').type='password';
            document.getElementById("confirm_password").type='password';
        }

        console.log(document.getElementById('password'));
    });

    document.getElementById('generate').addEventListener('click', () => {
        let chars = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        let passwordLength = 12;
        let password = "";
         for (let i = 0; i <= passwordLength; i++) {
           let randomNumber = Math.floor(Math.random() * chars.length);
           password += chars.substring(randomNumber, randomNumber +1);
          }
        document.getElementById("password").value = password;
        document.getElementById("confirm_password").value = password;

        document.getElementById("confirm_password").classList.remove('incorrect-input');
        document.getElementById("confirm_password").classList.add('correct-input');
        document.getElementById("confirm_password").previousElementSibling.innerHTML = '<b>Повторить пароль</b>'
        document.getElementById("confirm_password").dataset.value = 1;
        document.getElementById("password").classList.remove('incorrect-input');
        document.getElementById("password").classList.add('correct-input');
        document.getElementById("password").previousElementSibling.innerHTML = '<b>Пароль</b>'
        document.getElementById("password").dataset.value = 1;


    });


})