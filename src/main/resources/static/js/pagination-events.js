function bindPaginationEvents() {
    document.querySelectorAll('.pagination-arrow a').forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            fetch(this.href)
                .then(response => response.text())
                .then(html => {
                    const parsed = new DOMParser().parseFromString(html, 'text/html');

                    document.querySelector('.table-wrapper').innerHTML =
                        parsed.querySelector('.table-wrapper').innerHTML;

                    document.querySelector('.pagination-container').innerHTML =
                        parsed.querySelector('.pagination-container').innerHTML;

                    bindPaginationEvents();
                });
        });
    });
}

document.addEventListener('DOMContentLoaded', bindPaginationEvents);
