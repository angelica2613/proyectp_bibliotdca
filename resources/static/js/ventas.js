/**
 * Script para manejar interacciones en la página de gestión de ventas.
 * Principalmente, la función de confirmación de anulación usando SweetAlert2.
 */

// Función para confirmar la anulación de una venta
function confirmarAnulacion(idVenta, nombreCliente) {
    // 1. Muestra la ventana de confirmación de SweetAlert2
    Swal.fire({
        title: '¿Estás seguro de anular esta venta?',
        html: `Vas a anular la venta **ID ${idVenta}** realizada a **${nombreCliente}**. <br><br> Esta acción no se puede deshacer y el stock de libros será repuesto.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc2626', // Rojo para la anulación
        cancelButtonColor: '#1e40af',  // Azul para cancelar
        confirmButtonText: '<i class="fas fa-ban"></i> Sí, Anular Venta',
        cancelButtonText: '<i class="fas fa-times"></i> Cancelar'
    }).then((result) => {
        // 2. Procesa la respuesta del usuario
        if (result.isConfirmed) {
            // Si el usuario confirma, redirige al controlador de Spring Boot
            // El controlador debe estar mapeado a '/ventas/anular/{id}'
            window.location.href = '/ventas/anular/' + idVenta;
        }
    })
}

// ----------------------------------------------------------------------
// Función para mostrar mensajes de éxito o error (Opcional, pero recomendado)
// ----------------------------------------------------------------------
// Esta función lee los parámetros de la URL después de una redirección 
// y los muestra con SweetAlert2.

window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    const successMessage = urlParams.get('successMessage');
    const errorMessage = urlParams.get('errorMessage');

    if (successMessage) {
        // Muestra mensaje de éxito (ej: después de anular)
        Swal.fire({
            title: '¡Operación Exitosa!',
            text: successMessage,
            icon: 'success',
            confirmButtonColor: '#059669' // Verde
        });
        // Limpia el parámetro de la URL para que no se muestre al recargar
        history.replaceState(null, '', window.location.pathname); 
    }

    if (errorMessage) {
        // Muestra mensaje de error
        Swal.fire({
            title: '¡Error!',
            text: errorMessage,
            icon: 'error',
            confirmButtonColor: '#dc2626' // Rojo
        });
        // Limpia el parámetro de la URL
        history.replaceState(null, '', window.location.pathname);
    }
}