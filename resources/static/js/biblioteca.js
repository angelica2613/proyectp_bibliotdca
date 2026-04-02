// ======================================================================
// 1. VARIABLES GLOBALES
// ======================================================================

const poemas = [
    {
        titulo: "El Viento y el Libro",
        autor: "Anónimo",
        texto: "El viento pasó la página sin manos,\nDejó el susurro en el aire quieto.\nUn mundo azul se abrió bajo los vanos,\nDonde el silencio halló su alfabeto."
    },
    {
        titulo: "Lomo Azul",
        autor: "Lector Silencioso",
        texto: "En el estante, lomo de zafiro,\nPromesa azul de mundos que no acaban.\nCada letra es el tenue respiro\nDel alma que en sus páginas graban."
    },
    {
        titulo: "Café y Tinta",
        autor: "La Barista",
        texto: "Aroma amargo, sabor de la tinta,\nLa tarde cae y el papel se enciende.\nLa historia avanza, el corazón la pinta,\nY nadie sabe a dónde el sueño asciende."
    },
    {
        titulo: "La Llave",
        autor: "El Guardián",
        texto: "El libro es llave que abre más de cien puertas,\nNo a castillos de tierra o metal,\nSino a mundos que el alma lleva a cuestas,\nHechos de aire y tiempo atemporal."
    },

];

let prestamos = [];
let countdownIntervals = {};
let subscribers = JSON.parse(localStorage.getItem("subscribers")) || [];

// Variables para el control de TTS global (AÑADIDAS/AJUSTADAS)
let synth = window.speechSynthesis;
let currentUtterance = null;
let isSpeaking = false;


// ======================================================================
// 2. FUNCIONES DE UTILIDAD (TTS CORREGIDO)
// ======================================================================

function showNotification(message, isError = false) {
    const notification = document.getElementById("loan-notification");
    const notificationText = document.getElementById("notification-text");
    
    if (!notification || !notificationText) return; // Añadir chequeo

    notificationText.textContent = message;
    notification.className = "notification";
    notification.classList.add("show");
    if (isError) notification.classList.add("error");

    setTimeout(() => {
        notification.classList.remove("show");
        notification.classList.remove("error");
    }, 3000);
}

window.cerrarModalBiblioteca = function() {
    const modal = document.getElementById("modalBiblioteca");
    if (modal) modal.style.display = "none";
};

/**
 * Función centralizada para Texto a Voz (TTS).
 * AHORA maneja el estado del botón (Detener/Escuchar) y la interrupción.
 */
function speak(text, buttonElement = null) {
    if (!synth) {
        showNotification("Tu navegador no soporta Texto a Voz.", true);
        return;
    }

    if (synth.speaking) {
        synth.cancel();
        // Si ya estaba hablando el mismo texto, solo detenemos y salimos.
        if (isSpeaking && currentUtterance && currentUtterance.text === text) {
            isSpeaking = false;
            if (buttonElement) {
                buttonElement.innerHTML = '<i class="fas fa-volume-up"></i> Escuchar';
            }
            return;
        }
    }

    currentUtterance = new SpeechSynthesisUtterance(text);
    currentUtterance.lang = "es-ES";
    
    currentUtterance.onstart = () => {
        isSpeaking = true;
        if (buttonElement) {
            buttonElement.innerHTML = '<i class="fas fa-volume-mute"></i> Detener';
        }
    };

    currentUtterance.onend = () => {
        isSpeaking = false;
        if (buttonElement) {
            buttonElement.innerHTML = '<i class="fas fa-volume-up"></i> Escuchar';
        }
    };
    
    synth.speak(currentUtterance);
}


// ======================================================================
// 3. GESTIÓN DEL TEMA (MODO DÍA/NOCHE) (SIN CAMBIOS)
// ======================================================================

function initThemeToggle() {
    const toggle = document.getElementById("theme-toggle-input");
    
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme === "dark") {
        document.body.classList.add("dark-mode");
        if (toggle) toggle.checked = true;
    }

    if (toggle) {
        toggle.onchange = () => {
            document.body.classList.toggle("dark-mode");
            const isDark = document.body.classList.contains("dark-mode");
            localStorage.setItem("theme", isDark ? "dark" : "light");
        };
    }
}

