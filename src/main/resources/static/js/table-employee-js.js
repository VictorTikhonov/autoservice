document.addEventListener('DOMContentLoaded', function() {
    // ������������� ��� ��������
    initTable();

    // ���������� ������������ �������
    document.getElementById('searchByFio')?.addEventListener('input', searchTable);
    document.getElementById('searchByPhone')?.addEventListener('input', searchTable);
});

// ���������� ����������
let currentPage = 0;
const rowsPerPage = 5;
let totalRows = 0;
let allRows = [];

function initTable() {
    allRows = Array.from(document.querySelectorAll('#employeeTable tbody tr'));
    totalRows = allRows.length;
    filteredRows = [...allRows];
    updatePagination();
}

function searchTable() {
    const fioSearch = document.getElementById('searchByFio')?.value.toLowerCase() || '';
    const phoneSearch = document.getElementById('searchByPhone')?.value.toLowerCase() || '';

    filteredRows = allRows.filter(row => {
        const cells = row.cells;
        const surname = cells[1].textContent.toLowerCase();
        const name = cells[2].textContent.toLowerCase();
        const patronymic = cells[3].textContent.toLowerCase();
        const phone = cells[5].textContent.toLowerCase();

        // �������� ���������� �� ��� (����� �����)
        const fioMatch = !fioSearch ||
            surname.includes(fioSearch) ||
            name.includes(fioSearch) ||
            patronymic.includes(fioSearch);

        // �������� ���������� �� ��������
        const phoneMatch = !phoneSearch || phone.includes(phoneSearch);

        return fioMatch && phoneMatch;
    });

    totalRows = filteredRows.length;
    currentPage = 0;
    updatePagination();
}

function updatePagination() {
    // ���������� ����������� �������
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    document.getElementById('totalPages').textContent = totalPages || 1;
    document.getElementById('currentPageNumber').textContent = totalRows ? currentPage + 1 : 0;

    // ���������� ����������� �����
    updateTableDisplay();

    // ���������� ������ ���������
    updatePaginationButtons(totalPages);
}

function updateTableDisplay() {
    const start = currentPage * rowsPerPage;
    const end = start + rowsPerPage;

    // ������� �������� ��� ������
    allRows.forEach(row => row.style.display = 'none');

    // ���������� ������ ������ ������� ��������
    filteredRows.slice(start, end).forEach(row => {
        row.style.display = '';
    });
}

function updatePaginationButtons(totalPages) {
    const prevBtn = document.getElementById('prevPage');
    const nextBtn = document.getElementById('nextPage');
    const prevDisabled = document.getElementById('prevPageDisabled');
    const nextDisabled = document.getElementById('nextPageDisabled');

    if (!prevBtn || !nextBtn || !prevDisabled || !nextDisabled) return;

    // ��������� ������ "�����"
    if (currentPage <= 0 || totalPages === 0) {
        prevBtn.style.display = 'none';
        prevDisabled.style.display = 'inline';
    } else {
        prevBtn.style.display = 'inline';
        prevDisabled.style.display = 'none';
    }

    // ��������� ������ "������"
    if (currentPage >= totalPages - 1 || totalPages === 0) {
        nextBtn.style.display = 'none';
        nextDisabled.style.display = 'inline';
    } else {
        nextBtn.style.display = 'inline';
        nextDisabled.style.display = 'none';
    }
}

function changePage(newPage) {
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    if (newPage >= 0 && newPage < totalPages) {
        currentPage = newPage;
        updatePagination();
    }
}

function clearSearch() {
    document.getElementById("searchByFio").value = "";
    document.getElementById("searchByPhone").value = "";
    searchTable();
}