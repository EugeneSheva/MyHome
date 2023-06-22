
//Получение информации о текущем состоянии страницы - текущий номер, размер и фильтры
//Если информация уже сохранялась в history.state (через функцию saveState), она берется оттуда, иначе дефолтная
let currentPageNumber, currentPageSize, pageFiltersString;
if(history.state != null) {
    currentPageNumber = history.state.page;
    currentPageSize = history.state.size;
    pageFiltersString = history.state.filters;

    setFilters(pageFiltersString);
} else {
    currentPageNumber = 1;
    currentPageSize = 10;
    pageFiltersString = '';
}

//сбор фильтров со страницы
function gatherFilters() {

    let active = $("#active").val();
    let account = $("#account").val();
    let accountId = $("#accountId").val();
    let address = $("#address").val();
    let apartment_number = $("#apartment").val();
    let building_id = $("#building").val();
    let completed = $("#completed").val();
    let date = $("#date").val();
    let datetime = $("#datetime").val();
    let debt = $("#debt").val();
    let debtSting = $("#debtSting").val();
    let description = $("#description").val();
    let email = $("#email").val();
    let floor = $("#floor").val();
    let id = $("#id").val();
    let month = $("#month").val();
    let master_id = $("#master").val();
    let master_type = $("#master_type").val();
    let name = $("#name").val();
    let number = $("#number").val();
    let owner_id = $("#owner").val();
    let phone = $("#phone").val();
    let section_name = $("#section").val();
    let service_id = $("#service").val();
    let status = $("#status").val();
    let role = $("#role").val();
    let ownerName = $("#ownerName").val();
    let buildingName = $("#buildingName").val();
    let isCompleted = $("#isCompleted").val();
    let incomeExpenseItem = $("#incomeExpenseItem").val();
    let incomeExpenseType = $("#incomeExpenseType").val();

    let filterForm = {
        active: (active != null) ? active : null,
        building: (building_id) ? building_id : null,
        service: (service_id) ? service_id : null,
        apartment: (apartment_number) ? apartment_number : null,
        section: (section_name) ? section_name : null,
        id: (id) ? id : null,
        datetime: (datetime) ? datetime : null,
        date: (date) ? date : null,
        month: (month) ? month : null,
        description: (description) ? description : null,
        master_type: (master_type) ? master_type : null,
        master: (master_id) ? master_id : null,
        owner: (owner_id) ? owner_id : null,
        phone: (phone) ? phone : null,
        status: (status) ? status : null,
        completed: (completed != null) ? completed : null,
        debt: (debt != null) ? debt : null,
        debtSting: (debtSting != null) ? debtSting : null,
        name: (name) ? name : null,
        role: (role) ? role : null,
        email: (email) ? email : null,
        address: (address) ? address : null,
        number: (number) ? number : null,
        floor: (floor) ? floor: null,
        account : (account) ? account : null,
        accountId : (accountId) ? accountId : null,
        ownerName : (ownerName) ? ownerName : null,
        buildingName : (buildingName) ? buildingName : null,
        isCompleted : (isCompleted) ? isCompleted : null,
        incomeExpenseItem : (incomeExpenseItem) ? incomeExpenseItem : null,
        incomeExpenseType : (incomeExpenseType) ? incomeExpenseType : null
    };


    return filterForm;
}

function setFilters(filters) {

    if(filters === null) {
        $("#address").val(null);
        $("#account").val(null);
        $("#accountId").val(null);
        $("#building").val('').trigger('change');
        $("#service").val('').trigger('change');
        $("#apartment").val(null);
        $("#section").val('').trigger('change');
        $("#id").val(null);
        $("#month").val(null);
        $("#datetime").val(null);
        $("#date").val(null);
        $("#debt").val('').trigger('change');
        $("#debtSting").val(null);
        $("#email").val(null);
        $("#floor").val(null);
        $("#name").val(null);
        $("#number").val(null);
        $("#role").val('').trigger('change');
        $("#description").val(null);
        $("#master_type").val('').trigger('change');
        $("#master").val('').trigger('change');
        $("#owner").val('').trigger('change');
        $("#phone").val(null);
        $("#status").val('').trigger('change');
        $("#completed").val('').trigger('change');
        $("#active").val('').trigger('change');
        $("#ownerName").val(null);
        $("#buildingName").val(null);
        $("#isCompleted").val('-').trigger('change');
        $("#incomeExpenseItem").val('-').trigger('change');
        $("#incomeExpenseType").val('-').trigger('change');
        return;
    }
    else {
        $("#address").val(filters.address);
        $("#account").val(filters.account);
        $("#accountId").val(filters.accountId);
        $("#date").val(filters.date);
        $("#debt").val(filters.debt);
        $("#debtSting").val(filters.debtSting);
        $("#email").val(filters.email);
        $("#floor").val(filters.floor);
        $("#name").val(filters.name);
        $("#number").val(filters.number);
        $("#role").val(filters.role);
        $("#building").val(filters.building);
        $("#service").val(filters.service);
        $("#apartment").val(filters.apartment);
        $("#section").val(filters.section);
        $("#id").val(filters.id);
        $("#datetime").val(filters.datetime);
        $("#month").val(filters.month);
        $("#description").val(filters.description);
        $("#master_type").val(filters.master_type);
        $("#master").val(filters.master);
        $("#owner").val(filters.owner);
        $("#phone").val(filters.phone);
        $("#status").val(filters.status);
        $("#completed").val(filters.completed);
        $("#active").val(filters.active);
        $("#ownerName").val(filters.ownerName);
        $("#buildingName").val(filters.buildingName);
        $("#isCompleted").val(filters.isCompleted);
        $("#incomeExpenseItem").val(filters.incomeExpenseItem);
        $("#incomeExpenseType").val(filters.incomeExpenseType);

    }

}