// ======================================================================
// 4. CARRUSEL DE LIBROS DESTACADOS (CORREGIDO TTS)
// ======================================================================

let currentSlide = 0;
let slideInterval;
const INTERVALO_CARRUSEL_MS = 5000;

function showSlide(index) {
    const slides = document.querySelectorAll(".carousel-slide");
    if (slides.length === 0) return;

    currentSlide = (index + slides.length) % slides.length;

    slides.forEach((slide, i) => {
        slide.classList.toggle("active", i === currentSlide);
    });
}

function initCarousel() {
    const slides = document.querySelectorAll(".carousel-slide");
    const prevBtn = document.getElementById('carousel-prev-btn');
    const nextBtn = document.getElementById('carousel-next-btn');
    const ttsBtns = document.querySelectorAll('.carousel-slide .tts-btn');

    if (slides.length === 0 || !prevBtn || !nextBtn) return;

    if (slideInterval) clearInterval(slideInterval);

    function startAutoRotation() {
        slideInterval = setInterval(() => {
            showSlide(currentSlide + 1);
        }, INTERVALO_CARRUSEL_MS);
    }
    
    const handleManualNavigation = (direction) => {
        clearInterval(slideInterval);
        showSlide(currentSlide + direction);
        startAutoRotation();
    };

    prevBtn.onclick = () => handleManualNavigation(-1);
    nextBtn.onclick = () => handleManualNavigation(1);
    
    // CORRECCIÓN: Se pasa el elemento del botón a 'speak' para control de estado
    ttsBtns.forEach(button => {
        button.onclick = () => {
            const text = button.getAttribute('data-text');
            speak(text, button); 
        };
    });

    startAutoRotation();
    showSlide(currentSlide);
}

// ======================================================================
// 5. POEMAS ROTATIVOS (INTERVALO CORREGIDO)
// ======================================================================

function initPoemasRotativos() {
    const textoElemento = document.getElementById('poema-texto');
    const autorElemento = document.getElementById('poema-autor');
    const tituloElemento = document.getElementById('poema-titulo');

    if (!textoElemento || !autorElemento || !tituloElemento || poemas.length === 0) {
        if (poemas.length === 0 && tituloElemento && textoElemento) {
            tituloElemento.textContent = "Error de Poemas";
            textoElemento.textContent = "No hay poemas definidos.";
            textoElemento.classList.add('poema-error');
        }
        return;
    }

    let indicePoema = 0;
    // INTERVALO CORREGIDO A 15000 ms (15 segundos)
    const INTERVALO_MS = 15000; 

    function mostrarSiguientePoema() {
        const poemaActual = poemas[indicePoema];
        tituloElemento.textContent = poemaActual.titulo;
        // Convierte saltos de línea (\n) a etiquetas <br>
        textoElemento.innerHTML = poemaActual.texto.replace(/\n/g, '<br>');
        autorElemento.textContent = `- ${poemaActual.autor || 'Desconocido'}`; 
        textoElemento.classList.remove('poema-error');
        indicePoema = (indicePoema + 1) % poemas.length;
    }

    mostrarSiguientePoema();
    setInterval(mostrarSiguientePoema, INTERVALO_MS);
}

// ======================================================================
// 6. GESTIÓN DE PRÉSTAMOS - CON BACKEND (SIN CAMBIOS)
// ======================================================================

// Cargar préstamos desde el backend
async function cargarPrestamos() {
    try {
        const response = await fetch('/entrepaginas/prestamos');
        if (response.ok) {
            prestamos = await response.json();
            renderPrestamos();
        } else {
            console.error('Error al cargar préstamos. Status:', response.status);
            showNotification('Error al cargar los préstamos', true);
        }
    } catch (error) {
        console.error('Error en la petición de préstamos:', error);
        showNotification('Error de conexión al cargar préstamos', true);
    }
}

