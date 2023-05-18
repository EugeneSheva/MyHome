function countTotalPrice(){
      let total_price = 0;
      console.log(total_price);
      for(let i = 1; i < document.getElementsByClassName("total_price").length; i++){
        let total_price_element = document.getElementsByClassName("total_price")[i];
        console.log(total_price_element);
        console.log(total_price_element.value);
        if(total_price_element.value == NaN) continue;
        console.log(parseFloat(total_price_element.value));
        total_price += parseFloat(total_price_element.value);
      }
      document.getElementById("total_price").value = total_price;
      document.getElementById("total_price_show").innerText = 'Итого: ' + total_price;
    };

    function count(element) {
       console.log(element);
       let unit_price = element.parentElement.parentElement.querySelector(".unit_prices").value;
       let unit_amount = element.parentElement.parentElement.querySelector(".unit_amounts").value;
       let total_price = unit_price*unit_amount;


       console.log(unit_price);
       console.log(unit_amount);
       console.log(total_price);

       element.parentElement.parentElement.querySelector(".total_price").value = total_price;

       countTotalPrice();
    }

    function setUnit(select) {
        console.log(select.value);
        let service_id = $(select).val();
        console.log(service_id);
        let unit = $(select).closest('tr').find('.unit');
        console.log(unit);

        $.get('/myhome/admin/services/get-unit', {id:service_id}, function(data){
            $(unit).val(data.name);
        });
    }