function onlyUnique(value, index, array) {
  return array.indexOf(value) === index;
}

//AJAX-вызов и получение данных по url
function getTableData(url, pageNumber, pageSize, pageFiltersString) {
    let tp = /*[[${totalPagesCount}]]*/ 1
    console.log('THYMELEAF ' + tp);
    $.ajax(url, {
        async: false,
        data: {page:pageNumber, size:pageSize, filters:pageFiltersString},
        success: function(data) {
                         console.log('Returned following data from Controller:');
                         console.log(data);
                         tableData = data;
                         totalPagesCount = data.totalPages;
                     }
    }).then(function(){
        console.log('table data is: ');
        console.log(tableData);
    });
    return tableData;

}

//Функции, рисующие таблицы в зависимости от выбранной страницы
function drawApartmentsTable() {

    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/apartments/get-apartments-page', currentPageNumber, currentPageSize, pageFiltersString);
    let $apartmentsTable = $("#apartmentsTable tbody");
    $apartmentsTable.html('');
    for(const apartment of data.content) {
        console.log("apartment" + apartment);
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add = 'apartment_row';
        let buildingName = (apartment.building) ? apartment.building.name : '';
        let ownerName = (apartment.owner) ? apartment.owner.fullName : '';
        newTableRow.innerHTML = '<td>' + apartment.number + '</td>' +
                            '<td>' + buildingName + '</td>' +
                            '<td>' + apartment.section + '</td>' +
                            '<td>' + apartment.floor + '</td>' +
                            '<td>' + ownerName + '</td>' +
                            '<td>' + apartment.balance + '</td>' +
                            '<td>' +
                                '<div class="btn-group" role="group" aria-label="Basic outlined button group">' +
                                    '<a href="edit/'+apartment.id+'" class="btn btn-default btn-sm"><i class="fa fa-pencil aria-hidden="true"></i></i></a>' +
                                    '<button onclick="deleteApartment(this)" data-url="delete/'+apartment.id+'" class="btn btn-default btn-sm"><i class="fa fa-trash-o" aria-hidden="true"></i></i></a>' +
                                '</div>' +
                            '</td>';
        let row_children = newTableRow.children;
        for(let j = 1; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/apartments/'+apartment.id;
            });
        }

        $apartmentsTable.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=7>Ничего не найдено...</td>';
        $apartmentsTable.append(newTableRow);
    }

    drawPagination();

}
function drawInvoicesTable(){

    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/invoices/get-invoices', currentPageNumber, currentPageSize, pageFiltersString);
    let $invoicesTableBody = $("#invoicesTable tbody");
    $invoicesTableBody.html('');

    for(const invoice of data.content) {
        let date = new Date(invoice.date);
        date.setDate(date.getDate() + 1);
        const monthNames = ["January", "February", "March", "April", "May", "June",
          "July", "August", "September", "October", "November", "December"
        ];
        let month_string = monthNames[date.getMonth()] + ' ' + date.getFullYear();
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add('invoice_row');
        newTableRow.innerHTML = '<input type="hidden" value=' + invoice.id.toString() + '>' +
                                '<td><input type="checkbox" name="" id=""></td>' +
                                '<td>' + invoice.id.toString().padStart(10, '0') + '</td>' +
                                '<td>' +
                                    '<small class="label ' + (invoice.status == 'PAID' ? 'label-success' : invoice.status == 'UNPAID' ? 'label-danger' : 'label-warning') + '">' +
                                    invoice.statusName +
                                    '</small>' +
                               '</td>' +
                                '<td>' + date.toISOString().split('T')[0] + '</td>' +
                                '<td>' + month_string + '</td>' +
                                '<td>' + invoice.apartment.fullName + '</td>' +
                                '<td>' + invoice.owner.fullName + '</td>' +
                                '<td><span>' + (invoice.completed ? 'Проведена' : 'Не проведена') + '</span></td>' +
                                '<td><span>' + invoice.total_price + '</span></td>' +
                                '<td>'+
                                    '<div class="btn-group pull-right">' +
                                        '<a class="btn btn-default btn-sm" href="/myhome/admin/invoices/update/' + invoice.id + '"><i class="fa fa-pencil"></i></a>' +
                                        '<a class="btn btn-default btn-sm"' +
                                           'data-url="/myhome/admin/invoices/delete/' + invoice.id + '"' +
                                           'onclick="if(confirm(\'Удалить квитанцию?\')) window.location.href=this.dataset.url"><i class="fa fa-trash"></i></a>' +
                                    '</div>' +
                                '</td>';
        let row_children = newTableRow.children;
        for(let j = 2; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/invoices/'+invoice.id;
            });
        }

        $invoicesTableBody.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=10>'+notFoundText+'</td>';
        $invoicesTableBody.append(newTableRow);
    }

    drawPagination();

}
function drawMessagesTableCabinet(){
    console.log('msg draw table start');
    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/cabinet/get-messages', currentPageNumber, currentPageSize, pageFiltersString);
    let $invoicesTableBody = $("#messageTable tbody");
    $invoicesTableBody.html('');

    for(const msg of data.content) {

        let dateString = msg.date.toString();
        let dateParts = dateString.split(",");
        let year = parseInt(dateParts[0]);
        let month = parseInt(dateParts[1]) - 1;
        let day = parseInt(dateParts[2]);
        let hours = parseInt(dateParts[3]);
        let minutes = parseInt(dateParts[4]);
        let date = new Date(year, month, day, hours, minutes);
        let formattedDate = ("0" + date.getDate()).slice(-2) + "." + ("0" + (date.getMonth() + 1)).slice(-2) + "." + date.getFullYear() + " " + ("0" + date.getHours()).slice(-2) + ":" + ("0" + date.getMinutes()).slice(-2);

        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add = 'invoice_row';
        var text = msg.text;
        let strippedStr = text.replace(/(<([^>]+)>)/gi, "");
        var newText = strippedStr.substring(0, 70);
        if (newText.length == 70) {newText +='...' }
        newTableRow.innerHTML =
                                '<td><input type="checkbox" name="" id="" value="'+msg.id+'"></td>' +
                                '<td>' + msg.sender.first_name + ' ' + msg.sender.last_name + '</td>' +
                                '<td><strong>' + msg.subject + '</strong> - ' + newText + '</td>' +
                                '<td>' + formattedDate + '</td>';
        let row_children = newTableRow.children;
        for(let j = 1; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/cabinet/messages/'+msg.id;
            });
        }
        $invoicesTableBody.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=10>Ничего не найдено...</td>';
        $invoicesTableBody.append(newTableRow);
    }

    drawPagination();

}
function drawMessagesTableAdmin() {
    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/messages/get-messages', currentPageNumber, currentPageSize, pageFiltersString);
    let $invoicesTableBody = $("#messageTable tbody");
    $invoicesTableBody.html('');

    for (const msg of data.content) {
        let dateString = msg.date.toString();
        let dateParts = dateString.split(",");
        let year = parseInt(dateParts[0]);
        let month = parseInt(dateParts[1]) - 1;
        let day = parseInt(dateParts[2]);
        let hours = parseInt(dateParts[3]);
        let minutes = parseInt(dateParts[4]);
        let date = new Date(year, month, day, hours, minutes);
        let formattedDate = ("0" + date.getDate()).slice(-2) + "." + ("0" + (date.getMonth() + 1)).slice(-2) + "." + date.getFullYear() + " " + ("0" + date.getHours()).slice(-2) + ":" + ("0" + date.getMinutes()).slice(-2);

        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add = 'invoice_row';
        var text = msg.text;
        let strippedStr = text.replace(/(<([^>]+)>)/gi, "");
        var newText = strippedStr.substring(0, 70);
        if (newText.length == 70) {newText +='...' }
        newTableRow.innerHTML =
            '<td><input type="checkbox" name="" id="" value="' + msg.id + '"></td>' +
            '<td>' + msg.receiversName + '</td>' +
            '<td><strong>' + msg.subject + '</strong> - ' + newText + '</td>' +
            '<td>' + formattedDate + '</td>';
        let row_children = newTableRow.children;
        for (let j = 1; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function () {
                window.location.href = '/myhome/admin/messages/' + msg.id;
            });
        }
        $invoicesTableBody.append(newTableRow);
    }
    if (data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=10>Ничего не найдено...</td>';
        $invoicesTableBody.append(newTableRow);
    }
    drawPagination();
}
function drawInvoicesInCabinetTable(){

    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/cabinet/get-invoices-cabinet', currentPageNumber, currentPageSize, pageFiltersString);
    let $invoicesTableBody = $("#invoicesTable tbody");
    $invoicesTableBody.html('');

    for(const invoice of data.content) {
        let date = new Date(invoice.date);
        date.setDate(date.getDate() + 1);
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add = 'invoice_row';
        newTableRow.innerHTML =
            '<td>' + invoice.id.toString().padStart(10, '0') + '</td>' +
            '<td>' + date.toISOString().split('T')[0] + '</td>' +
            '<td>' +
            '<small class="label ' + (invoice.status == 'PAID' ? 'label-success' : invoice.status == 'UNPAID' ? 'label-danger' : 'label-warning') + '">' +
            invoice.status +
            '</small>' +
            '</td>' +
            '<td><span>' + invoice.total_price.toString() + '</span></td>';
        let row_children = newTableRow.children;
        for(let j = 1; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/cabinet/invoice/'+invoice.id;
            });
        }

        $invoicesTableBody.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=10>Ничего не найдено...</td>';
        $invoicesTableBody.append(newTableRow);
    }

    drawPagination();

}
function drawAccountsTable(){
    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/accounts/get-accounts', currentPageNumber, currentPageSize, pageFiltersString);
    let $accountsTableBody = $("#accountsTable tbody");
    $accountsTableBody.html('');

    for(const account of data.content) {

        let apartName = (account.apartment.fullName) ? account.apartment.fullName : '';
        let buildingName = (account.building.name) ? account.building.name : '';
        let ownerName = (account.owner.fullName) ? account.owner.fullName : '';

        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add = 'account_row';
        newTableRow.innerHTML = '<td>' + account.id.toString().padStart(10,'0') + '</td>' +

                                '<td>' +
                                    '<small class="label ' + ((account.isActive) ? 'label-success' : 'label-danger') + '">' +
                                                             ((account.isActive) ? activeText : inactiveText) + '</small>' +
                                '</td>' +
                                '<td>' + apartName + '</td>' +
                                '<td>' + buildingName + '</td>' +
                                '<td>' + ((account.section !== null && account.section != '0') ? account.section : '') + '</td>' +
                                '<td>' + ownerName + '</td>' +

                                '<td style="color:' + ((account.balance > 0) ? 'green' : ((account.balance < 0) ? 'red' : 'black')) + '" >' + account.balance + '</td>' +
                                '<td>' +
                                    '<div class="btn-group pull-right">' +
                                        '<a class="btn btn-default btn-sm" href="/myhome/admin/accounts/update/' + account.id + '"><i class="fa fa-pencil"></i></a>' +
                                        '<a class="btn btn-default btn-sm" data-url="/myhome/admin/accounts/delete/'+ account.id + '"' +
                                           'onclick="if(confirm(\'Удалить лицевой счёт?\')) window.location.href=this.dataset.url"><i class="fa fa-trash"></i></a>' +
                                    '</div>' +
                                '</td>' +
                                '<input type="hidden" value=' + account.id + '>';
        let row_children = newTableRow.children;
        for(let j = 0; j < row_children.length - 2; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/accounts/'+account.id;
            });
        }

        $accountsTableBody.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=8>Ничего не найдено...</td>';
        $accountsTableBody.append(newTableRow);
    }

    drawPagination();
}
function drawMetersTable(){
    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/meters/get-meters', currentPageNumber, currentPageSize, pageFiltersString);
    let $metersTable = $("#metersTable tbody");
    $metersTable.html('');
    for(const meter of data.content) {
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add = 'meter_row';
        newTableRow.innerHTML =   '<td><a href="/myhome/admin/buildings/' + meter.buildingID + '">' + meter.buildingName + '</a></td>' +
                                  '<td>' + meter.section + '</td>' +
                                  '<td>' + meter.apartmentNumber + '</td>' +
                                  '<td>' + meter.serviceName + '</td>' +
                                  '<td style="background-color: #DFD5; font-weight:bold; text-decoration:underline">' + meter.readings + '</td>' +
                                  '<td style="background-color: #DFD5; font-weight:bold; text-decoration:underline">' + meter.serviceUnitName + '</td>' +
                                  '<td>' +
                                      '<div class="btn-group pull-right">' +
                                          '<a class="btn btn-default btn-sm" href="/myhome/admin/meters/create-add?flat_id=' + meter.apartmentID + '&service_id=' + meter.serviceID + '" title="Снять новое показание счетчика" target="_blank" data-toggle="tooltip"><i class="fa fa-dashboard"></i></a>' +
                                          '<a class="btn btn-default btn-sm" href="/myhome/admin/meters/data?flat_id=' + meter.apartmentID + '&service_id=' + meter.serviceID + '" title="Открыть историю показаний для счетчика" data-toggle="tooltip"><i class="fa fa-eye"></i></a>' +
                                      '</div>' +
                                  '</td>';
        let row_children = newTableRow.children;
        for(let j = 0; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/meters/data?flat_id='+meter.apartmentID+'&service_id='+meter.serviceID;
            });
        }

        $metersTable.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=7>Ничего не найдено...</td>';
        $metersTable.append(newTableRow);
    }
    drawPagination();
}
function drawMeterDataTable(){
    let params = new URL(document.location).searchParams;
    let apartment_id = params.get("flat_id");
    let service_id = params.get("service_id");
    let singleMeterFilters = {
        apartment:apartment_id,
        service:service_id,
        id:$("#id").val(),
        status:$("#status").val(),
        date:$("#datetime").val()
    };

    let pageFiltersString = JSON.stringify(singleMeterFilters);
    let data = getTableData('/myhome/admin/meters/get-meter-data', currentPageNumber, currentPageSize, pageFiltersString);

    let $metersTable = $("#metersTable tbody");
    $metersTable.html('');
    for(const meter of data.content) {
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add = 'meter_row';
        let meter_date = new Date(meter.date);
        let meter_month = '' + (meter_date.getMonth()+1) + '-' + meter_date.getFullYear();
        newTableRow.innerHTML =   '<td>' + meter.id + '</td>' +
                                  '<td>' + meter.status + '</td>' +
                                  '<td>' + meter_date.toISOString().split('T')[0] + '</td>' +
                                  '<td>' + meter_month + '</td>' +
                                  '<td>' + meter.buildingName + '</td>' +
                                  '<td>' + meter.section + '</td>' +
                                  '<td>' + meter.apartmentNumber + '</td>' +
                                  '<td>' + meter.serviceName + '</td>' +
                                  '<td style="background-color: #DFD5; font-weight:bold; text-decoration:underline">' + meter.readings + '</td>' +
                                  '<td style="background-color: #DFD5; font-weight:bold; text-decoration:underline">' + meter.serviceUnitName + '</td>' +
                                  '<td>' +
                                      '<div class="btn-group pull-right">' +
                                          '<a class="btn btn-default btn-sm" href="/myhome/admin/meters/create-add?flat_id=' + meter.apartmentID + '&service_id=' + meter.serviceID + '" title="Снять новое показание счетчика" target="_blank" data-toggle="tooltip"><i class="fa fa-dashboard"></i></a>' +
                                          '<a class="btn btn-default btn-sm" href="/myhome/admin/meters/data?flat_id=' + meter.apartmentID + '&service_id=' + meter.serviceID + '" title="Открыть историю показаний для счетчика" data-toggle="tooltip"><i class="fa fa-eye"></i></a>' +
                                      '</div>' +
                                  '</td>';
        let row_children = newTableRow.children;
        for(let j = 0; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/meters/info/' + meter.id;
            });
        }

        $metersTable.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=11>' + notFoundText + '</td>';
        $metersTable.append(newTableRow);
    }

    drawPagination();
}
function drawRequestsTable() {
    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/requests/get-requests', currentPageNumber, currentPageSize, pageFiltersString);

    let $requestsTable = $("#requestsTable tbody");
    $requestsTable.html('');
    for(const request of data.content) {
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add = 'request_row';
        newTableRow.innerHTML =   '<td>' + request.id + '</td>' +
                                  '<td>' + request.best_time + '</td>' +
                                  '<td>' + request.masterTypeName + '</td>' +
                                  '<td style="max-width: 200px; text-overflow: ellipsis; white-space: nowrap; overflow:hidden">' + request.description + '</td>' +
                                  '<td><a href="/myhome/admin/apartments/' + request.apartmentID +'">кв. ' + request.apartmentNumber + ', ' + request.apartmentBuildingName + '</a></td>' +
                                  '<td><a href="/myhome/admin/owners/' + request.ownerID + '">' + request.ownerFullName + '</a></td>' +
                                  '<td>' + request.ownerPhoneNumber + '</td>' +
                                  '<td><a href="/myhome/admin/admins/' + request.masterID + '">' + ((request.masterFullName != null) ? request.masterFullName : '') + '</a></td>' +
                                  '<td><small class="label ' + ((request.statusName === 'Новое') ? 'label-primary' : (request.statusName === 'В работе') ? 'label-warning' : 'label-success') + '">' + request.statusName + '</small></td>' +
                                  '<td>' +
                                      '<div class="btn-group pull-right">' +
                                          '<a class="btn btn-default btn-sm" href="/myhome/admin/requests/update/' + request.id + '"><i class="fa fa-pencil" aria-hidden="true"></i></a>' +
                                          '<a class="btn btn-default btn-sm" href="/myhome/admin/requests/delete/' + request.id + '"><i class="fa fa-trash" aria-hidden="true"></i></a>' +
                                      '</div>' +
                                  '</td>';
        let row_children = newTableRow.children;
        for(let j = 0; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/requests/info/' + request.id;
            });
        }

        $requestsTable.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=10>'+notFoundText+'</td>';
        $requestsTable.append(newTableRow);
    }
    drawPagination();
}
function drawRequestsTableCabinet() {

    let pageFiltersString = '';
    let data = getTableData('/cabinet/get-requests', currentPageNumber, currentPageSize, pageFiltersString);

    let $requestsTable = $("#requestsTable tbody");
    $requestsTable.html('');
    for(const request of data.content) {
        let date = new Date(request.best_time.split('T')[0]);
        let day = date.getDate().toString().padStart(2, '0'); // получаем день месяца, преобразуем в строку и добавляем нуль в начало, если число состоит из одной цифры
        let month = (date.getMonth() + 1).toString().padStart(2, '0'); // получаем номер месяца (от 0 до 11), прибавляем 1, преобразуем в строку и добавляем нуль в начало, если число состоит из одной цифры
        let year = date.getFullYear().toString(); // получаем год в виде четырехзначного числа
        let hours = date.getHours().toString().padStart(2, '0'); // получаем часы, преобразуем в строку и добавляем нуль в начало, если число состоит из одной цифры
        let minutes = date.getMinutes().toString().padStart(2, '0'); // получаем минуты, преобразуем в строку и добавляем нуль в начало, если число состоит из одной цифры
        let formattedDate = `${day}.${month}.${year} ${hours}:${minutes}`;
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add('request_row');

        newTableRow.innerHTML =   '<td>' + request.id + '</td>' +
            '<td>' + request.master_type + '</td>' +
            '<td style="max-width: 200px; text-overflow: ellipsis; white-space: nowrap; overflow:hidden">' + request.description + '</td>' +
            '<td>' + formattedDate + '</td>' +
            '<td><small class="label ' + ((request.status === 'Новое') ? 'label-primary' : (request.status === 'В работе') ? 'label-warning' : 'label-success') + '">' + request.status + '</small></td>' +
            '<td>' +
            '<div class="btn-group pull-right">' +
            '<a class="btn btn-default btn-sm" href="/cabinet/requests/delete/' + request.id + '"><i class="fa fa-trash" aria-hidden="true"></i></a>' +
            '</div>' +
            '</td>';

        $requestsTable.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=10>Ничего не найдено...</td>';
        $requestsTable.append(newTableRow);
    }
    drawPagination();
}
function drawOwnersTable(){
    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/owners/get-owners', currentPageNumber, currentPageSize, pageFiltersString);
    let $ownersTable = $("#ownersTable tbody");
    $ownersTable.html('');
    for(const owner of data.content) {
        const ownerBuildings = [];
        let map = new Map();
        for(const building of owner.buildings) {
            if(!map.has(building)) ownerBuildings.push(building);
            map.set(building,building);
        }
        const buildingLinks = ownerBuildings.map(function(buildingObject) {return ('<p style="margin:0"><a href="/myhome/admin/buildings/' + buildingObject.id + '">' + buildingObject.name + '</a></p>')});
        const finalBuildingString = buildingLinks.join('');
        const apartmentLinks = owner.apartments.map(function(apartmentObject) {return ('<p style="margin:0; font-size:13px"><a href="/myhome/admin/apartments/' + apartmentObject.id + '">' + apartmentObject.fullName + '</a></p>')});
        const finalApartmentString = apartmentLinks.join('');

        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add('owner_row');
        newTableRow.innerHTML =   '<td>' + owner.id + '</td>' +
                                  '<td>' + owner.fullName + '</td>' +
                                  '<td>' + owner.phone_number + '</td>' +
                                  '<td>' + owner.email + '</td>' +
                                  '<td>' + finalBuildingString + '</td>' +
                                  '<td>' + finalApartmentString + '</td>' +
                                  '<td>' + owner.date + '</td>' +
                                  '<td>' + owner.status + '</td>' +
                                  '<td>' + ((owner.hasDebt) ? hasDebtText : hasNoDebtText) + '</td>' +
                                  '<td>' +
                                      '<div class="btn-group" role="group" aria-label="Basic outlined button group">' +
                                          '<a href="newTo/'+ owner.id + '" class="btn btn-default btn-sm"><i class="fa fa-envelope" aria-hidden="true"></i></a>' +
                                          '<a href="edit/'+ owner.id + '" class="btn btn-default btn-sm"><i class="fa fa-pencil" aria-hidden="true"></i></a>' +
                                          '<button onclick="deleteOwner(this)" data-url="delete/'+ owner.id + '" class="btn btn-default btn-sm"><i class="fa fa-trash-o" aria-hidden="true"></i></button>' +
                                      '</div>' +
                                  '</td>';

        let row_children = newTableRow.children;
        for(let j = 0; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/owners/' + owner.id;
            });
        }

        $ownersTable.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=10>Ничего не найдено...</td>';
        $ownersTable.append(newTableRow);
    }
    drawPagination();
}
function drawAdminsTable(){
    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/admins/get-admins', currentPageNumber, currentPageSize, pageFiltersString);
    let $adminsTable = $("#adminsTable tbody");
    $adminsTable.html('');
    console.log(data);
    for(const admin of data.content) {
        console.log(admin);
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add('user-row');
        newTableRow.innerHTML =   '<td>' + admin.id + '</td>' +
            '<td>' + ((admin.fullName) ? admin.fullName : notFoundText) + '</td>' +
            '<td>' + ((admin.role) ? admin.role : notFoundText) + '</td>' +
            '<td>' + ((admin.phone_number) ? admin.phone_number : notFoundText) + '</td>' +
            '<td>' + ((admin.email) ? admin.email : notFoundText) + '</td>' +
            '<td>' + ((admin.active) ? activeText : inactiveText) + '</td>' +
            '<td>' +
                '<div class="btn-group" role="group" aria-label="Basic outlined button group">' +
                '<button onclick="alert("Приглашение отправлено!")" class="btn btn-default btn-sm invite_button" title="Отправить приглашение"><i class="fa fa-repeat"></i></button>' +
                '<a href="/myhome/admin/admins/update/'+ admin.id + '" class="btn btn-default btn-sm"><i class="fa fa-pencil" aria-hidden="true"></i></i></a>' +
                '<a href="/myhome/admin/admins/delete/'+ admin.id + '" class="btn btn-default btn-sm"><i class="fa fa-trash-o" aria-hidden="true"></i></i></a>' +
                '</div>' +
            '</td>';

        let row_children = newTableRow.children;
        for(let j = 0; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/admins/' + admin.id;
            });
        }

        $adminsTable.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=10>Ничего не найдено...</td>';
        $adminsTable.append(newTableRow);
    }
    drawPagination();
}