// Renderizar préstamos
window.renderPrestamos = function() {
    const prestamoGrid = document.getElementById("prestamo-grid");
    if (!prestamoGrid) return;
    
    // Detener todos los intervalos anteriores
    Object.values(countdownIntervals).forEach(clearInterval);
    countdownIntervals = {};
    
    prestamoGrid.innerHTML = "";
    
    const prestamosActivos = prestamos.filter(prestamo => !prestamo.devuelto);

    if (prestamosActivos.length === 0) {
        prestamoGrid.innerHTML = `<p class="text-center italic text-gray-500">No hay préstamos activos para mostrar.</p>`;
        return;
    }

    prestamosActivos.forEach(prestamo => {
        const card = document.createElement("div");
        card.classList.add("prestamo-card");
        card.innerHTML = `
            <p><strong>ID Libro:</strong> ${prestamo.idLibro}</p>
            <p><strong>Prestado a:</strong> ${prestamo.nombre}</p>
            <p><strong>Email:</strong> ${prestamo.email}</p>
            <p><strong>Fecha de Préstamo:</strong> ${new Date(prestamo.fechaPrestamo).toLocaleDateString('es-ES')}</p>
            <p><strong>Devolución:</strong> ${new Date(prestamo.fechaDevolucion).toLocaleDateString('es-ES')}</p>
            <p><strong>Tiempo Restante:</strong> <span id="countdown-${prestamo.id}" class="countdown"></span></p>
            <button onclick="devolverLibro(${prestamo.id})">Devolver</button>
        `;
        prestamoGrid.appendChild(card);
        updateCountdown(prestamo.id, prestamo.fechaDevolucion);
    });
}

function updateCountdown(idPrestamo, fechaDevolucion) {
    const countdownElement = document.getElementById(`countdown-${idPrestamo}`);
    if (!countdownElement) return;
    
    // Limpiar el intervalo anterior si existe
    if(countdownIntervals[idPrestamo]) clearInterval(countdownIntervals[idPrestamo]);

    const interval = setInterval(() => {
        const now = new Date().getTime(); // Usar getTime() para la resta
        const devolucion = new Date(fechaDevolucion).getTime();
        const diff = devolucion - now;

        if (diff <= 0) {
            clearInterval(interval);
            countdownElement.textContent = "VENCIDO";
            countdownElement.classList.add("expired"); // Añadir clase para estilos de vencido
            delete countdownIntervals[idPrestamo];
            return;
        }
        
        countdownElement.classList.remove("expired"); // Asegurarse de que no tenga la clase si no está vencido

        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((diff % (1000 * 60)) / 1000);
        countdownElement.textContent = `${days}d ${hours}h ${minutes}m ${seconds}s`;
    }, 1000);
    
    countdownIntervals[idPrestamo] = interval;
}

