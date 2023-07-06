$(document).ready(function(){

    $("#house_select").change(function(){
        console.log(this.value);

        let section_select = document.getElementById("section_select");
        section_select.innerHTML = '';

        let apartment_select = document.getElementById("apartment_select");
        apartment_select.innerHTML = '';

        if(this.value === '0') {

          console.log("IF");

          section_select.disabled = true;
          let o = document.createElement("option");
          o.value = 0;
          o.text = 'Сначала выберите дом';
          o.selected = true;

          section_select.appendChild(o);

          apartment_select.disabled = true;
          o = document.createElement("option");
          o.value = 0;
          o.text = 'Сначала выберите секцию';
          o.selected = true;

          apartment_select.appendChild(o);
        } else {

          console.log("ELSE");

          section_select.disabled = false;
          apartment_select.disabled = true;

          let option = document.createElement("option");
          option.value = 0;
          option.text = 'Выберите...';
          option.selected = true;

          section_select.appendChild(option);

          option = document.createElement("option");
          option.value = 0;
          option.text = 'Сначала выберите секцию';
          option.selected = true;

          apartment_select.appendChild(option);

          $.get('/myhome/admin/buildings/get-sections/'+this.value, function(data){

            console.log(data);

            for(let i = 0; i < data.length; i++) {
              option = document.createElement("option");
              option.value = data[i];
              option.text = data[i];
              section_select.appendChild(option);
            }
          });
        };
    });

    $("#section_select").change(function(){

        let house_select = document.getElementById("house_select");
        console.log(house_select.value);

        let apartment_select = document.getElementById("apartment_select");
        apartment_select.innerHTML = '';
        apartment_select.disabled = false;

        let option = document.createElement("option");
        option.value = 0;
        option.text = 'Выберите...';
        option.selected = true;

        apartment_select.appendChild(option);

        $.get('/myhome/admin/buildings/get-section-apartments/'+house_select.value, {section_name:this.value}, function(data){

          console.log(data);

          for(let i = 0; i < data.length; i++) {
            let option = document.createElement("option");
            option.value = data[i].id;
            option.text = 'кв. ' + data[i].number;
            apartment_select.appendChild(option);
          }

        });
    });

    $("#apartment_select").change(function(){

      let flat_id = $(this).val();

      $.get('/myhome/admin/apartments/get-owner', {flat_id:flat_id}, function(data){

        alert('получил владельцa');
        console.log(data);

        $("#owner_name").empty();
        $("#owner_phone").empty();

        let name = document.createElement("a");
        name.href = '/myhome/admin/owners/'+data.id;
        name.text = data.first_name + ' ' + data.last_name;

        let phone = document.createElement("a");
        phone.href = 'tel:' + data.phone_number;
        phone.text = data.phone_number;

        $("#owner_name").append('<b>Владелец: </b>');
        $("#owner_name").append(name);
        $("#owner_phone").append('<b>Телефон: </b>');
        $("#owner_phone").append(phone);

      });

      $.get('/myhome/admin/accounts/get-flat-account',{flat_id:flat_id}, function(data){
          alert('получил лицевой счёт');
          console.log(data);

          document.getElementById("account").value = data;
      });

    });

  })