    let currentPage = 0;
    const rowsPerPage = 5;
    let totalRows = 0;
    let filteredRows = [];
    document.addEventListener('DOMContentLoaded', initializePagination);


    function initializePagination() {
        const rows = document.querySelectorAll('#employeeTable tbody tr');
        filteredRows = Array.from(rows); // Сохраняем все строки в массив
        totalRows = filteredRows.length; // Считаем все строки
        updatePagination(totalRows); // Обновляем пагинацию сразу при инициализации
    }


    function updateTableDisplay() {
        const startIndex = currentPage * rowsPerPage;
        const endIndex = Math.min(startIndex + rowsPerPage, totalRows);

        filteredRows.forEach((row, index) => {
            if (index >= startIndex && index < endIndex) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        document.getElementById('currentPageNumber').innerText = currentPage + 1;
    }


    function changePage(page) {
        const totalPages = Math.ceil(totalRows / rowsPerPage);
        if (page >= 0 && page < totalPages) {
            currentPage = page;
            updateTableDisplay();
            updatePaginationButtons(totalPages);
        }
    }


    function updatePaginationButtons(totalPages) {
        const prevPage = document.getElementById('prevPage');
        const nextPage = document.getElementById('nextPage');
        const prevPageDisabled = document.getElementById('prevPageDisabled');
        const nextPageDisabled = document.getElementById('nextPageDisabled');

        // Стрелка назад
        if (currentPage > 0) {
            prevPage.style.display = 'inline';  // Показываем активную стрелку
            prevPageDisabled.style.display = 'none';  // Скрываем неактивную стрелку
        } else {
            prevPage.style.display = 'none';  // Скрываем активную стрелку
            prevPageDisabled.style.display = 'inline';  // Показываем неактивную стрелку
        }

        // Стрелка вперед
        if (currentPage < totalPages - 1) {
            nextPage.style.display = 'inline';  // Показываем активную стрелку
            nextPageDisabled.style.display = 'none';  // Скрываем неактивную стрелку
        } else {
            nextPage.style.display = 'none';  // Скрываем активную стрелку
            nextPageDisabled.style.display = 'inline';  // Показываем неактивную стрелку
        }
    }


    function searchTable() {
        const searchByFio = document.getElementById('searchByFio').value.toLowerCase();
        const searchByPhone = document.getElementById('searchByPhone').value.toLowerCase();
        const rows = document.querySelectorAll('#employeeTable tbody tr');
        filteredRows = [];  // Очищаем массив отфильтрованных строк

        rows.forEach(row => {
            const fioContent = `${row.cells[0].textContent.toLowerCase()} ${row.cells[1].textContent.toLowerCase()} ${row.cells[2].textContent.toLowerCase()}`;
            const phoneContent = row.cells[3].textContent.trim();

            if ((fioContent.includes(searchByFio) || !searchByFio) && (phoneContent.startsWith(searchByPhone) || !searchByPhone)) {
                filteredRows.push(row);  // Добавляю строку в массив отфильтрованных
            } else {
                row.style.display = 'none';
            }
        });

        totalRows = filteredRows.length; // Пересчитываем количество строк после фильтрации
        currentPage = 0;  // Возвращаюсь на первую страницу при изменении поиска
        updatePagination(totalRows); // Обновляем пагинацию после изменения фильтра
    }


    function updatePagination(totalRows) {
        const totalPages = Math.ceil(totalRows / rowsPerPage);
        document.getElementById('totalPages').innerText = totalPages;
        updateTableDisplay();
        updatePaginationButtons(totalPages);
    }