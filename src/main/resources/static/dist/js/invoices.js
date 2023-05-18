function deleteChosen(){
        if(confirm('Удалить выбранные квитанции?')) {
            for(const row of document.querySelectorAll(".invoice_row")) {
                console.log(row);
                console.log(row.querySelector("input[type=checkbox]"));
                if(row.querySelector("input[type=checkbox]").checked == true) {
                    let invoice_id = row.children[0].value;
                    console.log(invoice_id);
                    $.get('/myhome/admin/invoices/delete-invoice', {id:invoice_id}, function(data){drawTable();}).fail(function(){alert('Ошибка в удалении квитанций')});
                }
            }
        }
    }

function insertPreviousNumbers(page_number) {
        if(page_number-1 <= 1 || page_number === 1) return;

        if(page_number == pages_count) {
            btn = document.createElement("a");
            btn.classList.add("btn", "btn-default");
            url.searchParams.delete('page'); url.searchParams.append('page', (pageNumber-2));
            btn.href = url.toString();
            console.log(btn.href);
            btn.innerText = pageNumber-2;
            page_buttons_container.appendChild(btn);
        }

        btn = document.createElement("a");
        btn.classList.add("btn", "btn-default");
        url.searchParams.delete('page'); url.searchParams.append('page', (pageNumber-1));
        btn.href = url.toString();
        console.log(btn.href);
        btn.innerText = pageNumber-1;
        page_buttons_container.appendChild(btn);
    }

function insertNextNumbers(page_number) {
    if(page_number+1 >= pages_count || page_number === pages_count) return;
    btn = document.createElement("a");
    btn.classList.add("btn", "btn-default");
    url.searchParams.delete('page'); url.searchParams.append('page', (pageNumber+1));
    btn.href = url.toString();
    console.log(btn.href);
    btn.innerText = pageNumber+1;
    page_buttons_container.appendChild(btn);

    if(page_number == 1) {
        btn = document.createElement("a");
        btn.classList.add("btn", "btn-default");
        url.searchParams.delete('page'); url.searchParams.append('page', (pageNumber+2));
        btn.href = url.toString();
        btn.innerText = pageNumber+2;
        page_buttons_container.appendChild(btn);
    }
}

