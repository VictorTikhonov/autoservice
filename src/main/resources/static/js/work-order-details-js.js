let currentAutoGoodsPage = 0;
let currentServicesPage = 0;
const rowsPerPage = 7;
let autoGoodsTotalRows = 0;
let servicesTotalRows = 0;
let autoGoodsFilteredRows = [];
let servicesFilteredRows = [];


document.addEventListener('DOMContentLoaded', initializePagination);






function filterAutoGoodsByCategory() {
    const filterValue = document.getElementById("autoGoodCategoryFilter").value.toLowerCase();
    const table = document.getElementById("autoGoodsTable");
    const rows = Array.from(table.querySelector("tbody").rows);

    // Получаем только подходящие строки
    autoGoodsFilteredRows = rows.filter(row => {
        const categoryText = row.cells[2].textContent.trim().toLowerCase();
        return !filterValue || categoryText === filterValue;
    });

    // Сначала всё скрываем
    rows.forEach(row => row.style.display = 'none');

    // Сброс текущей страницы ПОСЛЕ фильтрации
    currentAutoGoodsPage = 0;

    const totalFiltered = autoGoodsFilteredRows.length;
    updatePagination('autoGoods', totalFiltered);
    updateTableDisplay('autoGoods'); // Обновляем отображение таблицы
}

function filterServicesByCategory() {
    const filterValue = document.getElementById("serviceCategoryFilter").value.toLowerCase();
    const table = document.getElementById("servicesTable");
    const rows = Array.from(table.querySelector("tbody").rows);

    servicesFilteredRows = rows.filter(row => {
        const categoryText = row.cells[2].textContent.trim().toLowerCase(); // 3-й столбец: категория
        return !filterValue || categoryText === filterValue;
    });

    rows.forEach(row => row.style.display = 'none');

    currentServicesPage = 0;

    const totalFiltered = servicesFilteredRows.length;
    updatePagination('services', totalFiltered);
    updateTableDisplay('services');
}

function searchByName(tableId, inputId) {
    const filter = document.getElementById(inputId).value.toLowerCase();
    const rows = document.getElementById(tableId).getElementsByTagName('tr');
    let filteredRows = [];

    for (let i = 1; i < rows.length; i++) {
        const cellText = rows[i].cells[1]?.textContent || ''; // ищем по имени товара или услуги
        if (cellText.toLowerCase().includes(filter)) {
            filteredRows.push(rows[i]);
            rows[i].style.display = ''; // показываем строку
        } else {
            rows[i].style.display = 'none'; // скрываем строку
        }
    }

    if (tableId === 'autoGoodsTable') {
        autoGoodsFilteredRows = filteredRows;
        autoGoodsTotalRows = filteredRows.length;
        currentAutoGoodsPage = 0; // сбрасываем на первую страницу
        updatePagination('autoGoods', autoGoodsTotalRows);

        // Сбросить фильтр категории на "все категории"
        document.getElementById("autoGoodCategoryFilter").value = "";

    } else if (tableId === 'servicesTable') {
        servicesFilteredRows = filteredRows;
        servicesTotalRows = filteredRows.length;
        currentServicesPage = 0; // сбрасываем на первую страницу
        updatePagination('services', servicesTotalRows);

        // Сбросить фильтр категории на "все категории"
        document.getElementById("serviceCategoryFilter").value = "";
    }


    updateTableDisplay(tableId === 'autoGoodsTable' ? 'autoGoods' : 'services');
}


function clearSearchServices() {
    document.getElementById("searchServices").value = "";  // Очистка поля поиска
    searchByName('servicesTable', 'searchServices');  // Вызов поиска с пустым значением
}

function clearSearchAutoGoods() {
    document.getElementById("searchAutoGoods").value = "";  // Очистка поля поиска
    searchByName('autoGoodsTable', 'searchAutoGoods');  // Вызов поиска с пустым значением
}






