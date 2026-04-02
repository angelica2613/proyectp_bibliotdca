// ======================================================================
// PERFILES.JS - Gestión de la tabla de perfiles
// ======================================================================

// ============================================================
// CONFIGURACIÓN DE RUTAS
// ============================================================
window.PERFILES_CONFIG = {
    baseUrl: '/perfiles',
    eliminarUrl: '/entrepaginas/perfiles/eliminar/',
    editarUrl: '/perfiles/editar/',
    nuevoUrl: '/perfiles/nuevo'
};

document.addEventListener('DOMContentLoaded', () => {
    // ============================================================
    // 1. FILTRO DE BÚSQUEDA EN LA TABLA
    // ============================================================
    const table = document.querySelector('table');
    if (table) {
        const filterInput = document.createElement('input');
        filterInput.type = 'text';
        filterInput.placeholder = 'Buscar perfiles por nombre...';
        filterInput.className = 'table-filter w-full md:w-1/4 border p-2 rounded mb-4';
        table.parentNode.insertBefore(filterInput, table);

        // Filtrar filas de la tabla
        filterInput.addEventListener('input', () => {
            const filterValue = filterInput.value.toLowerCase();
            const rows = table.querySelectorAll('tbody tr');
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(filterValue) ? '' : 'none';
            });
        });
    }

    // ============================================================
    // 2. CONFIRMACIÓN DE ELIMINACIÓN
    // ============================================================
    const deleteLinks = document.querySelectorAll(`a[href*="${window.PERFILES_CONFIG.eliminarUrl}"]`);
    deleteLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            if (!confirm('¿Está seguro de que desea eliminar este perfil? Esta acción no se puede deshacer.')) {
                e.preventDefault();
            }
        });
    });

    // ============================================================
    // 3. MOSTRAR MENSAJES FLASH (SUCCESS/ERROR) SI EXISTEN
    // ============================================================
    const urlParams = new URLSearchParams(window.location.search);
    const mensajeExito = urlParams.get('mensajeExito');
    const error = urlParams.get('error');
    
    if (mensajeExito) {
        showMessage(mensajeExito, 'success');
    }
    if (error) {
        showMessage(error, 'error');
    }
});

// ======================================================================
// FUNCIÓN UTILITARIA: MOSTRAR MENSAJES FLOTANTES
// ======================================================================
function showMessage(message, type = 'success') {
    const msgDiv = document.createElement('div');
    msgDiv.className = `fixed top-4 right-4 p-4 rounded shadow-lg text-white z-50 ${
        type === 'success' ? 'bg-green-500' : 'bg-red-500'
    }`;
    msgDiv.textContent = message;
    document.body.appendChild(msgDiv);
    
    // Auto-remover después de 3 segundos
    setTimeout(() => {
        msgDiv.style.opacity = '0';
        msgDiv.style.transition = 'opacity 0.5s';
        setTimeout(() => msgDiv.remove(), 500);
    }, 3000);
}

// Hacer la función global para que pueda ser llamada desde otros scripts
window.showMessage = showMessage;