

$(document).ready(function() {
    let first_name = document.getElementById('first_name');
    let last_name = document.getElementById('last_name');
    let phone_number = document.getElementById('phone_number');
    let email = document.getElementById('email');
    let password = document.getElementById('password');
    let repeat_password = document.getElementById('confirm_password');

    let show_password = false;

    let save_button = document.getElementById('save_button');

    function countFields() {

        let correct_field_count = parseInt(first_name.dataset.value) + parseInt(last_name.dataset.value)
        + parseInt(phone_number.dataset.value) + parseInt(email.dataset.value) + parseInt(password.dataset.value)
        + parseInt(repeat_password.dataset.value);

//        if(correct_field_count != 6) {
//            save_button.classList.add('btn-default');
//            save_button.classList.remove('btn-success');
//        } else {
//            save_button.classList.add('btn-success');
//            save_button.classList.remove('btn-default');
//        }
    }

    first_name.addEventListener('focusout', (input) => {
        let value = 0;
        console.log('value:'+first_name.value);
        if(first_name.value === '' || first_name.value === undefined) {
            console.log('WRONG VALUE');
            first_name.classList.add('incorrect-input');
            first_name.classList.remove('correct-input');
            first_name.previousElementSibling.innerHTML = '<b>Имя</b><span style="color: red"> - Нужно заполнить имя!</span>';
            first_name.dataset.value = 0;

            countFields();
        } else {
            console.log('CORRECT VALUE');
            first_name.classList.remove('incorrect-input');
            first_name.classList.add('correct-input');
            first_name.previousElementSibling.innerHTML = '<b>Имя</b>'
            first_name.dataset.value = 1;

            countFields();
        }

        console.log(first_name);
    });

    last_name.addEventListener('focusout', () => {
        console.log('value:'+last_name.value);
        if(last_name.value === '' || last_name.value === undefined) {
            console.log('WRONG VALUE');
            last_name.classList.add('incorrect-input');
            last_name.classList.remove('correct-input');
            last_name.previousElementSibling.innerHTML = '<b>Фамилия</b><span style="color: red"> - Нужно заполнить фамилию!</span>';
            last_name.dataset.value = 0;

            countFields();
        } else {
            console.log('CORRECT VALUE');
            last_name.classList.remove('incorrect-input');
            last_name.classList.add('correct-input');
            last_name.previousElementSibling.innerHTML = '<b>Фамилия</b>';
            last_name.dataset.value = 1;

            countFields();
        }
    });

    phone_number.addEventListener('focusout', () => {

        const phone_pattern = new RegExp("0(50|66|99)[0-9]{7}");

        console.log('value:'+phone_number.value);
        if(phone_number.value === '' || phone_number.value === undefined) {
            console.log('WRONG VALUE');
            phone_number.classList.add('incorrect-input');
            phone_number.classList.remove('correct-input');
            phone_number.previousElementSibling.innerHTML = '<b>Телефон</b><span style="color: red"> - Нужно заполнить номер телефона!</span>';
            phone_number.dataset.value = 0;

            countFields();
        } else if(phone_pattern.test(phone_number.value)) {
            console.log('CORRECT VALUE');
            phone_number.classList.remove('incorrect-input');
            phone_number.classList.add('correct-input');
            phone_number.previousElementSibling.innerHTML = '<b>Телефон</b>'
            phone_number.dataset.value = 1;

            countFields();
        }
        else {
            console.log('WRONG VALUE');
            phone_number.classList.add('incorrect-input');
            phone_number.classList.remove('correct-input');
            phone_number.previousElementSibling.innerHTML += '<span style="color: red"> - Неправильный формат телефона! (e.g. 0997524927)</span>';
            phone_number.dataset.value = 0;

            countFields();
        }
    });

    email.addEventListener('focusout', () => {

        const pattern = new RegExp("^[a-z]+@[a-z]{2,}\\.[a-z]{2,3}$");
        console.log(pattern);

        console.log('value:'+email.value);
        if(email.value === '' || email.value === undefined) {
            console.log('WRONG VALUE');
            email.classList.add('incorrect-input');
            email.classList.remove('correct-input');
            email.previousElementSibling.innerHTML = '<b>E-mail(логин)</b><span style="color: red"> - Нужно указать почту!</span>';
            email.dataset.value = 0;

            countFields();
        } else if(pattern.test(email.value)) {
            console.log('CORRECT VALUE');
            email.classList.remove('incorrect-input');
            email.classList.add('correct-input');
            email.previousElementSibling.innerHTML = '<b>E-mail(логин)</b>'
            email.dataset.value = 1;

            countFields();
        }
        else {
            console.log('WRONG VALUE');
            email.classList.add('incorrect-input');
            email.classList.remove('correct-input');
            email.previousElementSibling.innerHTML = '<b>E-mail(логин)</b><span style="color: red"> - Неправильный формат почты! (e.g. test@gmail.com)</span>';
            email.dataset.value = 0;

            countFields();
        }
    });

    password.addEventListener('focusout', () => {

        console.log('value:'+password.value);
        if(password.value === '' || password.value === undefined) {
            console.log('WRONG VALUE');
            password.classList.add('incorrect-input');
            password.classList.remove('correct-input');
            password.previousElementSibling.innerHTML = '<b>Пароль</b><span style="color: red"> - Нужно указать пароль!</span>';
            password.dataset.value = 0;

            countFields();
        } else if(password.value.length > 8) {
            if(repeat_password.value === password.value) {
                console.log('CORRECT VALUE');
                password.classList.remove('incorrect-input');
                password.classList.add('correct-input');
                password.previousElementSibling.innerHTML = '<b>Пароль</b>'
                password.dataset.value = 1;
                repeat_password.classList.remove('incorrect-input');
                repeat_password.classList.add('correct-input');
                repeat_password.previousElementSibling.innerHTML = '<b>Повторить пароль</b>'
                repeat_password.dataset.value = 1;

                countFields();
            }
            else {
                console.log('WRONG VALUE');
                password.classList.add('incorrect-input');
                password.classList.remove('correct-input');
                repeat_password.classList.add('incorrect-input');
                repeat_password.classList.remove('correct-input');
                password.previousElementSibling.innerHTML = '<b>Пароль</b><span style="color: red"> - Пароли должны совпадать!</span>';
                repeat_password.previousElementSibling.innerHTML = '<b>Повторить пароль</b><span style="color: red"> - Пароли должны совпадать!</span>';
                password.dataset.value = 0;
                repeat_password.dataset.value = 0;

                countFields();
            }
        }
        else {
            console.log('WRONG VALUE');
            password.classList.add('incorrect-input');
            password.classList.remove('correct-input');
            password.previousElementSibling.innerHTML = '<b>Пароль</b><span style="color: red"> - Пароль должен быть больше 8 символов длиной!</span>';
            password.dataset.value = 0;

            countFields();
        }
    });

    repeat_password.addEventListener('focusout', () => {

        console.log('value:'+repeat_password.value);
        if(repeat_password.value === '' || repeat_password.value === undefined) {
            console.log('WRONG VALUE');
            repeat_password.classList.add('incorrect-input');
            repeat_password.classList.remove('correct-input');
            repeat_password.previousElementSibling.innerHTML = '<b>Повторить пароль</b><span style="color: red"> - Нужно указать пароль!</span>';
            repeat_password.dataset.value = 0;

            countFields();
        } else if(repeat_password.value.length > 8) {
            if(repeat_password.value === password.value) {
                console.log('CORRECT VALUE');
                repeat_password.classList.remove('incorrect-input');
                repeat_password.classList.add('correct-input');
                repeat_password.previousElementSibling.innerHTML = '<b>Повторить пароль</b>'
                repeat_password.dataset.value = 1;
                password.classList.remove('incorrect-input');
                password.classList.add('correct-input');
                password.previousElementSibling.innerHTML = '<b>Пароль</b>'
                password.dataset.value = 1;

                countFields();
            }
            else {
                console.log('WRONG VALUE');
                password.classList.add('incorrect-input');
                password.classList.remove('correct-input');
                repeat_password.classList.add('incorrect-input');
                repeat_password.classList.remove('correct-input');
                password.previousElementSibling.innerHTML = '<b>Пароль</b><span style="color: red"> - Пароли должны совпадать!</span>';
                repeat_password.previousElementSibling.innerHTML = '<b>Повторить пароль</b><span style="color: red"> - Пароли должны совпадать!</span>';
                repeat_password.dataset.value = 0;
                repeat_password.dataset.value = 0;

                countFields();
            }
        }
        else {
            console.log('WRONG VALUE');
            repeat_password.classList.add('incorrect-input');
            repeat_password.classList.remove('correct-input');
            repeat_password.previousElementSibling.innerHTML = '<b>Повторить пароль</b><span style="color: red"> - Пароль должен быть больше 8 символов длиной!</span>';
            repeat_password.dataset.value = 0;

            countFields();
        }
    });

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

        countFields();

    });


})