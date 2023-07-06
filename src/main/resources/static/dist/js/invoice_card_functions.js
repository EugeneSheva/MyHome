function countTotalPrice(){
      let total_price = 0;
      for(let i = 1; i < document.getElementsByClassName("total_price").length; i++){
        let total_price_element = document.getElementsByClassName("total_price")[i];
        if(total_price_element.value == NaN) continue;
        total_price += parseFloat(total_price_element.value);
      }
      document.getElementById("total_price").value = total_price;
      document.getElementById("total_price_show").innerText = 'Итого: ' + total_price;
};

function count(element) {
   let unit_price = element.parentElement.parentElement.querySelector(".unit_prices").value;
   let unit_amount = element.parentElement.parentElement.querySelector(".unit_amounts").value;
   let total_price = (unit_price*unit_amount).toFixed(3);

   element.parentElement.parentElement.querySelector(".total_price").value = total_price;

   countTotalPrice();
}

function setUnit(select) {
    let service_id = $(select).val();
    let unit = $(select).closest('tr').find('.unit');

    $.get('/myhome/admin/services/get-unit', {id:service_id}, function(data){
        $(unit).val(data.name);
    });
}