let currentPage = 0;
const rowsPerPage = 2;
let totalRows = 0;
let filteredRows = [];
document.addEventListener('DOMContentLoaded', initializePagination);


function initializePagination() {
    const rows = document.querySelectorAll('#servicesTable tbody tr');
    filteredRows = Array.from(rows); // ��������� ��� ������ � ������
    totalRows = filteredRows.length; // ������� ��� ������
    updatePagination(totalRows); // ��������� ��������� ����� ��� �������������
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

    // ������� �����
    if (currentPage > 0) {
        prevPage.style.display = 'inline';  // ���������� �������� �������
        prevPageDisabled.style.display = 'none';  // �������� ���������� �������
    } else {
        prevPage.style.display = 'none';  // �������� �������� �������
        prevPageDisabled.style.display = 'inline';  // ���������� ���������� �������
    }

    // ������� ������
    if (currentPage < totalPages - 1) {
        nextPage.style.display = 'inline';  // ���������� �������� �������
        nextPageDisabled.style.display = 'none';  // �������� ���������� �������
    } else {
        nextPage.style.display = 'none';  // �������� �������� �������
        nextPageDisabled.style.display = 'inline';  // ���������� ���������� �������
    }
}


function searchByName(tableId, inputId) {
    const filter = document.getElementById(inputId).value.toLowerCase();
    const rows = document.getElementById(tableId).getElementsByTagName('tr');
    filteredRows = [];  // ������� ������ ��������������� �����

    for (let i = 1; i < rows.length; i++) {
        const cellText = rows[i].cells[0]?.textContent || '';
        if (cellText.toLowerCase().includes(filter)) {
            filteredRows.push(rows[i]);  // �������� ������ � ������ ���������������
        } else {
            rows[i].style.display = 'none';
        }
    }

    totalRows = filteredRows.length; // ������������� ���������� ����� ����� ����������
    currentPage = 0;  // ����������� �� ������ �������� ��� ��������� ������
    updatePagination(totalRows); // ��������� ��������� ����� ��������� �������
}


function updatePagination(totalRows) {
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    document.getElementById('totalPages').innerText = totalPages;
    updateTableDisplay();
    updatePaginationButtons(totalPages);
}