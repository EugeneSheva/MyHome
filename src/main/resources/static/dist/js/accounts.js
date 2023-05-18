let rows = document.getElementsByClassName("account_row");
    for(let i = 0; i < rows.length; i++) {
        let row_children = rows[i].children;
        let account_id = row_children[row_children.length-1].value;
        for(let j = 1; j < row_children.length - 2; j++) {
            row_children[j].addEventListener('click', function(){
                window.location.href = '/myhome/admin/accounts/'+account_id;
            });
        }
    }

function downloadExcelTable() {
    const date = new Date();

    let day = date.getDate();
    let month = date.getMonth() + 1;
    let year = date.getFullYear();

    let table = document.getElementById("accountsTable");
    let excel_file = XLSX.utils.table_to_book(table);
    XLSX.writeFile(excel_file, "account info " + `${day}-${month}-${year}` + ".xlsx");
}

function insertPreviousNumbers(page_number) {
        if(page_number-1 <= 1 || page_number === 1) return;

        if(page_number == pages_count && pages_count != 3) {
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

    if(page_number == 1 && pages_count > 3) {
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