// Devolver libro - actualizar en backend
window.devolverLibro = async function(idPrestamo) {
    // Confirmación al usuario
    if (!confirm("¿Estás seguro de que quieres marcar este libro como devuelto?")) {
        return;
    }
    
    try {
        const response = await fetch(`/entrepaginas/prestamos/${idPrestamo}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ devuelto: true })
        });

        if (response.ok) {
            showNotification(`El libro (ID: ${idPrestamo}) ha sido devuelto con éxito.`);
            await cargarPrestamos(); // Recargar préstamos y actualizar la vista
        } else {
            const error = await response.json();
            showNotification(error.message || 'Error al devolver el libro', true);
        }
    } catch (error) {
        console.error('Error al devolver libro:', error);
        showNotification('Error de conexión al devolver libro', true);
    }
};

// Crear nuevo préstamo
window.crearPrestamo = async function(datosLibro) {
    const nombre = prompt("Ingresa el nombre de la persona:");
    const email = prompt("Ingresa el email:");
    
    if (!nombre || !email) {
        showNotification("Debes completar todos los datos", true);
        return;
    }

    const idLibro = datosLibro.id || 'N/A'; // Usar 'N/A' si el ID no está disponible
    
    // Validación básica de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showNotification("Email no válido.", true);
        return;
    }


    const fechaPrestamo = new Date();
    const fechaDevolucion = new Date();
    fechaDevolucion.setDate(fechaDevolucion.getDate() + 14); // 14 días

    const prestamo = {
        idLibro: idLibro,
        nombre: nombre,
        email: email,
        fechaPrestamo: fechaPrestamo.toISOString(),
        fechaDevolucion: fechaDevolucion.toISOString(),
        devuelto: false
    };

    try {
        const response = await fetch('/entrepaginas/prestamos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(prestamo)
        });

        if (response.ok) {
            showNotification(`Préstamo registrado para ${nombre}`);
            await cargarPrestamos(); // Recargar préstamos
        } else {
            const error = await response.json();
            showNotification(error.message || 'Error al crear préstamo', true);
        }
    } catch (error) {
        console.error('Error al crear préstamo:', error);
        showNotification('Error de conexión al crear préstamo', true);
    }
};

// ======================================================================
// 7. REGISTRO DE USUARIOS - CON BACKEND (SIN CAMBIOS)
// ======================================================================
document.addEventListener("DOMContentLoaded", () => {
  const registroForm = document.getElementById("registroForm");
  const registerModal = document.getElementById("register-modal");
  const closeBtn = document.querySelector(".close-modal");
  const notification = document.getElementById("register-notification");

  // Abrir modal
  const openRegisterModal = document.getElementById("register-link");
  if (openRegisterModal) {
    openRegisterModal.addEventListener("click", () => {
      //centrar modal en pantalla
        registerModal.style.display = "block";       
    });
  }

  // Cerrar modal
  if (closeBtn) {
    closeBtn.addEventListener("click", () => {
      registerModal.style.display = "none";
      notification.textContent = "";
      notification.className = "register-notification";
    });
  }

  // Enviar formulario
  if (registroForm) {
    registroForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const data = {
        correo: registroForm.email.value,
        contrasena: registroForm.password.value,
        rol: registroForm.rol ? registroForm.rol.value : "usuario" // valor por defecto
      };

      notification.textContent = "Registrando usuario...";
      notification.className = "register-notification";

      try {
        const response = await fetch("/entrepaginas/usuarios/registrar", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data)
        });

        const result = await response.json();

        if (response.ok) {
          Swal.fire({
            icon: "success",
            title: "Registro exitoso 🎉",
            text: result.message || "Usuario registrado correctamente.",
            confirmButtonColor: "#3085d6",
            confirmButtonText: "Aceptar"
          }).then(() => {
            registroForm.reset();
            registerModal.style.display = "none";
          });
        } else {
          Swal.fire({
            icon: "error",
            title: "Error al registrar",
            text: result.message || "Verifica los datos e inténtalo de nuevo.",
            confirmButtonColor: "#d33"
          });
        }
      } catch (error) {
        console.error("Error en la petición de registro:", error);
        Swal.fire({
          icon: "error",
          title: "Error de conexión",
          text: "No se pudo conectar con el servidor.",
          confirmButtonColor: "#d33"
        });
      }
    });
  }
});

// La función initRegistro se define fuera del DOMContentLoaded si se usa en otro lugar, 
// pero en tu código el listener hace la función de inicializador.


// ======================================================================
// 8. FUNCIONALIDADES DE ESCRITURA Y COMPARTIR (SIN CAMBIOS)
// ======================================================================

function initWriteShare() {
    const storyInput = document.getElementById('story-input');
    const saveDraftBtn = document.getElementById('save-draft');
    const publishStoryBtn = document.getElementById('publish-story');
    const ttsStoryBtn = document.getElementById('tts-story');
    
    // Cargar borrador al inicio si existe
    if (storyInput) {
        const draft = localStorage.getItem("draftStory");
        if (draft) {
            storyInput.value = draft;
            showNotification("Borrador cargado automáticamente.");
        }
    }


    if (saveDraftBtn) {
        saveDraftBtn.onclick = () => {
            if (storyInput && storyInput.value.trim()) {
                localStorage.setItem("draftStory", storyInput.value);
                showNotification("Borrador guardado localmente. ¡Continúa escribiendo!");
            } else {
                showNotification("No hay texto para guardar.", true);
            }
        };
    }

    if (publishStoryBtn) {
        publishStoryBtn.onclick = () => {
            if (storyInput && storyInput.value.trim()) {
                const historia = storyInput.value;
                
                // Simulación de publicación
                console.log("Historia publicada (simulación):", historia); 
                
                storyInput.value = "";
                localStorage.removeItem("draftStory");
                showNotification("¡Tu historia ha sido publicada!");
            } else {
                showNotification("Escribe algo antes de publicar.", true);
            }
        };
    }

    if (ttsStoryBtn) {
        ttsStoryBtn.onclick = () => {
            if (storyInput && storyInput.value.trim()) {
                // Se llama a speak, asumiendo que el botón es para control (aunque no se pasa aquí)
                speak(storyInput.value); 
            } else {
                showNotification("No hay texto para escuchar.", true);
            }
        };
    }
}

// ======================================================================
// 9. NEWSLETTER Y SUSCRIPTORES (SIN CAMBIOS)
// ======================================================================

function updateSubscriberList() {
    const subscriberList = document.getElementById('subscriber-list');
    if (!subscriberList) return;

    subscriberList.innerHTML = subscribers.map(email => `<li>${email}</li>`).join('');
}

function initNewsletter() {
    const newsletterForm = document.getElementById('newsletter-form');
    
    if (newsletterForm) {
        newsletterForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const emailInput = document.getElementById('newsletter-input');
            if (!emailInput) return; // Chequeo de elemento
            
            const email = emailInput.value.trim();
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Validación de email

            if (!email || !emailRegex.test(email)) {
                showNotification("Ingresa un email válido.", true);
                return;
            }

            if (subscribers.indexOf(email) === -1) {
                subscribers.push(email);
                localStorage.setItem("subscribers", JSON.stringify(subscribers));
                updateSubscriberList();
                showNotification(`¡Gracias por suscribirte con ${email}!`);
                emailInput.value = '';
            } else {
                showNotification("Ya estás suscrito.", true);
            }
        });
    }
}

// ======================================================================
// 10. MANEJO DE BÚSQUEDA Y MENÚ HAMBURGUESA (SIN CAMBIOS)
// ======================================================================

function initSearchAndNavigation() {
    const searchBtn = document.getElementById("search-btn");
    const hamburgerBtn = document.getElementById("hamburger-btn");

    if (searchBtn) {
        searchBtn.onclick = () => {
             showNotification("Búsqueda iniciada (función no implementada)", false); 
        };
    }

    if (hamburgerBtn) {
        hamburgerBtn.onclick = () => {
            const navLinks = document.querySelector(".nav-links");
            if (navLinks) navLinks.classList.toggle("active");
        };
    }
}

// ======================================================================
// 11. INICIALIZACIÓN DE LA PÁGINA (SIN CAMBIOS ESTRUCTURALES)
// ======================================================================

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Tema
    initThemeToggle();
    
    // 2. Registro (Si initRegistro existe fuera del listener, se llamaría aquí)
    // Se comenta ya que la lógica de registro está en el listener del punto 7.
    // initRegistro(); 
    
    // 3. Cargar y renderizar préstamos desde backend
    await cargarPrestamos();
    
    // 4. Poemas (AJUSTADO)
    initPoemasRotativos();
    
    // 5. Carrusel (AJUSTADO)
    initCarousel();
    
    // 6. Escritura
    initWriteShare();
    
    // 7. Newsletter
    initNewsletter();
    updateSubscriberList();
    
    // 8. Búsqueda y Navegación
    initSearchAndNavigation();
    
    // 9. Modal de Bienvenida
    const modal = document.getElementById("modalBiblioteca");
    if (modal) {
        setTimeout(() => {
            modal.style.display = "block";
        }, 1000);
    }
});