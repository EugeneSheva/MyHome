function loadApartments(input) {
    let owner_id = input.value;
    console.log(owner_id);

    $("#apartmentID").prop("disabled", (owner_id != '0') ? false : true);

    $.get('/myhome/admin/owners/get-apartments/' + owner_id, function(data){
      console.log(data);

      $("#apartmentID").html('');

      for(let i = 0; i < data.length; i++) {
        let option = document.createElement("option");
        option.value = data[i].id;
        option.text = 'кв. ' + data[i].number + ', ' + data[i].building.name;
        $("#apartmentID").append(option);
      }
    });
  }

$(document).ready(function(){



//    if(requestApartID != null) {
//        $.get("/myhome/admin/apartments/get-apartment?id="+requestApartID,function(data) {
//            console.log(data);
//            let option = new Option(data.fullName, data.id, null, null);
//            $("#apartmentID").append(option);
//            $("#apartmentID").val(data.id);
//        })
//    }


    $("#date, #time").change(function(){
      console.log($(this).val());
    });

    let owner_input = document.getElementById("ownerID");
    owner_input.addEventListener('change', () => loadApartments(owner_input));

    $("#ownerID").select2({
        language: currentLanguage,
        ajax: {
            url: '/myhome/admin/owners/get-all-owners',
            data: function(params){
                console.log(params.page);
                return {
                    search: params.term || "",
                    page: params.page || 1
                }
            },

        },
        placeholder: placeholderText,
        minimumResultsForSearch: 2
    });

    $("#apartmentID, #masterTypeID, #status, #masters").select2({language: currentLanguage,placeholder:placeholderText, minimumResultsForSearch:Infinity});

    $("#ownerID").change(function(){
      let owner_id = $("#ownerID").val();

      $("#apartmentID").prop("disabled", (owner_id != '0') ? false : true);

      $.get('/myhome/admin/owners/get-apartments/' + owner_id, function(data){
        console.log(data);

        $("#apartmentID").html('');

        for(let i = 0; i < data.length; i++) {
          let option = document.createElement("option");
          option.value = data[i].id;
          option.text = 'кв. ' + data[i].number + ', ' + data[i].building.name;
          $("#apartmentID").append(option);
        }
      });
    });

    $("#masterTypeID").change(function(){
      let master_type = $(this).val();

      $.get('/myhome/admin/admins/get-masters-by-type', {typeID:master_type}, function(data){
        console.log(data);

        $("#masters").html('');

        for(let i = 0; i < data.length; i++) {
          let option = document.createElement("option");
          console.log(data[i].role);
          option.value = data[i].id;
          option.text = data[i].role + ' - ' + data[i].first_name + ' ' + data[i].last_name;
          $("#masters").append(option);
        }

        if(data.length === 0) {
            let option = document.createElement("option");
            option.value = -1;
            option.text = notFoundText;
            $("#masters").append(option);
        }
      })
    });



    if(requestApartOwner != null) {
        $.get("/myhome/admin/owners/get-owner?id="+requestApartOwner, function(data){
            console.log(data);
            let option = new Option(data.text, data.id, null, null);
            $("#ownerID").append(option);
            $("#ownerID").val(data.id);
        }).then(function(){
            // add apartments to list
            $.get("/myhome/admin/owners/get-apartments/"+requestApartOwner, function(data){
                console.log(data);
                for(const apartment of data) {
                    let option = new Option(apartment.fullName, apartment.id, null, null);
                    $("#apartmentID").append(option);
                }

                // select previous apartment
                if(requestApartID != null) {
                    $("#apartmentID").val(requestApartID);
                }
            })
        });


    }

    $("#comment").summernote({height:150});

  })
