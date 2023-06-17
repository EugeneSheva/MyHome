let house_selector = $("#house_select");
let section_selector = $("#section_select");
let apartment_selector = $("#apartment_select");
let account = $("#account");
let accountDisplay = $("#account_display");

$(document).ready(function(){

    $("#isActive, #status, #tariff").select2({language: currentLanguage,placeholder:placeholderText, minimumResultsForSearch:Infinity});

    house_selector.select2({
        language: currentLanguage,
        ajax: {
            url: '/myhome/admin/buildings/get-buildings',
            data: function(params){
                console.log(params.page);
                return {
                    search: params.term || "",
                    page: params.page || 1
                }
            },
        },
        placeholder: placeholderText
    });

// селекторы секций и квартир сначала появляются нерабочими
    section_selector.select2({language: currentLanguage,placeholder:selectHouseText});
    apartment_selector.select2({language: currentLanguage,placeholder:selectSectionText});

// изменение дома сбрасывает квартиры и перезагружает секции
    house_selector.change(function(){

        console.log('house selector change triggered');
        console.log('house selector value is ' + this.value);
        buildingID = $(this).val();

        section_selector.select2({language: currentLanguage,placeholder:selectHouseText, minimumResultsForSearch:Infinity});
        apartment_selector.select2({language: currentLanguage,placeholder:selectSectionText, minimumResultsForSearch:Infinity});
        section_selector.html('');
        apartment_selector.html('');
        apartment_selector.prop('disabled', true);

        accountDisplay.val('----------');

        if(this.value === 0) {
          section_selector.prop('disabled', true);

        } else {

          console.log('looking for sections');

          section_selector.prop('disabled', false);

          $.get('/myhome/admin/buildings/get-sections/'+this.value, function(data){

            option = document.createElement("option");
            option.value = 0;
            option.text = placeholderText;
            section_selector.append(option);

            for(let i = 0; i < data.length; i++) {
              option = document.createElement("option");
              option.value = data[i];
              option.text = data[i];
              section_selector.append(option);
            }

            section_selector.val(0);
            section_selector.trigger('change');

          });
        };
    });

// изменение секции перезагружает квартиры
    section_selector.change(function(){

        accountDisplay.val('----------');

       if(section_selector.val() != 0) {
            apartment_selector.html('');
            apartment_selector.prop('disabled', false);
            $.ajax({
                type:'GET',
                url:'/myhome/admin/buildings/get-section-apartments',
                data:{id:buildingID, section_name:section_selector.val()},
                success: function(data){

                    apartment_selector.append(new Option(placeholderText, 0, true, true));

                    for(const apart of data) {
                        let option = new Option('кв. ' + apart.number, apart.id, true, true);
                        apartment_selector.append(option);
                    }

                    if(apartmentID != null && apartmentID != 0) apartment_selector.val(apartmentID)
                    else apartment_selector.val(0);
                    apartment_selector.trigger('change');
                }
            });
       }

    });

// изменение квартиры перезагружает владельца
    apartment_selector.change(function(){

        apartmentID = 0;

        if(apartment_selector.val() != 0) {
            $.get("/myhome/admin/apartments/get-owner", {flat_id:apartment_selector.val()}, function(data){
              owner = data;
              console.log(data);

              $("#owner").val(owner.id);

              $("#owner_name").html('<b>'+ownerText+': </b>');
              $("#owner_phone").html('<b>'+ownerPhoneText+': </b>');

              let name = document.createElement("a");
              name.href = (data != null) ? '/myhome/admin/owners/'+data.id : '#';
              name.text = (data != null) ? data.fullName : notFoundText;

              let phone = document.createElement("a");
              phone.href = (data.phone_number != null) ? 'tel:' + data.phone_number : '#';
              phone.text = (data.phone_number != null) ? data.phone_number : notFoundText;

              $("#owner_name").append(name);
              $("#owner_phone").append(phone);

              // Получение номера лицевого счета
                $.get('/myhome/admin/accounts/get-flat-account',{flat_id:apartment_selector.val()}, function(data){
                    console.log(data);
                    account.val(data.toString());
                    accountDisplay.val(data.toString().padStart(10,'0'));
                })
                .fail(function(){accountDisplay.val('----------');});

                // Получение счетчиков для выбранной квартиры
                $.get('/myhome/admin/apartments/get-meters', {flat_id:apartment_selector.val()}, function(data){

                  console.log(data);

                  let $table_body = $('#meter_table tbody');
                  $table_body.html('');

                  for(const meter of data) {
                      console.log(meter);

                      let table_row = document.createElement("tr");
                      table_row.classList.add("meter_data_row");

                      const date = new Date(meter.date);

                      table_row.innerHTML += '<td>'+meter.id.toString().padStart(10,'0')+'</td>';
                      table_row.innerHTML += '<td>'+meter.status+'</td>';
                      table_row.innerHTML += '<td>'+date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+'</td>';
                      table_row.innerHTML += '<td>'+(date.getMonth()+1)+'.'+date.getFullYear()+'</td>';
                      table_row.innerHTML += '<td>'+meter.apartment.building.name+'</td>';
                      table_row.innerHTML += '<td>'+meter.apartment.section+'</td>';
                      table_row.innerHTML += '<td>'+meter.apartment.number+'</td>';
                      table_row.innerHTML += '<td>'+meter.service.name+'</td>';
                      table_row.innerHTML += '<td>'+parseFloat(meter.currentReadings)+'</td>';
                      table_row.innerHTML += '<td>'+meter.service.unit.name+'</td>';

                      $table_body.append(table_row);
                  }

                });

            });
        } else {
            $("#owner_name").html('<b>'+ownerText+': </b>');
            $("#owner_phone").html('<b>'+ownerPhoneText+': </b>');
        }



    });


// получение дома
    $.ajax({
      type:'GET',
      url: '/myhome/admin/buildings/get-building/',
      data: {building_id: buildingID},
      success: function(data) {

            console.log(data);

          let option = new Option(data.name, data.id, true, true);
          house_selector.append(option).trigger('change');

            // к найденному дому сразу грузит его секции
           $.get('/myhome/admin/buildings/get-sections/'+house_selector.val(), function(data){

               for(let i = 0; i < data.length; i++) {
                 option = document.createElement("option");
                 option.value = data[i];
                 option.text = data[i];
                 section_selector.append(option);
               }
                // пробует подставить существующую секцию, если есть
               console.log('trying to insert correct section ' + apartmentSection);
               if(apartmentSection != null) {

                   section_selector.val(apartmentSection);
                   section_selector.trigger('change');

               }
           });
        },
        error: function(data) {
            console.log(data);
            console.log('building could not be loaded');

            section_selector.select2({language: currentLanguage,placeholder:selectHouseText, minimumResultsForSearch:Infinity});
            apartment_selector.select2({language: currentLanguage,placeholder:selectSectionText, minimumResultsForSearch:Infinity});
            apartment_selector.html('');
            apartment_selector.prop('disabled', true);
            section_selector.html('');
            section_selector.prop('disabled', true);
        }
      });

// Получение номера лицевого счета
$.get('/myhome/admin/accounts/get-flat-account',{flat_id:apartment_selector.val()}, function(data){
    console.log(data);
    account.val(data);
    accountDisplay.val(data.toString().padStart(10,'0'));
})
.fail(function(){
accountDisplay.val('----------');
});


});