function insertNumber(page_number) {
    btn = document.createElement("a");
    btn.classList.add("btn", "btn-default", "selected");
    url.searchParams.delete('page'); url.searchParams.append('page', (pageNumber));
    btn.href = url.toString();
    btn.innerText = pageNumber;
    page_buttons_container.appendChild(btn);
}


    function gatherFilters() {

            let building_id = $("#building").val();
            let service_id = $("#service").val();
            let apartment_number = $("#apartment").val();
            let section_name = $("#section").val();

            let id = $("#id").val();
            let datetime = $("#datetime").val();
            let description = $("#description").val();
            let master_type = $("#master_type").val();
            let master_id = $("#master").val();
            let owner_id = $("#ownerId").val();
            let phone = $("#phone").val();
            let status = $("#status").val();

            let completed = $("#completed").val();

            console.log(apartment_number);

            let filterForm = {
                building: (building_id) ? building_id : null,
                service: (service_id) ? service_id : null,
                apartment: (apartment_number) ? apartment_number : null,
                section: (section_name) ? section_name : null,
                id: (id) ? id : null,
                datetime: (datetime) ? datetime : null,
                description: (description) ? description : null,
                master_type: (master_type) ? master_type : null,
                master: (master_id) ? master_id : null,
                owner: (owner_id) ? owner_id : null,
                phone: (phone) ? phone : null,
                status: (status) ? status : null,
                completed: (completed) ? completed : null
            };

            return filterForm;
        }

        function fillPageButtonsContainer(page_buttons_container, invoice_list_length) {

                invoices_amount = invoice_list_length;
                pages_count = Math.floor(invoices_amount / 5);

                page_buttons_container.innerHTML = '';

                let back_btn = document.createElement("a");
                back_btn.classList.add("btn", "btn-default");
                back_btn.innerText = ' < ';
                back_btn.addEventListener('click', () => switchPage(current_page_number - 1));

                page_buttons_container.appendChild(back_btn);

                for(let i = 0; i <= pages_count; i++) {
                    let btn = document.createElement("a");
                    btn.classList.add("btn", "btn-default");
                    if(i == current_page_number) btn.classList.add("selected");
                    btn.innerText = i+1;
                    btn.addEventListener('click', () => switchPage(i));
                    page_buttons_container.appendChild(btn);
                }

                let fwd_btn = document.createElement("a");
                fwd_btn.classList.add("btn", "btn-default");
                fwd_btn.innerText = ' > ';
                fwd_btn.addEventListener('click', () => switchPage(current_page_number + 1));

                page_buttons_container.appendChild(fwd_btn);
            }

            function setInvoicesForPage(page_number) {

                    let filterForm = JSON.stringify(gatherFilters());
                    console.log(filterForm);

                    $.get("/myhome/admin/invoices/get-invoices", {page:page_number, page_size:invoices_on_page, f_string:filterForm}, function(data){
                        $("#invoices_table tbody").html('');
                        for(const row of data) {
                            let date = new Date(row.date);
                            date.setDate(date.getDate() + 1);
                            const monthNames = ["January", "February", "March", "April", "May", "June",
                              "July", "August", "September", "October", "November", "December"
                            ];
                            let month_string = monthNames[date.getMonth()] + ' ' + date.getFullYear();
                            let table_row = document.createElement('tr');
                            table_row.style.cursor = 'pointer';
                            table_row.class = 'invoice_row';
                            table_row.innerHTML = '<input type="hidden" value=' + row.id.toString() + '>' +
                                    '<td><input type="checkbox" name="" id=""></td>' +
                                    '<td>' + row.id.toString().padStart(10, '0') + '</td>' +
                                    '<td>' +
                                        '<small class="label ' + (row.status == 'PAID' ? 'label-success' : row.status == 'UNPAID' ? 'label-danger' : 'label-warning') + '">' +
                                        row.status +
                                        '</small>' +
                                   '</td>' +
                                    '<td>' + date.toISOString().split('T')[0] + '</td>' +
                                    '<td>' + month_string + '</td>' +
                                    '<td>' + row.apartment.number.toString() + ', ' + row.apartment.building.name.toString() + '</td>' +
                                    '<td>' + row.apartment.owner.fullName.toString() + '</td>' +
                                    '<td><span>' + (row.completed ? 'Проведена' : 'Не проведена') + '</span></td>' +
                                    '<td><span>' + row.total_price.toString() + '</span></td>' +
                                    '<td>'+
                                        '<div class="btn-group pull-right">' +
                                            '<a class="btn btn-default btn-sm" href="/myhome/admin/invoices/update/' + row.id.toString() + '"><i class="fa fa-pencil"></i></a>' +
                                            '<a class="btn btn-default btn-sm"' +
                                               'data-url="/myhome/admin/invoices/delete/' + row.id.toString() + '"' +
                                               'onclick="if(confirm(\'Удалить квитанцию?\')) window.location.href=this.dataset.url"><i class="fa fa-trash"></i></a>' +
                                        '</div>' +
                                    '</td>';

                            let row_children = table_row.children;
                            let invoice_id = row.id;
                            for(let j = 2; j < row_children.length - 1; j++) {
                                row_children[j].addEventListener('click', function(){
                                    window.location.href = '/myhome/admin/invoices/'+invoice_id;
                                });
                            }

                            $("#invoices_table tbody").append(table_row);

                        }
                    });

                    $.get("/myhome/admin/invoices/get-filtered-invoice-count", {f_string:filterForm}, function(invoice_list_length) {
                        fillPageButtonsContainer(page_buttons_container, invoice_list_length);
                        fillPageButtonsContainer(page_buttons_container_top, invoice_list_length);
                    })
                }

                function switchPage(page_number) {

                        if(page_number < 0) page_number = pages_count;
                        else if(page_number > pages_count) page_number = 0;

                        setInvoicesForPage(page_number);

                        current_page_number = page_number;

                    }