function initializePagination() {
    const autoGoodsRows = document.querySelectorAll('#autoGoodsTable tbody tr');
    const servicesRows = document.querySelectorAll('#servicesTable tbody tr');

    // Применяем фильтрацию
    autoGoodsFilteredRows = Array.from(autoGoodsRows);
    servicesFilteredRows = Array.from(servicesRows);

    autoGoodsTotalRows = autoGoodsFilteredRows.length;
    servicesTotalRows = servicesFilteredRows.length;

    // Прячем все строки перед загрузкой таблицы
    autoGoodsRows.forEach(row => row.style.display = 'none');
    servicesRows.forEach(row => row.style.display = 'none');

    updatePagination('autoGoods', autoGoodsTotalRows);
    updatePagination('services', servicesTotalRows);

    // Обновляем отображение первой страницы
    updateTableDisplay('autoGoods');
    updateTableDisplay('services');
}


function updatePagination(tableName, totalRows) {
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    const totalPagesId = tableName === 'autoGoods' ? 'totalAutoGoodsPages' : 'totalServicePages';
    const currentPageNumberId = tableName === 'autoGoods' ? 'currentAutoGoodsPageNumber' : 'currentServicePageNumber';

    document.getElementById(totalPagesId).innerText = totalPages;

    // Обновляем номер текущей страницы
    const currentPage = tableName === 'autoGoods' ? currentAutoGoodsPage : currentServicesPage;
    document.getElementById(currentPageNumberId).innerText = currentPage + 1;
}


function changePage(tableName, page) {
    // Сохраняем текущую позицию прокрутки
    const scrollPosition = window.scrollY;

    const totalRows = tableName === 'autoGoods' ? autoGoodsTotalRows : servicesTotalRows;
    const totalPages = Math.ceil(totalRows / rowsPerPage);

    if (page >= 0 && page < totalPages) {
        if (tableName === 'autoGoods') {
            currentAutoGoodsPage = page;
        } else {
            currentServicesPage = page;
        }

        // Обновляем таблицу, только скрывая и показывая строки
        updateTableDisplay(tableName);

        // Обновляем пагинацию (номер текущей страницы и количество страниц)
        updatePagination(tableName, totalRows);
        updatePaginationButtons(tableName, totalPages);

        // Восстанавливаем прокрутку вручную, только после обновления таблицы
        requestAnimationFrame(() => {
            window.scrollTo(0, scrollPosition);
        });
    }
}

function updateTableDisplay(tableName) {
    const startIndex = tableName === 'autoGoods' ? currentAutoGoodsPage * rowsPerPage : currentServicesPage * rowsPerPage;
    const endIndex = Math.min(startIndex + rowsPerPage, tableName === 'autoGoods' ? autoGoodsTotalRows : servicesTotalRows);

    const filteredRows = tableName === 'autoGoods' ? autoGoodsFilteredRows : servicesFilteredRows;

    // Скрываем все строки для текущей таблицы
    filteredRows.forEach(row => row.style.display = 'none');

    // Показываем только строки для текущей страницы
    filteredRows.slice(startIndex, endIndex).forEach(row => row.style.display = '');

    // Обновляем кнопки пагинации
    updatePaginationButtons(tableName, Math.ceil(filteredRows.length / rowsPerPage));
}


function updatePaginationButtons(tableName, totalPages) {
    const prevBtn = document.getElementById(tableName === 'autoGoods' ? 'prevAutoGoodsPage' : 'prevServicePage');
    const nextBtn = document.getElementById(tableName === 'autoGoods' ? 'nextAutoGoodsPage' : 'nextServicePage');
    const prevDisabled = document.getElementById(tableName === 'autoGoods' ? 'prevAutoGoodsPageDisabled' : 'prevServicePageDisabled');
    const nextDisabled = document.getElementById(tableName === 'autoGoods' ? 'nextAutoGoodsPageDisabled' : 'nextServicePageDisabled');

    const currentPage = tableName === 'autoGoods' ? currentAutoGoodsPage : currentServicesPage;

    if (currentPage > 0) {
        prevBtn.style.display = 'inline';
        prevDisabled.style.display = 'none';
    } else {
        prevBtn.style.display = 'none';
        prevDisabled.style.display = 'inline';
    }

    if (currentPage < totalPages - 1) {
        nextBtn.style.display = 'inline';
        nextDisabled.style.display = 'none';
    } else {
        nextBtn.style.display = 'none';
        nextDisabled.style.display = 'inline';
    }
}