function drawBuildingsTable() {

    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/buildings/get-buildings-page', currentPageNumber, currentPageSize, pageFiltersString);
    let $buildingsTable = $("#buildingsTable tbody");
    $buildingsTable.html('');
    for(const building of data.content) {
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add('building_row');
        newTableRow.innerHTML =   '<td>' + building.id + '</td>' +
                                  '<td>' + building.name + '</td>' +
                                  '<td>' + building.address + '</td>' +
                                  '<td>' +
                                      '<div class="btn-group" role="group" aria-label="Basic outlined button group">' +
                                          '<a href="edit/' + building.id +'" class="btn btn-default btn-sm"><i class="fa fa-pencil" aria-hidden="true"></i></i></a>' +
                                          '<button onclick="deleteBuilding(this)" data-url="delete/' + building.id +'" class="btn btn-default btn-sm"><i class="fa fa-trash-o" aria-hidden="true"></i></i></a>' +
                                      '</div>' +
                                  '</td>';
        let row_children = newTableRow.children;
        for(let j = 0; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/buildings/' + building.id;
            });
        }

        $buildingsTable.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=4>Ничего не найдено...</td>';
        $buildingsTable.append(newTableRow);
    }
    drawPagination();

}
function drawTransactionsTable() {

    let pageFiltersString = JSON.stringify(gatherFilters());
    let data = getTableData('/myhome/admin/cashbox/get-cashbox-page', currentPageNumber, currentPageSize, pageFiltersString);
    let $cashboxTable = $("#cashboxTable tbody");
    $cashboxTable.html('');
    console.log('data ' + data)
    console.log('data.content ' + data.content)
    for(const cashbox of data.content) {
        let date = new Date(cashbox.date);
        let newTableRow = document.createElement('tr');
        newTableRow.style.cursor = 'pointer';
        newTableRow.classList.add('cashbox_row');
        let incomeExpenseItem = (cashbox.incomeExpenseItems) ? cashbox.incomeExpenseItems : notFoundText;
        let ownerFullName = (cashbox.ownerFullName) ? cashbox.ownerFullName : notFoundText;
        let apartmentAccount = (cashbox.apartmentAccount) ? cashbox.apartmentAccount : notFoundText;
        newTableRow.innerHTML =   '<td>' + cashbox.id.toString().padStart(10,'0') + '</td>' +
                                  '<td>' + date.toISOString().split('T')[0] + '</td>' +
                                  '<td>' + ((cashbox.completed) ? 'Проведен' : 'Не проведен') + '</td>' +
                                  '<td>' + incomeExpenseItem + '</td>' +
                                  '<td>' + ownerFullName + '</td>' +
                                  '<td>' + apartmentAccount + '</td>' +
                                  '<td style="color: ' + (cashbox.amount > 0 ? 'green' : (cashbox.amount < 0 ? 'red' : 'black')) + '">' + cashbox.incomeExpenseType + '</td>' +
                                  '<td style="color: ' + (cashbox.amount > 0 ? 'green' : (cashbox.amount < 0 ? 'red' : 'black')) + '">' + cashbox.amount + '</td>' +
                                  '<td>' +
                                      '<div class="btn-group" role="group" aria-label="Basic outlined button group">' +
                                          '<a href="/myhome/admin/cashbox/edit/' + cashbox.id + '" class="btn btn-default btn-sm"><i class="fa fa-pencil" aria-hidden="true"></i></a>' +
                                          '<a href="/myhome/admin/cashbox/delete/' + cashbox.id + '" class="btn btn-default btn-sm"><i class="fa fa-trash" aria-hidden="true"></i></a>' +
                                      '</div>' +
                                  '</td>';
        let row_children = newTableRow.children;
        for(let j = 0; j < row_children.length - 1; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/cashbox/' + cashbox.id;
            });
        }

        $cashboxTable.append(newTableRow);
    }
    if(data.content.length === 0) {
        let newTableRow = document.createElement('tr');
        newTableRow.innerHTML = '<td style="text-align:center" colspan=9>Ничего не найдено...</td>';
        $cashboxTable.append(newTableRow);
    }
    drawPagination();

}

