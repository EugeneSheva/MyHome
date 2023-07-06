function toggleDark() {
    let dark = JSON.parse(localStorage.getItem("jamesonDarkMode"))
    if (dark) {
        localStorage.setItem("jamesonDarkMode", JSON.stringify(false))
        console.log("Dark mode off")
    }
    else {
        localStorage.setItem("jamesonDarkMode", JSON.stringify(true))
        console.log("Dark mode on")
    }
}

function loadDark() {
    //default is light mode
    console.log("dark mode is ", JSON.parse(localStorage.getItem("jamesonDarkMode")))
    let dark = JSON.parse(localStorage.getItem("jamesonDarkMode"))
    if (dark === null) {
        localStorage.setItem("jamesonDarkMode", JSON.stringify(false))
    }
    else if (dark === true) {
        themeTogglerSwitch.innerHTML = '<i class="fa-solid fa-moon" style="font-size: 20px; margin-top: 5px; margin-left: 3px"></i>';
        themeTogglerSwitch.style.transform = 'translateX(140%)';
        themeToggler.classList.add('theme-dark');
        themeToggler.classList.remove('theme-light');
        themeTogglerSwitch.classList.add('theme-dark');
        themeTogglerSwitch.classList.remove('theme-light');

        nav.classList.add('theme-dark');
        nav.classList.remove('theme-light');

        document.body.classList.add('theme-dark');
        document.body.classList.remove('theme-light');
        document.body.classList.add('dark-mode');
        document.body.classList.remove('light-mode');

        sidebar.classList.add('sidebar-dark-secondary');
        sidebar.classList.remove('sidebar-light-primary');

        nav.classList.add('navbar-dark');
        nav.classList.remove('navbar-light');
        nav.classList.remove('navbar-white');

        for(const box of boxes) {
            box.classList.add('dark-box')
        }

        theme = 'dark';
    }
}

$(document).ready(function() {
    loadDark();
});


//TOGGLE THEME ON CLICK
let theme = "light";
let themeToggler = document.querySelector('.theme-toggler');
let themeTogglerSwitch = document.getElementById('theme-switch-button');
let nav = document.querySelector('#page-nav');
let sidebar = document.querySelector('#page-sidebar');
let footer = document.querySelector('#page-footer');
let boxes = document.getElementsByClassName('box');
console.log(boxes);
themeToggler.addEventListener('click', ()=>{

    toggleDark();
    if(theme === 'light') {
    // change toggler
        themeTogglerSwitch.innerHTML = '<i class="fa-solid fa-moon" style="font-size: 20px; margin-top: 5px; margin-left: 3px"></i>';
        themeTogglerSwitch.style.transform = 'translateX(140%)';
        themeToggler.classList.add('theme-dark');
        themeToggler.classList.remove('theme-light');
        themeTogglerSwitch.classList.add('theme-dark');
        themeTogglerSwitch.classList.remove('theme-light');

        nav.classList.add('theme-dark');
        nav.classList.remove('theme-light');

        document.body.classList.add('theme-dark');
        document.body.classList.remove('theme-light');
        document.body.classList.add('dark-mode');
        document.body.classList.remove('light-mode');

        sidebar.classList.add('sidebar-dark-secondary');
        sidebar.classList.remove('sidebar-light-primary');

        nav.classList.add('navbar-dark');
        nav.classList.remove('navbar-light');
        nav.classList.remove('navbar-white');

        for(const box of boxes) {
            box.classList.add('dark-box')
        }

        theme = 'dark';
    } else if(theme === 'dark') {
    // change toggler
        themeTogglerSwitch.innerHTML = '<i class="fa-solid fa-sun" style="font-size: 20px; margin-top: 5px; margin-left: 3px"></i>';
        themeTogglerSwitch.style.transform = 'translateX(0)';
        themeToggler.classList.add('theme-light');
        themeToggler.classList.remove('theme-dark');
        themeTogglerSwitch.classList.add('theme-light');
        themeTogglerSwitch.classList.remove('theme-dark');

        nav.classList.add('theme-light');
        nav.classList.remove('theme-dark');
        nav.classList.add('navbar-light');
        nav.classList.add('navbar-white');
        nav.classList.remove('navbar-dark');

        sidebar.classList.add('sidebar-light-primary');
        sidebar.classList.remove('sidebar-dark-secondary');

        document.body.classList.add('theme-light');
        document.body.classList.remove('theme-dark');
        document.body.classList.add('light-mode');
        document.body.classList.remove('dark-mode');

        for(const box of boxes) {
            box.classList.remove('dark-box')
        }

        theme = 'light';
    }
    var currentPagePath = window.location.pathname;
    if (currentPagePath.includes('statistics')) {
        drawChart()
        drawChart2()
    }

});
