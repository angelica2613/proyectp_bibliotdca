// usuarios.js
// JavaScript for usuarios.html to handle table filtering and delete confirmations
document.addEventListener('DOMContentLoaded', () => {
  // Add filter input
  const table = document.querySelector('table');
  if (table) {
    const filterInput = document.createElement('input');
    filterInput.type = 'text';
    filterInput.placeholder = 'Buscar usuarios por correo o rol...';
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

  // Eliminar el listener antiguo de confirmación nativa si ya no se usa
  // const deleteLinks = document.querySelectorAll('a[href*="/usuarios/eliminar/"]');
  // deleteLinks.forEach(link => {
  //     link.addEventListener('click', (e) => {
  //         if (!confirm('¿Está seguro de que desea eliminar este usuario?')) {
  //             e.preventDefault();
  //         }
  //     });
  // });
});

// === FUNCIÓN PARA ELIMINAR USUARIO (GLOBAL) ===
// Esta función se llama desde el onclick del botón en el HTML
function confirmarEliminacion(usuarioId, usuarioCorreo) {
  Swal.fire({
    title: '¿Estás seguro?',
    html: `¿Deseas eliminar al usuario <strong>${escapeHtml(
      usuarioCorreo
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
      // Redirigir a la URL de eliminación del controlador
      window.location.href = `/entrepaginas/usuarios/eliminar/${usuarioId}`;
    }
  });
}

// === FUNCIÓN PARA ESCAPAR HTML (UTILITY) ===
function escapeHtml(text) {
  if (!text && text !== 0) return ''; // Manejar valores nulos o vacíos
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

// Utility to show messages
function showMessage(message, type = 'success') {
  const msgDiv = document.createElement('div');
  msgDiv.className = `fixed top-4 right-4 p-4 rounded shadow-lg text-white ${
    type === 'success' ? 'bg-green-500' : 'bg-red-500'
  }`;
  msgDiv.textContent = message;
  document.body.appendChild(msgDiv);
  setTimeout(() => msgDiv.remove(), 3000);
}
window.showMessage = showMessage;