function drawTable() {

    let tableType = globalTableName;
    if(tableType === 'invoices') drawInvoicesTable();
    else if(tableType === 'invoicesInCabinet') drawInvoicesInCabinetTable();
    else if(tableType === 'apartments') drawApartmentsTable();
    else if(tableType === 'accounts') drawAccountsTable();
    else if(tableType === 'meters') drawMetersTable();
    else if(tableType === 'meter_data') drawMeterDataTable();
    else if(tableType === 'requests') drawRequestsTable();
    else if(tableType === 'requestsCabinet') drawRequestsTableCabinet();
    else if(tableType === 'owners') drawOwnersTable();
    else if(tableType === 'buildings') drawBuildingsTable();
    else if(tableType === 'transactions') drawTransactionsTable();
    else if(tableType === 'messagesCabinet') drawMessagesTableCabinet();
    else if(tableType === 'messagesAdmin') drawMessagesTableAdmin();
    else if(tableType === 'admins') drawAdminsTable();
}
//Функции, рисующие таблицы в зависимости от выбранной страницы

//Функции, перерисовывающие таблицы после изменений фильтров/номера страниц/размера страниц
function changeFilterData() {
    pageFiltersString = gatherFilters();
    setFilters(pageFiltersString);
    saveState();
    drawTable();
}
function changePageNumber(newPageNumber) {
    currentPageNumber = newPageNumber;
    saveState();
    drawTable();
}
function increasePageByOne() {
    if(currentPageNumber < totalPagesCount) currentPageNumber++;
    saveState();
    drawTable();
}
function decreasePageByOne() {
    if(currentPageNumber > 0) currentPageNumber--;
    saveState();
    drawTable();
}
function changePageSize(newPageSize) {
    currentPageSize = newPageSize;
    currentPageNumber = 1;
    $(".page-size-display").text(newPageSize);
    drawPagination();

    saveState();
    drawTable();
}
function saveState() {
    let pageState = {
        page: currentPageNumber,
        size: currentPageSize,
        filters: pageFiltersString
    };

    history.replaceState(pageState, null, window.location.href);
    console.log(pageState);
}
function reset() {
    setFilters(null);
    currentPageNumber = 1;
    currentPageSize = 10;
    saveState();
    drawTable();
}
//Функции, перерисовывающие таблицы после изменений фильтров/номера страниц/размера страниц

