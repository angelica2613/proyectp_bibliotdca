
document.addEventListener('DOMContentLoaded', () => {
    // Add filter input
    const table = document.querySelector('table');
    if (table) {
        const filterInput = document.createElement('input');
        filterInput.type = 'text';
        filterInput.placeholder = 'Buscar categorías por nombre...';
        filterInput.className = 'table-filter w-full md:w-1/4 border p-2 rounded mb-4';
        table.parentNode.insertBefore(filterInput, table);

        // Filter table
        filterInput.addEventListener('input', () => {
            const filterValue = filterInput.value.toLowerCase();
            const rows = table.querySelectorAll('tbody tr');
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(filterValue) ? '' : 'none';
            });
        });
    }

    // Confirm deletion
    const deleteLinks = document.querySelectorAll('a[href*="/categorias/eliminar/"]');
    deleteLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            if (!confirm('¿Está seguro de que desea eliminar esta categoría?')) {
                e.preventDefault();
            }
        });
    });
});

// Utility to show messages
function showMessage(message, type = 'success') {
    const msgDiv = document.createElement('div');
    msgDiv.className = `fixed top-4 right-4 p-4 rounded shadow-lg text-white ${type === 'success' ? 'bg-green-500' : 'bg-red-500'}`;
    msgDiv.textContent = message;
    document.body.appendChild(msgDiv);
    setTimeout(() => msgDiv.remove(), 3000);
}
window.showMessage = showMessage;