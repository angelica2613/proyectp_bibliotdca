// prestamos.js
// JavaScript for prestamos.html to handle table filtering and delete confirmations
document.addEventListener('DOMContentLoaded', () => {
  // Add filter input
  const table = document.querySelector('table');
  if (table) {
    const filterInput = document.createElement('input');
    filterInput.type = 'text';
    filterInput.placeholder = 'Buscar préstamos por cliente o libro...';
    filterInput.className =
      'table-filter w-full md:w-1/4 border p-2 rounded mb-4';
    table.parentNode.insertBefore(filterInput, table);

    // Filter table
    filterInput.addEventListener('input', () => {
      const filterValue = filterInput.value.toLowerCase();
      const rows = table.querySelectorAll('tbody tr');
      rows.forEach((row) => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(filterValue) ? '' : 'none';
      });
    });
  }

  // Confirm deletion
  const deleteLinks = document.querySelectorAll(
    'a[href*="/prestamos/eliminar/"]'
  );
  deleteLinks.forEach((link) => {
    link.addEventListener('click', (e) => {
      if (!confirm('¿Está seguro de que desea ELIMINAR este préstamo?')) {
        e.preventDefault();
      }
    });
  });
});

// Utility to show messages
function showMessage(message, type = 'success') {
  const msgDiv = document.createElement('div');
  msgDiv.className = `fixed top-4 right-4 p-4 rounded shadow-lg text-white ${type === 'success' ? 'bg-green-500' : 'bg-red-500'
    }`;
  msgDiv.textContent = message;
  document.body.appendChild(msgDiv);
  setTimeout(() => msgDiv.remove(), 3000);
}
window.showMessage = showMessage;

// Función para confirmar la devolución de un préstamo
function confirmarDevolucion(id, cliente, libro) {
  Swal.fire({
    title: '¿Estás seguro?',
    html: `¿Deseas DEVOLVER el préstamo del cliente <strong>${cliente}</strong> para el libro <strong>${libro}</strong>?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Sí, devolver',
    cancelButtonText: 'Cancelar',
  }).then((result) => {
    if (result.isConfirmed) {
      // Redirige directamente al endpoint del controlador para la devolución
      window.location.href = `/entrepaginas/prestamos/devolver/${id}`;
    }
  });
}

// Función para confirmar la eliminación de un préstamo
function confirmarEliminacion(id, cliente, libro) {
  Swal.fire({
    title: '¿Estás seguro?',
    html: `¡No podrás revertir esto! ¿Deseas ELIMINAR el préstamo del cliente <strong>${cliente}</strong> para el libro <strong>${libro}</strong>?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar',
  }).then((result) => {
    if (result.isConfirmed) {
      // Redirige directamente al endpoint del controlador para la eliminación
      window.location.href = `/entrepaginas/prestamos/eliminar/${id}`;
    }
  });
}