//Отрисовка пагинации
function drawPagination() {

    let pageOffset = 2; // 1 ... 3 4 -5- 6 7 ... 10 -  current page 5 , offset 2

    let $pagination = $(".pagination_container");
    $pagination.html('');

    if(tableData.totalElements === 0) return;

    let totalElements = tableData.totalElements;
    console.log(totalElements);

    let totalElementsOnCurrentPage = ((currentPageSize*currentPageNumber) < totalElements) ? currentPageSize : (currentPageSize - ((currentPageSize*currentPageNumber)-totalElements))
    console.log(totalElements);
    console.log(totalElementsOnCurrentPage);

    //ADD TEXT
    let span = document.createElement('span');
    span.innerText = `Показано ${currentPageSize*(currentPageNumber-1)+1} - ` +
    `${currentPageSize*(currentPageNumber-1)+totalElementsOnCurrentPage} из ${totalElements} записей`;
    $pagination.append(span);

    if(totalPagesCount <= 1) return;

    let ul = document.createElement('ul');
    ul.classList.add('pagination', 'justify-content-center', 'font-weight-medium');

    //backward navigation buttons (to first page)
    let li = document.createElement('li');
    li.classList.add('page-item');
    if(currentPageNumber === 1) li.classList.add('disabled');
    li.innerHTML = '<a class="page-link" href="#" onclick="changePageNumber(1)" aria-label="Previous"> <span aria-hidden="true">«</span></a>';
    ul.appendChild(li);

    //backward navigation buttons (-1)
    li = document.createElement('li');
    li.classList.add('page-item');
    if(currentPageNumber === 1) li.classList.add('disabled');
    li.innerHTML = '<a class="page-link" href="#" onclick="decreasePageByOne()" aria-label="Previous"> <span aria-hidden="true">‹</span></a>';
    ul.appendChild(li);

    //first page
    li = document.createElement('li');
    li.classList.add('page-item');
    if(currentPageNumber === 1) li.classList.add('active');
    li.innerHTML = '<a class="page-link" href="#" onclick="changePageNumber(1)" aria-label="Previous"> <span aria-hidden="true">1</span></a>';
    ul.appendChild(li);

    // ... block
    if(totalPagesCount > 2*pageOffset + 5 && currentPageNumber - pageOffset - 1 > 2) {
        li = document.createElement('li');
        li.classList.add('page-item');
        li.classList.add('disabled');
        li.innerHTML = '<a class="page-link" href="#">...</a>';
        ul.appendChild(li);
    }

    //main pages
    let startOfSequence;
    let endOfSequence;

    if(totalPagesCount <= 2*pageOffset+5) {
        startOfSequence = 2;
        endOfSequence = totalPagesCount-1;
    } else {
        startOfSequence = (currentPageNumber > totalPagesCount - 3 - pageOffset + 1) ? totalPagesCount - 3 - 2*pageOffset + 1 :
                            (currentPageNumber - pageOffset - 1 > 2) ? currentPageNumber - pageOffset : 2;
        endOfSequence = (currentPageNumber < 3 + pageOffset) ? 3 + 2*pageOffset :
                        (totalPagesCount - currentPageNumber - pageOffset > 2) ? currentPageNumber + pageOffset : totalPagesCount-1
    }

    for(let page = startOfSequence; page <= endOfSequence; page++) {
        li = document.createElement('li');
        li.classList.add('page-item');
        if(currentPageNumber === page) li.classList.add('active');
        li.innerHTML = '<a class="page-link" href="#" onclick="changePageNumber('+page+')" aria-label="Previous"> <span aria-hidden="true">'+page+'</span></a>';
        ul.appendChild(li);
    }

    // ... block
    if(totalPagesCount > 2*pageOffset + 5 && totalPagesCount - currentPageNumber - pageOffset > 2) {
        li = document.createElement('li');
        li.classList.add('page-item');
        li.classList.add('disabled');
        li.innerHTML = '<a class="page-link" href="#">...</a>';
        ul.appendChild(li);
    }

    //last page
    if(totalPagesCount > 1) {
        li = document.createElement('li');
        li.classList.add('page-item');
        if(currentPageNumber === totalPagesCount) li.classList.add('active');
        li.innerHTML = '<a class="page-link" href="#" onclick="changePageNumber('+totalPagesCount+'); drawTable()" aria-label="Previous"> <span aria-hidden="true">'+totalPagesCount+'</span></a>';
        ul.appendChild(li);

    }

    //forward navigation buttons (+1)
    li = document.createElement('li');
    li.classList.add('page-item');
    if(currentPageNumber === totalPagesCount || totalPagesCount === 0) li.classList.add('disabled');
    li.innerHTML = '<a class="page-link" href="#" onclick="increasePageByOne()" aria-label="Previous"> <span aria-hidden="true">›</span></a>';
    ul.appendChild(li);

    //forward navigation buttons (to last page)
    li = document.createElement('li');
    li.classList.add('page-item');
    if(currentPageNumber === totalPagesCount || totalPagesCount === 0) li.classList.add('disabled');
    li.innerHTML = '<a class="page-link" href="#" onclick="changePageNumber(totalPagesCount)" aria-label="Previous"> <span aria-hidden="true">››</span></a>';
    ul.appendChild(li);

    //page size changer
    let sizeChanger = document.createElement('div');
    sizeChanger.classList.add('btn-group');
    sizeChanger.innerHTML = '<button type="button"' +
                                    'class="btn btn-primary btn-sm dropdown-toggle page-size-display" data-bs-toggle="dropdown"' +
                                    'aria-expanded="false">' + currentPageSize +
                            '</button>' +
                            '<ul class="dropdown-menu dropdown-menu-end">' +
                                '<li><a class="dropdown-item" href="#" onclick="changePageSize(1)">1</a></li>' +
                                '<li><a class="dropdown-item" href="#" onclick="changePageSize(10)">10</a></li>' +
                                '<li><a class="dropdown-item" href="#" onclick="changePageSize(25)">25</a></li>' +
                                '<li><a class="dropdown-item" href="#" onclick="changePageSize(50)">50</a></li>' +
                                '<li><a class="dropdown-item" href="#" onclick="changePageSize(100)">100</a></li>' +
                            '</ul>'
    ul.appendChild(sizeChanger);

    $pagination.append(ul);
}

//Удаление сущностей
function deleteOwner(button) {
    let url = button.dataset.url;
    if(confirm('Удалить пользователя?')) {
        window.location.href="/myhome/admin/owners/"+url;
    }
}
function deleteBuilding(button) {
    let url = button.dataset.url;
    if(confirm('Удалить дом?')) {
        window.location.href="/myhome/admin/buildings/"+url;
    }
}
function deleteApartment(button) {
    let url = button.dataset.url;
    if(confirm('Удалить квартиру?')) {
        window.location.href="/myhome/admin/apartments/"+url;
    }
}
//Удаление сущностей

//Установка слушателей на фильтры
$(document).ready(function(){

    $(".my_filters").change(() => changeFilterData());
    $(".datetime_filter").change(function(){
        console.log('date changed');
        let datetime = this.value;
        console.log(datetime);
        if(datetime.split(' to ').length > 1) changeFilterData();
    });

});
