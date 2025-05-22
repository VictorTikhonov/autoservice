let currentPage = 0;
const rowsPerPage = 7;
let totalRows = 0;
let filteredRows = [];

document.addEventListener('DOMContentLoaded', initializePagination);

function initializePagination() {
    const rows = Array.from(document.querySelectorAll('#servicesTable tbody tr'));
    filteredRows = rows.filter(row => !row.style.display || row.style.display === '');
    totalRows = filteredRows.length;
    updatePagination();
}

function updateTableDisplay() {
    // ������� �������� ��� ������
    const allRows = document.querySelectorAll('#servicesTable tbody tr');
    allRows.forEach(row => row.style.display = 'none');

    // ���������� ������ ������ ��� ������� ��������
    const startIndex = currentPage * rowsPerPage;
    const endIndex = Math.min(startIndex + rowsPerPage, totalRows);

    for (let i = startIndex; i < endIndex; i++) {
        if (filteredRows[i]) {
            filteredRows[i].style.display = '';
        }
    }

    document.getElementById('currentPageNumber').innerText = currentPage + 1;
}

function changePage(page, event) {
    if (event) event.preventDefault();  // �������� ������� �� ������

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

    // ������� �����
    if (currentPage > 0) {
        prevPage.style.display = 'inline';
        prevPageDisabled.style.display = 'none';
    } else {
        prevPage.style.display = 'none';
        prevPageDisabled.style.display = 'inline';
    }

    // ������� ������
    if (currentPage < totalPages - 1) {
        nextPage.style.display = 'inline';
        nextPageDisabled.style.display = 'none';
    } else {
        nextPage.style.display = 'none';
        nextPageDisabled.style.display = 'inline';
    }
}

function searchByName(tableId, inputId) {
    const filter = document.getElementById(inputId).value.toLowerCase();
    const rows = Array.from(document.getElementById(tableId).getElementsByTagName('tr'));

    // ���������� ��������� ������� (������ ������)
    filteredRows = rows.slice(1).filter(row => {
        const cellText = row.cells[1]?.textContent || '';
        return cellText.toLowerCase().includes(filter);
    });

    totalRows = filteredRows.length;
    currentPage = 0;
    updatePagination();
}

function updatePagination() {
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    document.getElementById('totalPages').innerText = totalPages;
    updateTableDisplay();
    updatePaginationButtons(totalPages);
}