// Автоматическая привязка для всех полей ввода
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('input[type="number"]').forEach((input) => {
        input.addEventListener('input', () => {
            validateInput(input);
            enableCheckbox(input);  // Валидация и включение/выключение чекбоксов для каждого поля с числовым вводом
        });
    });
});


// Валидации значений, вводимых в поля ввода типа number.
function validateInput(input) {
    let value = parseFloat(String(input.value)); // Текущее значение
    const max = parseFloat(input.getAttribute('max'));
    const min = parseFloat(input.getAttribute('min'));

    input.value = Math.max(Math.min(value, isNaN(max) ? Infinity : max), isNaN(min) ? 0 : min);
}


// Активации/деактивации чекбокса
function enableCheckbox(inputElement) {
    const checkbox = inputElement.closest('tr').querySelector('input[type="checkbox"]');

    // Если значение в поле больше или равно 0, активируем чекбокс, иначе деактивируем
    if (inputElement.value && parseFloat(inputElement.value) >= 0) {
        checkbox.disabled = false;
        checkbox.checked = true;
    } else {
        checkbox.disabled = true;
        checkbox.checked = false;
    }

    // Обновляем список выбранных товаров/услуг
    const type = checkbox.name.includes("AutoGoods") ? "AutoGoods" : "Services";
    updateSelectedItems(type);



    // Обработчик для снятия галочки
    checkbox.addEventListener('change', function () {
        if (!checkbox.checked) {
            // Очистить количество для автотовара или цену для услуги
            if (checkbox.name === "selectedAutoGoods") {
                const quantityInput = checkbox.closest('tr').querySelector("input[name='quantities']");
                quantityInput.value = '';
                quantityInput.disabled = false; // Разблокируем поле для ввода
            } else if (checkbox.name === "selectedServices") {
                const priceInput = checkbox.closest('tr').querySelector("input[name='price']");
                priceInput.value = '';
                priceInput.disabled = false; // Разблокируем поле для ввода
            }

            // Блокируем чекбокс, если количество или цена сброшены
            checkbox.disabled = true;

            // Обновляем список выбранных автотоваров или услуг
            if (checkbox.name === "selectedAutoGoods") {
                updateSelectedItems("AutoGoods");
            } else if (checkbox.name === "selectedServices") {
                updateSelectedItems("Services");
            }
        }
    });
}

// Обновление выбранных товаров или услуг
function updateSelectedItems(itemType) {
    const selectedItemsList = document.getElementById(`selected${itemType}List`);
    selectedItemsList.innerHTML = '';  // Очищаем список

    const checkboxes = document.querySelectorAll(`input[name="selected${itemType}"]:checked`);
    checkboxes.forEach((checkbox) => {
        const row = checkbox.closest('tr');
        const name = row.querySelector('td:nth-child(2)').textContent;
        let priceOrQuantity;

        if (itemType === "AutoGoods") {
            // Для автотоваров: извлекаем количество
            priceOrQuantity = parseFloat(row.querySelector('input[name="quantities"]').value);
        } else if (itemType === "Services") {
            // Для услуг: извлекаем цену
            priceOrQuantity = row.querySelector('input[name="price"]').value;
        }

        const listItem = document.createElement('li');
        listItem.textContent = `${name} (${itemType === "AutoGoods" ? "Кол-во" : "Цена"}: ${priceOrQuantity})`;
        selectedItemsList.appendChild(listItem);
    });
}


