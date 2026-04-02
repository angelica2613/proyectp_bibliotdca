// libros.js
// JavaScript for libros.html to handle table filtering and delete confirmations

document.addEventListener('DOMContentLoaded', () => {
  // === FILTRO DE BÚSQUEDA ===
  const table = document.querySelector('table');
  if (table) {
    const filterInput = document.createElement('input');
    filterInput.type = 'text';
    filterInput.placeholder =
      'Buscar libros por título, autor, ISBN o género...';
    filterInput.className =
      'w-full md:w-1/3 border-2 border-blue-300 p-3 rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500';

    // Insertar el input de filtro
    const contenedorLibros = document.querySelector('.contenedor-libros');
    if (contenedorLibros) {
      const flexContainer = contenedorLibros.querySelector(
        '.flex.justify-between'
      );
      if (flexContainer) {
        flexContainer.parentNode.insertBefore(
          filterInput,
          flexContainer.nextSibling
        );
      } else {
        contenedorLibros.insertBefore(filterInput, table);
      }
    } else {
      table.parentNode.insertBefore(filterInput, table);
    }

    // Evento de filtrado
    filterInput.addEventListener('input', () => {
      const filterValue = filterInput.value.toLowerCase();
      const rows = table.querySelectorAll('tbody tr');

      let visibleCount = 0;
      rows.forEach((row) => {
        // Verificar que no sea la fila de "No hay libros"
        if (row.cells.length < 9) return;

        // Índices: ID(0), Imagen(1), Título(2), Autor(3), Género(4), ISBN(5), Descripción(6), Disponible(7), Acciones(8)
        const titulo = row.cells[2]?.textContent.toLowerCase() || '';
        const autor = row.cells[3]?.textContent.toLowerCase() || '';
        const genero = row.cells[4]?.textContent.toLowerCase() || '';
        const isbn = row.cells[5]?.textContent.toLowerCase() || '';

        const matches =
          titulo.includes(filterValue) ||
          autor.includes(filterValue) ||
          isbn.includes(filterValue) ||
          genero.includes(filterValue);

        row.style.display = matches ? '' : 'none';
        if (matches) visibleCount++;
      });

      // Mostrar mensaje si no hay resultados
      const noResultsRow = table.querySelector('.no-results-row');
      if (visibleCount === 0 && filterValue.trim() !== '') {
        if (!noResultsRow) {
          const tbody = table.querySelector('tbody');
          const newRow = tbody.insertRow();
          newRow.className = 'no-results-row';
          const cell = newRow.insertCell();
          cell.colSpan = 9;
          cell.className =
            'border border-gray-300 p-4 text-gray-600 font-semibold text-center';
          cell.textContent = `No se encontraron libros que coincidan con "${filterValue}"`;
        }
      } else if (noResultsRow) {
        noResultsRow.remove();
      }
    });
  }
});

// === FUNCIÓN PARA ELIMINAR LIBRO (GLOBAL) ===
// Esta función se llama desde el onclick del botón en el HTML
function eliminarLibro(buttonElement) {
  const libroId = buttonElement.dataset.id;
  const libroTitulo = buttonElement.dataset.titulo;

  Swal.fire({
    title: '¿Estás seguro?',
    html: `¿Deseas eliminar el libro <strong>${escapeHtml(
      libroTitulo
    )}</strong>?<br><small class="text-gray-500">Esta acción no se puede deshacer</small>`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#dc2626',
    cancelButtonColor: '#6b7280',
    confirmButtonText: '<i class="fas fa-trash-alt mr-1"></i> Sí, eliminar',
    cancelButtonText: '<i class="fas fa-times mr-1"></i> Cancelar',
    background: '#fffef0',
    backdrop: 'rgba(0,0,0,0.4)',
    customClass: {
      confirmButton: 'px-6 py-2 rounded-full',
      cancelButton: 'px-6 py-2 rounded-full',
    },
  }).then((result) => {
    if (result.isConfirmed) {
      // Mostrar loading
      Swal.fire({
        title: 'Eliminando...',
        text: 'Por favor, espere.',
        icon: 'info',
        allowOutsideClick: false,
        allowEscapeKey: false,
        allowEnterKey: false,
        showConfirmButton: false,
        didOpen: () => {
          Swal.showLoading();
        },
      });

      // Redirigir al endpoint de eliminación
      window.location.href = `/entrepaginas/libros/eliminar/${libroId}`;
    }
  });
}

// === FUNCIÓN PARA ESCAPAR HTML ===
function escapeHtml(text) {
  if (!text && text !== 0) return '';
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

// === FUNCIÓN PARA MOSTRAR MENSAJES (UTILITY) ===
function showMessage(message, type = 'success') {
  Swal.fire({
    icon: type,
    title: type === 'success' ? '¡Éxito!' : 'Error',
    text: message,
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true,
    customClass: {
      popup: 'rounded-lg shadow-lg',
    },
  });
}

// Hacer la función global si se necesita en otros scripts
window.showMessage = showMessage;
