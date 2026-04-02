// ============================================================
// CARGA DE LIBROS DESDE BASE DE DATOS
// ============================================================

let librosFromDB = [];
let currentPage = 1;
const itemsPerPage = 9;

// Cargar libros desde el endpoint
async function cargarLibrosDesdeDB() {
  try {
    const response = await fetch('/entrepaginas/libros/listar');
    if (!response.ok) {
      throw new Error('Error al cargar los libros');
    }
    librosFromDB = await response.json();
    console.log('Libros cargados desde BD:', librosFromDB);
    renderCatalogo();
  } catch (error) {
    console.error('Error al cargar libros:', error);
    document.getElementById('book-grid').innerHTML =
      '<p class="text-center text-red-500">Error al cargar los libros. Por favor, intenta de nuevo.</p>';
  }
}

// Renderizar el catálogo con los libros de la BD
function renderCatalogo() {
  const grid = document.getElementById('book-grid');
  const categorySelect = document.getElementById('category-select');
  const selectedCategory = categorySelect ? categorySelect.value : '';

  // Filtrar por categoría
  let librosFiltrados = librosFromDB;
  if (selectedCategory) {
    librosFiltrados = librosFromDB.filter(
      (libro) =>
        libro.genero &&
        libro.genero.toLowerCase() === selectedCategory.toLowerCase()
    );
  }

  // Calcular paginación
  const totalPages = Math.ceil(librosFiltrados.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const librosPagina = librosFiltrados.slice(startIndex, endIndex);

  // Renderizar libros
  if (librosPagina.length === 0) {
    grid.innerHTML =
      '<p class="text-center">No hay libros disponibles en esta categoría.</p>';
  } else {
    grid.innerHTML = librosPagina
      .map((libro) => {
        // ✅ Generar ruta completa con context path /entrepaginas
        let imagenUrl = '/entrepaginas/images/book-default.png';

        if (libro.imagen && libro.imagen.trim() !== '') {
          const raw = libro.imagen.trim();

          if (/^https?:\/\//i.test(raw)) {
            imagenUrl = raw;
          } else if (raw.startsWith('/uploads/')) {
            imagenUrl = `/entrepaginas${raw}`;
          } else if (raw.startsWith('uploads/')) {
            imagenUrl = `/entrepaginas/${raw}`;
          } else {
            imagenUrl = `/entrepaginas/uploads/libros/${raw}`;
          }
        }

        const disponible =
          libro.disponible !== undefined ? libro.disponible : true;
        const estadoClase = disponible ? 'disponible' : 'no-disponible';
        const estadoTexto = disponible ? 'Disponible' : 'No Disponible';

        return `
        <div class="book-card">
          <img 
            src="${imagenUrl}" 
            alt="${libro.titulo}" 
            class="book-image"
            onerror="this.onerror=null;this.src='/entrepaginas/images/book-default.png';"
          />
          <h3>${libro.titulo}</h3>
          <p><strong>Autor:</strong> ${libro.autor || 'Desconocido'}</p>
          <p><strong>Género:</strong> ${libro.genero || 'Sin categoría'}</p>
          ${libro.isbn ? `<p><strong>ISBN:</strong> ${libro.isbn}</p>` : ''}
          <p class="${estadoClase}"><strong>Estado:</strong> ${estadoTexto}</p>
          ${
            libro.descripcion
              ? `<p class="descripcion">${libro.descripcion.substring(
                  0,
                  100
                )}${libro.descripcion.length > 100 ? '...' : ''}</p>`
              : ''
          }
          <button 
            class="btn-prestar ${!disponible ? 'disabled' : ''}" 
            onclick="solicitarPrestamo('${libro.titulo}', '${libro.autor}', ${libro.id})"
            ${!disponible ? 'disabled' : ''}
          >
            ${disponible ? 'Solicitar Préstamo' : 'No Disponible'}
          </button>
        </div>
      `;
      })
      .join('');
  }

  // Actualizar paginación
  actualizarPaginacion(totalPages);
}

// Actualizar controles de paginación
function actualizarPaginacion(totalPages) {
  const paginationNumbers = document.getElementById('pagination-numbers');
  const prevBtn = document.querySelector('.pagination-prev');
  const nextBtn = document.querySelector('.pagination-next');

  if (!paginationNumbers) return;

  paginationNumbers.innerHTML = '';

  for (let i = 1; i <= totalPages; i++) {
    const pageBtn = document.createElement('button');
    pageBtn.textContent = i;
    pageBtn.classList.add('pagination-number');
    if (i === currentPage) {
      pageBtn.classList.add('active');
    }
    pageBtn.addEventListener('click', () => {
      currentPage = i;
      renderCatalogo();
    });
    paginationNumbers.appendChild(pageBtn);
  }

  if (prevBtn) {
    prevBtn.disabled = currentPage === 1;
    prevBtn.onclick = () => {
      if (currentPage > 1) {
        currentPage--;
        renderCatalogo();
      }
    };
  }

  if (nextBtn) {
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.onclick = () => {
      if (currentPage < totalPages) {
        currentPage++;
        renderCatalogo();
      }
    };
  }
}

// Función para solicitar préstamo
function solicitarPrestamo(titulo, autor, libroId) {
  alert(`Solicitud de préstamo para: ${titulo} por ${autor} (ID: ${libroId})`);
  mostrarNotificacion(`Préstamo solicitado: ${titulo}`);
}

// Notificación de préstamo
function mostrarNotificacion(mensaje) {
  const notification = document.getElementById('loan-notification');
  const notificationText = document.getElementById('notification-text');

  if (notification && notificationText) {
    notificationText.textContent = mensaje;
    notification.classList.add('show');

    setTimeout(() => {
      notification.classList.remove('show');
    }, 3000);
  }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function () {
  cargarLibrosDesdeDB();

  const categorySelect = document.getElementById('category-select');
  if (categorySelect) {
    categorySelect.addEventListener('change', () => {
      currentPage = 1;
      renderCatalogo();
    });
  }
});