// Отправка выбранных автотоваров и услуг
function submitSelectedItems() {
    saveScrollPosition();
    const autoGoods = [];
    const services = [];

    // Скрываем сообщения об ошибках
    const errorMessageAutoGoodsOrService = document.getElementById('noAutoGoodsOrServiceMessage');
    errorMessageAutoGoodsOrService.style.display = 'none';

    // Собираем данные по автотоварам
    const autoGoodsCheckboxes = document.querySelectorAll("input[name='selectedAutoGoods']:checked");
    autoGoodsCheckboxes.forEach((checkbox) => {
        const row = checkbox.closest("tr");
        const autoGoodId = checkbox.value;
        const quantity = row.querySelector(".quantity-input").value;

        if (quantity > 0) {
            autoGoods.push({
                id: autoGoodId,
                quantity: quantity
            });
        }
    });

    // Собираем данные по услугам
    const serviceCheckboxes = document.querySelectorAll("input[name='selectedServices']:checked");
    serviceCheckboxes.forEach((checkbox) => {
        const row = checkbox.closest("tr");
        const serviceId = checkbox.value;
        const price = row.querySelector(".price-input").value;

        if (price >= 0) {
            services.push({
                id: serviceId,
                price: price
            });
        }
    });

    // Если оба списка пусты, показываем сообщение
    if (autoGoods.length === 0 && services.length === 0) {
        errorMessageAutoGoodsOrService.style.display = 'block'; // Показываем сообщение об ошибке
        return; // Останавливаем выполнение функции
    }

    const workOrderId = document.querySelector('input[name="workOrderId"]').value;

    // Формируем общий payload
    const payload = {
        workOrderId: workOrderId,
        autoGoodQuantity: autoGoods,
        servicePrice: services
    };

    fetch(`/work-order/details/add-items`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
    })
        .then((response) => {
            if (response.ok) {
                resetForms();
                window.location.href = `/work-order/details?workOrderId=${workOrderId}`;
            } else {
                console.error('Ошибка при добавлении данных');
            }
        })
        .catch((error) => {
            console.error('Ошибка при отправке данных:', error);
        });
}

// Сброс форм
function resetForms() {
    resetItemForm("selectedAutoGoods", ".quantity-input", "selectedAutoGoodsList");
    resetItemForm("selectedServices", ".price-input", "selectedServicesList");
}

function resetItemForm(itemName, inputSelector, listId) {
    document.querySelectorAll(`input[name='${itemName}']:checked`).forEach((checkbox) => {
        checkbox.checked = false;
        checkbox.disabled = true;
    });
    document.querySelectorAll(inputSelector).forEach((input) => {
        input.value = '';
    });
    document.getElementById(listId).innerHTML = '';
}


// Удаление автотоваров/услуг
function deleteItem(workOrderId, itemId, itemType) {
    const url = itemType === 'autoGood'
        ? `/work-order/details/delete-auto-good?workOrderId=${workOrderId}&autoGoodId=${itemId}`
        : `/work-order/details/delete-service?workOrderId=${workOrderId}&serviceId=${itemId}`;

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (response.ok) {
                location.reload(); // Перезагружаем страницу, чтобы обновить данные
            } else {
                alert(`Ошибка при удалении ${itemType === 'autoGood' ? 'автотовара' : 'услуги'}`);
            }
        })
        .catch(error => {
            console.error(`Ошибка при отправке запроса на удаление ${itemType}:`, error);
        });
}


// Сохранение позиции прокрутки
function saveScrollPosition() {
    sessionStorage.setItem('scrollPosition', window.scrollY);
}


// Восстановление позиции прокрутки
function restoreScrollPosition() {
    const scrollPosition = sessionStorage.getItem('scrollPosition');
    if (scrollPosition !== null) {
        window.scrollTo(0, parseInt(scrollPosition, 10));
        sessionStorage.removeItem('scrollPosition');
    }
}

window.addEventListener('load', restoreScrollPosition);